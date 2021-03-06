package io.cobla.core.dto;

import io.cobla.core.domain.ApiWalletSave;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class WalletSaveReqDto {

    @Id
    Long wallet_id;
    String addr;
    String exchange_id;
    String result_type_id;
    String currency_id;
    String reason;
    String hash;
    LocalDateTime sys_create_date;

    public ApiWalletSave toEntity(){
        return ApiWalletSave.builder()
                .addr(addr)
                .exchange_id(exchange_id)
                .result_type_id(result_type_id)
                .currency_id(currency_id)
                .reason(reason)
                .hash(hash)
                .sys_cret_date(sys_create_date)
                .build();
    }
}
