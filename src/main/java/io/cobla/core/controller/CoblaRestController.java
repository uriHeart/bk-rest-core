package io.cobla.core.controller;

import com.google.gson.Gson;
import io.cobla.core.domain.*;
import io.cobla.core.domain.repository.ApiWalletSaveRepository;
import io.cobla.core.domain.repository.WalletRepository;
import io.cobla.core.dto.*;
import io.cobla.core.service.CoblaRestService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RestController
public class CoblaRestController {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ApiWalletSaveRepository walletSaveRepository;


    @Autowired
    CoblaRestService coblaRestService;

    @PostMapping("/wallet")
    public String getWallet(@RequestBody WalletSelDto dto){

        if(dto.getAddr().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'addr' is required");

            return new Gson().toJson(errResult);

         }

         ApiWallet wallet = dto.toEntity();

        Optional<ApiWallet>  indata = Optional.ofNullable(wallet);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("wallet_id")
                .withIgnoreNullValues();

        Example<ApiWallet> example = Example.of(wallet,matcher);


        Optional<ApiWallet> result =  walletRepository.findOne(example);

        WalletSelDto resultDto = new WalletSelDto();
        resultDto.setAddr(result.map(ApiWallet::getAddr).orElse(dto.getAddr()));
        resultDto.setCurrency(result.map(ApiWallet::getCoins).map(ApiCoin::getId).orElse(""));
        resultDto.setResult_code(result.map(ApiWallet::getWalletType).map(ApiWallettype::getResultcodtype).map(ApiResultcodtype::getId).orElse("0"));

        String resultType=result.map(ApiWallet::getWalletType).map(ApiWallettype::getId).orElse("0");

        //ico 기간만료 체크
        if(resultType.equals("2")) {
            Date icoStart = result.map(ApiWallet::getIco_start).orElse(null);
            Date icoEnd = result.map(ApiWallet::getIco_end).orElse(null);
            Date sysDate = new Date();
            if (!(sysDate.after(icoStart) && sysDate.before(icoEnd))) {
                //ico 기간만료
                resultType = "7";
            }
        }

        resultDto.setResult_type(resultType);
        resultDto.setExchange(result.map(ApiWallet::getApiExchange).map(ApiExchange::getId).orElse(""));
        resultDto.setIco_currency(result.map(ApiWallet::getIco_currency).orElse(""));

        return new Gson().toJson(resultDto);
    }



    @PutMapping("/wallet")
    public String saveWallet(@RequestBody WalletParamDto param){

        if(param.getAddr().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'addr' is required");
            return new Gson().toJson(errResult);
        }else if(param.getResult_type().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'result_type' is required");
            return new Gson().toJson(errResult);
        }else if(param.getCurrency().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'currency' is required");
            return new Gson().toJson(errResult);
        }else if(param.getExchange().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'exchange' is required");
            return new Gson().toJson(errResult);
        }

        WalletSaveReqDto inDto = new WalletSaveReqDto();
        inDto.setAddr(param.getAddr());
        inDto.setCurrency_id(param.getCurrency());
        inDto.setExchange_id(param.getExchange());
        inDto.setResult_type_id(param.getResult_type());
        ApiWalletSave result= walletSaveRepository.save(inDto.toEntity());

        WalletParamDto outDto = new WalletParamDto();
        outDto.setResult_type(result.getResult_type_id());
        outDto.setAddr(result.getAddr());
        outDto.setCurrency(result.getCurrency_id());
        outDto.setExchange(result.getExchange_id());

        return new Gson().toJson(outDto);
    }

    //@Transactional
    //@PostMapping(value ="/malware", consumes  = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value ="/malware")
    public String addWalletByMalware( @RequestParam("file") MultipartFile file) throws Exception {
        ResultDto result = coblaRestService.addBlackWallet(file);

        return new Gson().toJson(result);
     }



    @PostMapping(value ="/addr/transaction")
    public String doTransactionAnalysis( @RequestBody ApiWalletTransactionReqDto dto) throws Exception {

        String url = coblaRestService.buildEtherScanAccountUri(dto);

        RestTemplate restTemplate = new RestTemplate();

        String jsonData = restTemplate.getForObject(url,String.class);
        Gson apiResult = new Gson();
        EtherScanDto etherTxData = apiResult.fromJson(jsonData,EtherScanDto.class);

        String resultText = coblaRestService.addWalletTransaction(etherTxData);
        ResultDto result= new ResultDto();
        result.setResult_code("0");
        result.setResult_text(resultText);

        return new Gson().toJson(result);
    }

    @Transactional
    public void saveallWallet(List<ApiWalletSave> inData){
        walletSaveRepository.saveAll(inData);
    }


    @ExceptionHandler(value = NullPointerException.class)
    public OauthErrDTO errorHandler(NullPointerException e){
        OauthErrDTO errResult = new OauthErrDTO();
        errResult.setError("invalid_parameter");
        errResult.setError_description("parameter is null");
        return errResult;
    }


}
