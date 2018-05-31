package io.cobla.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletParamDto {

    String addr;
    String exchange;
    String result_type;
    String currency;
}
