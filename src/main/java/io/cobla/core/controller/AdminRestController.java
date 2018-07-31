package io.cobla.core.controller;

import com.google.gson.Gson;
import io.cobla.core.domain.OauthClientDetail;
import io.cobla.core.domain.repository.OauthClientSaveRepository;
import io.cobla.core.domain.OauthErrDTO;
import io.cobla.core.dto.OauthClientDetailReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@RestController
public class AdminRestController {


    @Autowired
    OauthClientSaveRepository oauthClientSaveRepository;

    @PutMapping("/oauth/change_user")
    public String getWalletQuery(@RequestBody OauthClientDetailReqDto dto, HttpServletRequest request){
//        String ip = request.getHeader("X-FORWARDED-FOR");
//        if(ip==null){
//            ip =request.getRemoteAddr();
//        }

        if(dto.getClient_id().isEmpty() || dto.getClient_secret().isEmpty()){
            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'client_id,client_secret' is required");
            return new Gson().toJson(errResult);

        }

        Optional<OauthClientDetail> read =oauthClientSaveRepository.findById(dto.getClient_id());
         String client_secret =  read.get().getClient_secret().replace("{noop}","");
         if(dto.getClient_secret().equals(client_secret)){
             dto.setClient_secret(StringUtils.isEmpty(dto.getChange_client_secret()) ? dto.getClient_secret() : dto.getChange_client_secret());
         }else{
             OauthErrDTO errResult = new OauthErrDTO();
             errResult.setError("invalid_password");
             errResult.setError_description("client_secret is not correct");
             return new Gson().toJson(errResult);
         }

        dto.setAccess_token_validity(dto.getAccess_token_validity() == null? 86400 :dto.getAccess_token_validity()); //60*60*24 하루동안유효함
        dto.setRefresh_token_validity(2592000);
        dto.setAuthorized_grant_types("password,refresh_token,client_credentials");
        dto.setScope("read,write");
        dto.setAuthorities("ROLL_EXCHANGE");
        String secret = dto.getClient_secret();
        dto.setClient_secret("{noop}"+secret);

        OauthClientDetail result = oauthClientSaveRepository.save(dto.toEntity());
        result.setClient_secret(secret);
        return new Gson().toJson(result);
    }
}
