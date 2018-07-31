package io.cobla.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

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
