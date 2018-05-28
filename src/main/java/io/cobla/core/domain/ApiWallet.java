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
public class ApiWallet implements Serializable {

    @Id
    String addr;

    @ManyToOne(targetEntity = ApiExchange.class)
    @JoinColumn(name="exchange_id" )
    ApiExchange apiExchange;

    @ManyToOne(targetEntity = ApiWallettype.class)
    @JoinColumn(name="result_type_id")
    ApiWallettype walletType;

    @ManyToOne(targetEntity = ApiCoin.class)
    @JoinColumn(name="currency_id")
    ApiCoin coins;

    Date ico_start;
    Date ico_end;
    String ico_currency;

    @Builder
    public ApiWallet(String addr ,Date ico_start,Date ico_end,String ico_currency) {
        this.addr = addr;
        this.ico_start = ico_start;
        this.ico_end = ico_end;
        this.ico_currency = ico_currency;
        this.apiExchange.id =ico_currency;
    }
}
