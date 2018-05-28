package io.cobla.core.dto;

import io.cobla.core.domain.ApiWalletSave;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;


@Getter
@Setter
@NoArgsConstructor
public class WalletSaveReqDto {

    @Id
    String addr;
    String exchange_id;
    String result_type_id;
    String currency_id;

    public ApiWalletSave toEntity(){
        return ApiWalletSave.builder()
                .addr(addr)
                .exchange_id(exchange_id)
                .result_type_id(result_type_id)
                .currency_id(currency_id)
                .build();
    }
}
