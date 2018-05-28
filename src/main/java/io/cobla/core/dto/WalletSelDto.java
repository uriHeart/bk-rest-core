package io.cobla.core.dto;

import io.cobla.core.domain.ApiCoin;
import io.cobla.core.domain.ApiWallet;
import lombok.*;

import javax.persistence.Id;
import java.util.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class WalletSelDto{

    @Id
    String addr;
    Date ico_start;
    Date ico_end;
    String ico_currency;
    String currency;
    String result_code;
    String result_type;
    String exchange;


     public WalletSelDto(String addr, ApiCoin coins, Date ico_start, Date ico_end, String ico_currency) {
         this.addr = addr;
          this.ico_start = ico_start;
         this.ico_end = ico_end;
         this.ico_currency = ico_currency;
     }

}
