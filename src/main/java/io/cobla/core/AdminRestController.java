package io.cobla.core;

import com.google.gson.Gson;
import io.cobla.core.domain.OauthClientDetail;
import io.cobla.core.domain.OauthClientSaveRepository;
import io.cobla.core.domain.OauthErrDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class AdminRestController {


    @Autowired
    OauthClientSaveRepository oauthClientSaveRepository;

    @PutMapping("/oauth/coinrail")
    public String getWalletQuery(@RequestBody OauthClientDetail dto){

        if(dto.getClient_id().isEmpty() || dto.getClient_secret().isEmpty()){
            OauthErrDTO errResult = new OauthErrDTO();
            errResult.setError("invalid_parameter");
            errResult.setError_description("'client_id,client_secret' is required");
            return new Gson().toJson(errResult);

        }

        dto.setAccess_token_validity(dto.getAccess_token_validity()==null? 86400 :dto.getAccess_token_validity()); //60*60*24 하루동안유효함
        dto.setRefresh_token_validity(2592000);
        dto.setAuthorized_grant_types("password,refresh_token,client_credentials");
        dto.setScope("read,write");
        dto.setAuthorities("ROLL_EXCHANGE");
        String secret = dto.getClient_secret();
        dto.setClient_secret("{noop}"+secret);

        OauthClientDetail result = oauthClientSaveRepository.save(dto);
        result.setClient_secret(secret);
        return new Gson().toJson(result);
    }
}
