package io.cobla.core.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Entity
@Table(name="oauth_client_details")
public class OauthClientDetail {

    @Id
    String client_id;
    String client_secret;
    String scope;
    String authorized_grant_types;
    String authorities;
    Integer access_token_validity;
    Integer refresh_token_validity;

    @Builder
    public OauthClientDetail(String client_id, String client_secret, String scope, String authorized_grant_types,String authorities , Integer access_token_validity, Integer refresh_token_validity){
        this.client_id = client_id;
        this.client_secret =client_secret;
        this.scope = scope;
        this.authorized_grant_types = authorized_grant_types;
        this.authorities = authorities;
        this.access_token_validity = access_token_validity;
        this.refresh_token_validity = refresh_token_validity;
    }
}
