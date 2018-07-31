package io.cobla.core.service.impl;

import com.google.gson.Gson;
import io.cobla.core.domain.ApiCollect;
import io.cobla.core.domain.ApiWalletSave;
import io.cobla.core.domain.ApiWalletTransactionEther;
import io.cobla.core.domain.repository.ApiCollectRepository;
import io.cobla.core.domain.repository.ApiWalletSaveRepository;
import io.cobla.core.domain.repository.ApiWalletTransactionRepository;
import io.cobla.core.dto.*;
import io.cobla.core.service.CoblaRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CoblaRestServiceImpl implements CoblaRestService {

    @Autowired
    ApiWalletTransactionRepository apiWalletTransactionRepository;

    @Autowired
    ApiWalletSaveRepository walletSaveRepository;

    @Autowired
    ApiCollectRepository apiCollectRepository;

    @Value("${etherscan.api.key}")
    private String apiKey;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;


    @Override
    public String buildEtherScanAccountUri(ApiWalletTransactionReqDto param) {


        String module = "account";
        String action = "tokentx";
        String address =param.getAddr();
        String startBlock = "0";
        String endBlock ="99999999";
        String sort = "asc";

        StringBuffer sb = new StringBuffer();

        sb.append("http://api.etherscan.io/api?");
        sb.append("module=");
        sb.append(module);
        sb.append("&action=");
        sb.append(action);
        sb.append("&address=");
        sb.append(address);
        sb.append("&startblock=");
        sb.append(startBlock);
        sb.append("&endblock=");
        sb.append(endBlock);
        sb.append("&sort=");
        sb.append(sort);
        sb.append("&apikey=");
        sb.append(apiKey);

        return sb.toString();
    }

    @Override
    public ArrayList<ApiWalletTransactionReqDto> addWalletTransaction(EtherScanDto etherTxData ) {

        ArrayList<ApiWalletTransactionEther>  inDataList = new ArrayList<ApiWalletTransactionEther>();

        ArrayList<ApiWalletTransactionReqDto> result = new ArrayList<ApiWalletTransactionReqDto>();

        for(ApiWalletTransactionEtherDto data : etherTxData.getResult()){
            ApiWalletTransactionEther indata = data.toEntity();
            inDataList.add(indata);

            ApiWalletTransactionReqDto outData = new ApiWalletTransactionReqDto();
            outData.setAddr(indata.getAddr_to());

            result.add(outData);
        }

        apiWalletTransactionRepository.saveAll(inDataList);

        return result ;
    }

    @Override
    public ResultDto addBlackWallet(MultipartFile file)  throws Exception{
        ResultDto result = new ResultDto();
        InputStreamReader isr = null;
        BufferedReader buffer = null;
        String read = null;
        String hash = file.getOriginalFilename().replace(".csv","");

        //to_do 기존해쉬 체크
        Optional<ApiCollect> collect =apiCollectRepository.findById(hash);

        String progress = collect.map(ApiCollect::getProgress).orElse("0");


        ApiCollectDto stateModifyDto= new ApiCollectDto();
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        Timestamp systime = new Timestamp(now.getTime());
        stateModifyDto.setHash(hash);
         stateModifyDto.setDate(systime);

        /**
         * 1 Not yet
         * 2 collecting
         * 3 collect done
         * 4 collect error
         * 5 parsing
         * 6 parse error
         * 7 parse done
         * */
        if(!progress.equals("3")) {

            String resultText = progress.equals("5") ? "an another process parsing" : progress.equals("7") ?"Already done" : "parse error";
            String resultCode = progress.equals("4") ? "1" : progress.equals("6") ?"1" : "0";

            result.setResult_code(resultCode);
            result.setResult_text(resultText);
            return result ;
        }
        stateModifyDto.setProgress("5");
        apiCollectRepository.save(stateModifyDto.toEntiry());


        String reason= null;
        int firstLine = 0;
        int errorCount = 0;

        isr= new InputStreamReader(file.getInputStream(),"utf-8");
        buffer = new BufferedReader(isr);

        int commitSize = batchSize;
        int dataSeq = 0;
        ArrayList<ApiWalletSave>  insData = new ArrayList<ApiWalletSave>();
        while ((read = buffer.readLine()) !=null){
            dataSeq++;

            String data[] = read.split(",");

            if(firstLine ==0){
                reason=data[0]+":"+data[1];
                firstLine++;
                continue;
            }

            WalletSaveReqDto inDto = new WalletSaveReqDto();

            inDto.setHash(hash);
            inDto.setReason(reason);

            //파일 업로드후 거래주소 입력
            int idx = 0;
            for(String inData :data){
                if(idx == 0){
                    inDto.setAddr(inData.toLowerCase());
                }else if(idx == 1){
                    inDto.setCurrency_id(inData);
                }

                idx++;
            }
            //malware 타입지정
            inDto.setResult_type_id("4");
            insData.add(inDto.toEntity());

            //em.persist(inDto.toEntity());

            if(dataSeq%commitSize==0){
                try {
                    // walletSaveRepository.save(inDto.toEntity());
                    walletSaveRepository.saveAll(insData);
                    // em.flush();
                    //  em.clear();
                }catch (Exception e){
                    errorCount++;
                }

                insData.clear();
            }
        }

        stateModifyDto.setProgress("7");
        apiCollectRepository.save(stateModifyDto.toEntiry());

        result.setResult_code("0");
        result.setResult_text("add success and row errorCount:"+errorCount);
        return result;
    }


    @Override
    public ResultDto collectTransaction(ApiWalletTransactionReqDto param){

        ResultDto result = new ResultDto();

        //GET REQUEST URL 생성
        String url = this.buildEtherScanAccountUri(param);

        RestTemplate restTemplate = new RestTemplate();

        //api 호출
        String jsonData = restTemplate.getForObject(url,String.class);
        Gson apiResult = new Gson();

        EtherScanDto etherTxData = apiResult.fromJson(jsonData,EtherScanDto.class);

        //호출데이터 저장
        ArrayList<ApiWalletTransactionReqDto> addrToData = this.addWalletTransaction(etherTxData);
        result.setResult_code("success");
        result.setResult_text(addrToData.size()+" transaction add success");
        return result;
    }












//    @Override
//    public ArrayList<ApiWalletTransactionReqDto> collectTransaction(ArrayList<ApiWalletTransactionReqDto> param,HashMap<String,String> runKey){
//
//            ArrayList<ApiWalletTransactionReqDto> result = new ArrayList<ApiWalletTransactionReqDto>();
//
//            int stopCount =0;
//
//            param.forEach((dto)->{
//
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if(!runKey.containsKey(dto.getAddr())){
//                    runKey.put(dto.getAddr(),dto.getAddr());
//
//                    //GET REQUEST URL 생성
//                    String url = this.buildEtherScanAccountUri(dto);
//
//
//                    RestTemplate restTemplate = new RestTemplate();
//
//                    //api 호출
//                    String jsonData = restTemplate.getForObject(url,String.class);
//                    Gson apiResult = new Gson();
//
//                    EtherScanDto etherTxData = apiResult.fromJson(jsonData,EtherScanDto.class);
//
//                    //호출데이터 저장
//                    ArrayList<ApiWalletTransactionReqDto> addrToData = this.addWalletTransaction(etherTxData);
//
//                    //1,000건이상의 데이터는 거래소로 판단하여 더이상 수집하지 않는다.
//                    if(etherTxData.getResult().size() < 1000) {
//
//                        result.addAll(addrToData);
//
//                    }
//                }
//            });
//
//            if(result.size()==0){
//            return  null;
//        }
//
//        return collectTransaction(result,runKey);
//    }
}
