package io.cobla.core.service.impl;

import com.google.gson.Gson;
import io.cobla.core.domain.repository.ApiWalletMonitorRepository;
import io.cobla.core.domain.rpc.ApiWalletMonitor;
import io.cobla.core.dto.ResultDto;
import io.cobla.core.dto.rpc.ApiWalletMonitorReqDto;
import io.cobla.core.dto.rpc.RpcReqDto;
import io.cobla.core.service.ParityRpcService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableScheduling
@Service
public class ParityRpcServiceImpl implements ParityRpcService {

    @Value("${block.tracer.parity.rpc.host}")
    private String rpcHost;

    @Value("${block.tracer.parity.rpc.port}")
    private String rpcPort;

    @Value("${block.tracer.parity.rpc.get.balance}")
    private String balanceMethod;

    @Autowired
    ApiWalletMonitorRepository apiWalletMonitorRepository;


    private final BigInteger divideBase = new BigInteger("1000000000000000000");

    @Override
    public ResultDto getBalance(HashMap<String, String> params) throws IOException {

        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);
        ResultDto result = new ResultDto();

        RpcReqDto req = new RpcReqDto();
        req.setParams(new String[]{params.get("addr")});
        req.setMethod(this.balanceMethod);

        StringEntity rcpParma =new StringEntity(new Gson().toJson(req));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rcpParma);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");

        RpcReqDto data =  new Gson().fromJson(rpcResult,RpcReqDto.class);

        if(data.getResult()==null){
            result.defaultFail(params.get("addr")+ " Is not correct eth address!!");
            return result;
        }


        BigInteger balance = new BigInteger(data.getResult().substring(2), 16);
        BigInteger[] divideBalance = balance.divideAndRemainder(divideBase);

        String returnBalance = divideBalance[0].toString()+"."+divideBalance[1].toString();
        result.setResult_text(returnBalance);

        return result;
    }

    @Override
    public ResultDto addEthMonitorTarget(ApiWalletMonitorReqDto reqDto) throws IOException {

        reqDto.setMonitorReqTime(LocalDateTime.now());
        HashMap<String,String> param = new HashMap<String,String>();
        param.put("addr",reqDto.getAddr());

        ResultDto txCountResult = this.getSendTxCount(param);
        if("0".equals(txCountResult.getResult_code())) {
            reqDto.setTxCount(Integer.parseInt(txCountResult.getResult_text()));
         }else{
            return  txCountResult;
        }

        ResultDto balanceResult =this.getBalance(param);

        if("0".equals(balanceResult.getResult_code())) {
            reqDto.setBalance(Double.valueOf(balanceResult.getResult_text()));
            reqDto.setLastTxTime(LocalDateTime.now());
            apiWalletMonitorRepository.save(reqDto.toEntity());
            balanceResult.setResult_text("add success");
        }

        return balanceResult;
    }

    @Scheduled(cron ="0/60 * * * * ?")
    @Override
    public void ethMonitorSendTx() throws IOException {

        //모니터링 대상 조회
        List<ApiWalletMonitor> monitorList= this.getEthMonitorList();

        for(ApiWalletMonitor monitor : monitorList){

            //모니터링 사이클에 맞지 않으면 제외
            Double nowSecond = Double.valueOf(LocalDateTime.now().getSecond());
            int mod = LocalDateTime.now().getSecond()%monitor.getMonitor_cycle_sec();
            if(!(mod==0)) continue;

            HashMap<String,String> reqParam = new HashMap<String,String>();
            reqParam.put("addr",monitor.getId().getAddr());
            ResultDto result= this.getSendTxCount(reqParam);

            int resultTx = Integer.parseInt(result.getResult_text());

            if(monitor.getTx_count() != resultTx ){
                monitor.setTx_count(resultTx);
                monitor.setLast_tx_time(LocalDateTime.now());
                apiWalletMonitorRepository.save(monitor);
            }
        }
    }

    @Override
    public List<ApiWalletMonitor> getEthMonitorList() {

        List<ApiWalletMonitor> result = apiWalletMonitorRepository.findAll();

        Stream<ApiWalletMonitor> monitorTargetData =  result.stream().filter(dto ->
             "Y".equals(dto.getMonitor_yn())
        );

        Stream<ApiWalletMonitor> effectiveData = monitorTargetData.filter(dto ->{

            boolean isExpirationData = LocalDateTime.now().isAfter(dto.getMonitor_req_time().plusHours(dto.getMonitor_effective_time()));

            //모니터링 유효시간 초과시 상태변경
            if(isExpirationData){
                dto.setMonitor_yn("N");
                apiWalletMonitorRepository.save(dto);
            }
            return !isExpirationData;
        });


        return effectiveData.collect(Collectors.toList());
    }

    @Override
    public ResultDto getSendTxCount(HashMap<String, String> params) throws IOException {
        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);

        RpcReqDto req = new RpcReqDto();
        ResultDto result = new ResultDto();

        req.setParams(new String[]{params.get("addr"),"latest"});
        req.setMethod("eth_getTransactionCount");

        StringEntity rpcParams =new StringEntity(new Gson().toJson(req));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");

        RpcReqDto data =  new Gson().fromJson(rpcResult,RpcReqDto.class);

        if(data.getResult()==null){
            result.defaultFail(params.get("addr")+ " Is not correct eth address!!");
            return result;
        }

        BigInteger txCount = new BigInteger(data.getResult().substring(2), 16);
        result.setResult_text(txCount.toString());
        return result;
    }
}
