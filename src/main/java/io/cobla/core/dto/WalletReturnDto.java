package io.cobla.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletReturnDto {
    String addr;
    String currency;
    String result_code;
    String result_type ;
}
