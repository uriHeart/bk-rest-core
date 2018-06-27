package io.cobla.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiWalletTransactionReqDto {

    String addr;
    String module;
    String action;
    String startBlock;
    String endBlock;
    String sort;

}
