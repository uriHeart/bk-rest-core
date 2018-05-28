package io.cobla.core.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name="api_wallet")
public class ApiWalletSave implements Serializable {

    @Id
    String addr;
    String exchange_id;
    String result_type_id;
    String currency_id;

    @Builder
    public ApiWalletSave(String addr, String exchange_id , String result_type_id, String currency_id) {
        this.addr = addr;
        this.exchange_id = exchange_id;
        this.result_type_id = result_type_id;
        this.currency_id = currency_id;
     }
}
