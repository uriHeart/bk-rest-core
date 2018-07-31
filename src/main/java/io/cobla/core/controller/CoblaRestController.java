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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.*;


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

    @PostMapping("/v1/blacklist/wallet")
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


        Optional<ApiWallet> result =  walletRepository.findDistinctFirstApiWalletByAddrAndCoins_Id(dto.getAddr(),dto.getCurrency());

        //응답값 생성
        WalletSelDto resultDto = new WalletSelDto();
        String addr = result.map(ApiWallet::getAddr).orElse(dto.getAddr());
        resultDto.setAddr(addr.toLowerCase());
        resultDto.setCurrency(result.map(ApiWallet::getCoins).map(ApiCoin::getId).orElse(""));
        resultDto.setResult_code(result.map(ApiWallet::getWalletType).map(ApiWallettype::getResultcodtype).map(ApiResultcodtype::getId).orElse("0"));

        String resultType=result.map(ApiWallet::getWalletType).map(ApiWallettype::getId).orElse("0");

        //ico 기간만료 체크
        if(resultType.equals("2")) {
            Date icoStart = result.map(ApiWallet::getIco_start).orElse( new Date());
            Date icoEnd = result.map(ApiWallet::getIco_end).orElse( new Date());
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



    @PutMapping("/v1/blacklist/wallet")
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

        inDto.setAddr(param.getAddr().toLowerCase());
        inDto.setCurrency_id(param.getCurrency());
        inDto.setExchange_id(param.getExchange());
        inDto.setResult_type_id(param.getResult_type());
        inDto.setSys_create_date(LocalDateTime.now());

        ApiWalletSave result= walletSaveRepository.save(inDto.toEntity());

        WalletParamDto outDto = new WalletParamDto();

        outDto.setResult_type(result.getResult_type_id());
        outDto.setAddr(result.getAddr());
        outDto.setCurrency(result.getCurrency_id());
        outDto.setExchange(result.getExchange_id());

        return new Gson().toJson(outDto);
    }

    @PostMapping("/v1/blacklist/wallet_block")
    public String saveWalletList(@RequestBody WalletParamListDto params){

        ArrayList<String> addrList = new ArrayList<String>();

        //입력값 검증
        for(WalletSelDto param : params.getAddr_list()){
            addrList.add(param.getAddr());
            if(StringUtils.isEmpty(param.getAddr())){
                ResultDto resultDto = new ResultDto();
                resultDto.setResult_code("1");
                resultDto.setResult_text("error : addr is nul!");
                return new Gson().toJson(resultDto);
            }
        }


        ArrayList<WalletReturnDto> result = new ArrayList<WalletReturnDto>();

        ArrayList<ApiWallet> addrResult =  walletRepository.findDistinctApiWalletByAddrInAndCoins_Id(addrList,params.getCurrency());

        for(ApiWallet getData : addrResult){
            WalletReturnDto  outData = new WalletReturnDto();
            outData.setAddr(getData.getAddr().toLowerCase());
            outData.setCurrency(getData.getCoins().getId());
            outData.setResult_type(getData.getWalletType().getId());
            outData.setResult_code(getData.getWalletType().getResultcodtype().getId());

            String resultType = getData.getWalletType().getId();

            //ico 기간만료 체크
            if(resultType.equals("2")) {
                Date icoStart = getData.getIco_start();
                Date icoEnd = getData.getIco_end();
                Date sysDate = new Date();
                if(icoStart ==null || icoEnd == null){continue;}
                if (!(sysDate.after(icoStart) && sysDate.before(icoEnd))) {
                    //ico 기간만료
                    resultType = "7";
                }
            }

            outData.setResult_type(resultType);

            result.add(outData);
        }

        WalletListReturnDto listReturn = new WalletListReturnDto();
        listReturn.setResult(result);

        return new Gson().toJson(listReturn);
    }

    //@Transactional
    //@PostMapping(value ="/malware", consumes  = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PostMapping(value ="/malware")
    public String addWalletByMalware( @RequestParam("file") MultipartFile file) throws Exception {
        ResultDto result = coblaRestService.addBlackWallet(file);

        return new Gson().toJson(result);
     }




    @CrossOrigin(origins ="*")
    @RequestMapping(value ="/addr/transaction" ,method = RequestMethod.OPTIONS)
    public ResponseEntity<?> doTransactionAnalysisOptions(@RequestBody ApiWalletTransactionReqDto dto)  {
        return ResponseEntity.ok().allow(HttpMethod.POST,HttpMethod.GET,HttpMethod.DELETE,HttpMethod.OPTIONS,HttpMethod.HEAD).build();
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


    //이더스캔에서캔에서 지값 주소에 대한 트랜잭션을 획득한다.
     @PostMapping(value ="/addr/transaction")
    public String doTransactionAnalysis( @RequestBody ApiWalletTransactionReqDto dto)  {

        ResultDto result= new ResultDto();

        try {
            result = coblaRestService.collectTransaction(dto);
            result.setResult_code("0");
            result.setResult_text("SUCCESS");

        }catch(Exception e){
            result.setResult_code("1");
            result.setResult_text("addr add exception");
        }

        return new Gson().toJson(result);




    //이더스캔에서 지값 주소에 대한 트랜잭션을 획득한다.
//    @CrossOrigin(origins ="*")
//    @PostMapping(value ="/addr/transaction")
//    public String doTransactionAnalysis( @RequestBody ApiWalletTransactionReqDto dto)  {
//
//        //중복실행방지
//        HashMap<String,String> runKey = new HashMap<String,String>();
//
//        ResultDto result= new ResultDto();
//        result.setResult_code("0");
//        result.setResult_text("SUCCESS");
//
//
//        ArrayList<ApiWalletTransactionReqDto> req = new ArrayList<ApiWalletTransactionReqDto>();
//        req.add(dto);
//
//        try {
//            ArrayList<ApiWalletTransactionReqDto> addrKey = coblaRestService.collectTransaction(req,runKey);
//
//        }catch(Exception e){
//            result.setResult_code("1");
//            result.setResult_text("addr add exception");
//        }
//
//        return new Gson().toJson(result);
    }

}
