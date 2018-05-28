package io.cobla.core.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="api_wallettype")
public class ApiWallettype {

    @Id
    String id;
    String name;

    @ManyToOne(targetEntity = ApiResultcodtype.class)
    @JoinColumn(name="result_code_id")
    ApiResultcodtype resultcodtype;

}
