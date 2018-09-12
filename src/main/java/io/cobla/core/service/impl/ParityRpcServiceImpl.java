package io.cobla.core.service.impl;

import com.google.gson.Gson;
import io.cobla.core.domain.repository.ApiWalletMonitorRepository;
import io.cobla.core.domain.rpc.ApiWalletMonitor;
import io.cobla.core.dto.ResultDto;
import io.cobla.core.dto.rpc.*;
import io.cobla.core.service.ElasticSearchService;
import io.cobla.core.service.ParityRpcService;
import io.cobla.core.util.EthDateUtil;
import io.cobla.core.util.EthNumberUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@EnableScheduling
@Service
public class ParityRpcServiceImpl implements ParityRpcService {

    @Value("${block.tracer.parity.rpc.host}")
    private String rpcHost;

    @Value("${block.tracer.elk.host}")
    private String elkHost;

    @Value("${block.tracer.parity.rpc.port}")
    private String rpcPort;

    @Value("${block.tracer.elk.port}")
    private String elkPort;

    @Value("${block.tracer.parity.rpc.get.balance}")
    private String balanceMethod;

    @Value("${block.tracer.parity.rpc.block.number}")
    private String lastBlockNumberMethode;

    @Autowired
    ApiWalletMonitorRepository apiWalletMonitorRepository;

    @Autowired
    ElasticSearchService elkService;

    @Value("${block.tracer.parity.rpc.divide.value}")
    private String divideBase;

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

        BigDecimal divideValue = new BigDecimal(balance,balance.compareTo(BigInteger.ZERO)==0 ? 0 : 18);

        String returnBalance = divideValue.toString();
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
            reqDto.setLastTxTime(reqDto.getMonitorReqTime());
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

                ResultDto balance = this.getBalance(reqParam);
                monitor.setBalance(Double.valueOf(balance.getResult_text()));
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

    @Override
    public String getTransactionByHash(HashMap<String, String> params) throws IOException {

        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);

        RpcReqDto req = new RpcReqDto();
        ResultDto result = new ResultDto();

        req.setParams(new String[]{params.get("hash")});
        req.setMethod("eth_getTransactionByHash");

