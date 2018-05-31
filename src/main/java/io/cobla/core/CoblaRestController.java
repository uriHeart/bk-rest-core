package io.cobla.core;

import com.google.gson.Gson;
import io.cobla.core.domain.*;
import io.cobla.core.dto.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RestController
public class CoblaRestController {


    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ApiWalletSaveRepository walletSaveRepository;

/*    @GetMapping("/walletQuery")
    public String getWalletQuery(@RequestBody WalletSelDto dto){
        WalletSelDto result = walletRepository.findByWallet(dto.getAddr());

        return new Gson().toJson(result);
    }*/

    @PostMapping("/wallet")
    public String getWallet(@RequestBody WalletSelDto dto){

        if(dto.getAddr().isEmpty()){

            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'addr' is required");
            return new Gson().toJson(errResult);

         }


        Optional<ApiWallet> result = walletRepository.findById(dto.getAddr());

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

   @ExceptionHandler(value = NullPointerException.class)
    public OauthErrDTO errorHandler(NullPointerException e){
        OauthErrDTO errResult = new OauthErrDTO();
        errResult.setError("invalid_parameter");
        errResult.setError_description("parameter is null");
        return errResult;
    }


}
