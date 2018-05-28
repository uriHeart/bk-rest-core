package io.cobla.core.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Table(name="api_wallettype")
public class ApiWallettype implements Serializable {

    @Id
    String id;
    String name;

    @ManyToOne(targetEntity = ApiResultcodtype.class)
    @JoinColumn(name="result_code_id")
    ApiResultcodtype resultcodtype;

}
