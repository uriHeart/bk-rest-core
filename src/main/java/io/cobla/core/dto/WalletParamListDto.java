package io.cobla.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WalletParamListDto {

    List<WalletSelDto> addr_list;
    String currency;
    String exchange;
}