        StringEntity rpcParams =new StringEntity(new Gson().toJson(req));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");
        return rpcResult;
    }

    @Override
    public String transactionInElastic(String txData) throws IOException {


      /*  HttpPost request = new HttpPost("http://"+rpcHost+":"+"9200/eth/node");

        ResultDto result = new ResultDto();

        StringEntity rpcParams =new StringEntity(txData);

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);*/

        return null;
    }

    @Scheduled(cron ="0/1 * * * * ?")
    @Override
    public String getEtherBlockNumber() throws IOException {

        String procBlockNumber =null;
        String blockHex = null;

        try {

            RpcReqDto blockNumberReqDto = new RpcReqDto();
            blockNumberReqDto.setMethod(this.lastBlockNumberMethode);

            //node 의 현재 블록넘버 조회
            RpcReqDto nodeData_blockNumber = this.callParityRpc(blockNumberReqDto, RpcReqDto.class);

            String nodeBlockNumber = EthNumberUtil.hexToNumber(nodeData_blockNumber.getResult());

            String elkBlockNumber = elkService.getMaxEthBlockNumber();

            //elk의 현재 블록과 node의 블록 넘버가 같으면 pass
            if (Integer.parseInt(nodeBlockNumber) == Integer.parseInt(elkBlockNumber)) {
                return "pass";
            }

            procBlockNumber = String.valueOf(Integer.parseInt(elkBlockNumber) + 1);


            //elk 에 저장된 blockNumber 조회

            String query = procBlockNumber;
            String searchUri = elkService.makeElasticUri("check/block", query);

            HashMap<String, Object> checkBlock = elkService.elasticHttpGet(searchUri, HashMap.class);

            String check = checkBlock.get("found").toString();
            //node의 블록이 elk에 있으면 pass

            if (Boolean.valueOf(check)) {
                return "pass";
            }

            //blockNumber 저장
            String blockSaveUri = elkService.makeElasticUri("check/block", procBlockNumber);
            EthBlockNumberInsDto blockNumberInsDto = new EthBlockNumberInsDto();
            blockNumberInsDto.setInsert(true);

            blockNumberInsDto.setBlockNumber(Integer.parseInt(procBlockNumber));
            elkService.elasticHttpPost(blockSaveUri, blockNumberInsDto);

            //이더리움 블록 데이터 조회
            BigInteger bi = new BigInteger(procBlockNumber);
            blockHex ="0x" + bi.toString(16);
            List<EthTxInsDto> insDtoList = this.getEthBlockDataByNumber(blockHex);


            //ELK 호출하여 데이터 저장
            //데이터 저장시에는 도큐먼트에 트랜잭션과 블록단위의 데이터를 전부 넣는다
            elkService.addEthTxBulk(insDtoList);


        }catch(Exception e){

            log.error("Error Block:"+procBlockNumber+" $$ Block Hex:"+blockHex);

            String blockSaveUri = elkService.makeElasticUri("error/block", procBlockNumber);
            EthBlockNumberInsDto blockNumberInsDto = new EthBlockNumberInsDto();
            blockNumberInsDto.setInsert(true);

            blockNumberInsDto.setBlockNumber(Integer.parseInt(procBlockNumber));
            elkService.elasticHttpPost(blockSaveUri, blockNumberInsDto);
         }
        return "success";
    }

    @Override
    public void addEthTxToElk(EthTxInsDto insDto) throws IOException {

        HttpPost request = new HttpPost("http://"+rpcHost+":"+elkPort+"/eth/transaction/"+insDto.getHash());

        StringEntity rpcParams =new StringEntity(new Gson().toJson(insDto));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

    }

    @Override
    public List<EthTxInsDto> getEthBlockDataByNumber(String hexBlockNumber) throws IOException {
            RpcReqDto transactionReqDto = new RpcReqDto();

            transactionReqDto.setMethod("eth_getBlockByNumber");
            Object[] params = {hexBlockNumber,true};
            transactionReqDto.setParams(params);

            //Ethereum block 정보 취득
            EthBlockDto ethBlock = this.callParityRpc(transactionReqDto,EthBlockDto.class);

            EthBlockDataDto blockData = ethBlock.getResult();

            blockData.setGasLimit(EthNumberUtil.hexToNumber(blockData.getGasLimit()));
            blockData.setGasUsed(EthNumberUtil.hexToNumber(blockData.getGasUsed()));
            blockData.setNonce(EthNumberUtil.hexToNumber(blockData.getNonce()));
            blockData.setNumber(EthNumberUtil.hexToNumber(blockData.getNumber()));
            blockData.setDifficulty(EthNumberUtil.hexToNumber(blockData.getDifficulty()));

            //도큐먼트 생성 DTO
            List<EthTxInsDto> insDtoList = new ArrayList<EthTxInsDto>();
            List<EthTxDto> txList = ethBlock.getResult().getTransactions();
            txList.forEach(tx ->{
                //Block Data Mapping
                EthTxInsDto insDto = new EthTxInsDto();
                insDto.setAuthor(blockData.getAuthor());
                insDto.setDifficulty(blockData.getDifficulty());
                insDto.setExtraData(blockData.getExtraData());
                insDto.setGasLimit(blockData.getGasLimit());
                insDto.setGasUsed(blockData.getGasUsed());
                insDto.setBlockHash(blockData.getHash());
                insDto.setLogsBloom(blockData.getLogsBloom());
                insDto.setMiner(blockData.getMiner());
                insDto.setMixHash(blockData.getMixHash());
                insDto.setBlockNonce(blockData.getNonce());
                insDto.setBlockNumber(blockData.getNumber());
                insDto.setParentHash(blockData.getParentHash());
                insDto.setReceiptsRoot(blockData.getReceiptsRoot());
                insDto.setSha3Uncles(blockData.getSha3Uncles());
                insDto.setSize(EthNumberUtil.hexToNumber(blockData.getSize()));
                insDto.setTimestamp(EthDateUtil.hexToDate(blockData.getTimestamp(),"UTC"));
                insDto.setTotalDifficulty(EthNumberUtil.hexToNumber(blockData.getTotalDifficulty()));
                insDto.setTransactionsRoot(blockData.getTransactionsRoot());


                //Tx Data Mapping
                insDto.setChainId(tx.getChainId());
                insDto.setCondition(tx.getCondition());
                insDto.setCreates(tx.getCreates());
                insDto.setFrom(tx.getFrom());
                insDto.setGas(EthNumberUtil.hexToNumber(tx.getGas()));
                insDto.setGasPrice(EthNumberUtil.hexToGasPrice(tx.getGasPrice()));
                insDto.setHash(tx.getHash());
                insDto.setInput(tx.getInput());
                insDto.setNonce(EthNumberUtil.hexToNumber(tx.getNonce()));
                insDto.setPublicKey(tx.getPublicKey());
                insDto.setR(tx.getR());
                insDto.setRaw(tx.getRaw());
                insDto.setS(tx.getS());
                insDto.setStandardV(tx.getStandardV());
                insDto.setTo(tx.getTo());
                insDto.setTransactionIndex(EthNumberUtil.hexToNumber(tx.getTransactionIndex()));
                insDto.setV(tx.getV());
                insDto.setValue(EthNumberUtil.hexToRealNumber(tx.getValue()));

                insDtoList.add(insDto);
            });


        return insDtoList;
    }

    public <T> T  callParityRpc(RpcReqDto params, Class<T> classOfT ) throws IOException {
        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);

        StringEntity rpcParams =new StringEntity(new Gson().toJson(params));

        request.addHeader("Content-type", "application/json");
        request.setEntity(rpcParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        String rpcResult = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");

        T result =  new Gson().fromJson(rpcResult,classOfT);
        return result;
    }


}
