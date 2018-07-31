package io.cobla.core.domain.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class WalletMonitorId implements Serializable {

    Integer user_id;
    String addr;
    String currency_id;

}
