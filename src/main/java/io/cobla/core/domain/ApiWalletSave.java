package io.cobla.core.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@SequenceGenerator(name = "wallet_seq", sequenceName = "api_wallet_id_seq",  allocationSize = 1000)
@Table(name="api_wallet")
public class ApiWalletSave implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    Long wallet_id;

    String addr;
    String exchange_id;
    String result_type_id;
    String currency_id;
    String reason;
    String hash;

    @Builder
    public ApiWalletSave(String addr, String exchange_id , String result_type_id, String currency_id,String reason,String hash) {
        //this.wallet_id = wallet_id;
        this.addr = addr;
        this.exchange_id = exchange_id;
        this.result_type_id = result_type_id;
        this.currency_id = currency_id;
        this.reason = reason;
        this.hash = hash;
     }
}
