package io.cobla.core.controller;

import com.google.gson.Gson;
import io.cobla.core.dto.ResultDto;
import io.cobla.core.dto.rpc.ApiWalletMonitorReqDto;
import io.cobla.core.dto.rpc.RpcReqDto;
import io.cobla.core.service.ParityRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;



@RestController
public class ParityRcpController {



    @Autowired
    ParityRpcService parityRpcService;

    //@RequestMapping(value = "/v1/address/balance", method = RequestMethod.POST,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @PostMapping("/v1/address/balance")
    public ResponseEntity<ResultDto> ethGetBalance(@RequestBody HashMap<String,String> params) throws IOException {

        ResultDto result = parityRpcService.getBalance(params);

        return ResponseEntity.ok(result);
    }


     //@Scheduled(cron ="0/5 * * * * ?")
     public ResponseEntity<RpcReqDto> ethMoniterSendTx() throws IOException {

//        HttpPost request = new HttpPost("http://"+rpcHost+":"+rpcPort);
//
//
//        RpcReqDto req = new RpcReqDto();
//        req.setParams(new String[]{"0x915d7915f2b469bb654A7D903A5d4417Cb8eA7Df","latest"});
//        req.setMethod("eth_getTransactionCount");
//
//        StringEntity params =new StringEntity(new Gson().toJson(req));
//
//        request.addHeader("Content-type", "application/json");
//        request.setEntity(params);
//
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        CloseableHttpResponse httpResponse = httpClient.execute(request);
//
//        String result = EntityUtils.toString(httpResponse.getEntity()).replace("\n","");
//
//        RpcReqDto data =  new Gson().fromJson(result,RpcReqDto.class);
//        BigInteger balance = new BigInteger(data.getResult().substring(2), 16);
//
//        data.setResult(balance.toString());
//        System.out.println(data.getResult());

        return ResponseEntity.ok().build();
    }


    /**
     * 모니터링 요청대상 등록
     * @param reqDto
     * @return
     */
    @PutMapping("/wallet/monitor")
    public ResponseEntity<ResultDto> addEthMonitorTarget(@RequestBody  ApiWalletMonitorReqDto reqDto)  {
        ResultDto result = new ResultDto();

        try {
            result = parityRpcService.addEthMonitorTarget(reqDto);
        } catch (IOException e) {
            result.defaultFail("add monitor fail!!");
        }

        return ResponseEntity.ok(result);
    }



    @PostMapping("/wallet/monitor")
    public ResponseEntity<ApiWalletMonitorReqDto> ethMonitor(@RequestBody  ApiWalletMonitorReqDto reqDto) {

        reqDto.setMonitorReqTime(LocalDateTime.now());
        //List<ApiWalletMonitor> result = apiWalletMonitorRepository.findAll();
        return ResponseEntity.ok(reqDto);
    }

    @PostMapping("/test/elastic")
    public String convertTransaction(@RequestBody HashMap<String, String> params ) throws IOException {


        String txData = parityRpcService.getTransactionByHash(params);
        parityRpcService.transactionInElastic(txData);

        return new Gson().toJson("");
    }


}
