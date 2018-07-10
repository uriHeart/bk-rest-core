package io.cobla.core.dto;

import io.cobla.core.domain.OauthClientDetail;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
public class OauthClientDetailReqDto {

    @Id
    String client_id;
    String client_secret;
    String change_client_secret;
    String scope;
    String authorized_grant_types;
    String authorities;
    Integer access_token_validity;
    Integer refresh_token_validity;

    public OauthClientDetail toEntity(){
        return OauthClientDetail.builder()
                .client_id(client_id)
                .client_secret(client_secret)
                .scope(scope)
                .authorized_grant_types(authorized_grant_types)
                .authorities(authorities)
                .access_token_validity(access_token_validity)
                .refresh_token_validity(refresh_token_validity)
                .build();
    }


}
