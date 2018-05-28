package io.cobla.core.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthErrDTO {

    String error;
    String error_description;
}
