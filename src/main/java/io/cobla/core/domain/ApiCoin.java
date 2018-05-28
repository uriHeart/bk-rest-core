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
@Table(name="api_coin")
public class ApiCoin implements Serializable {

    @Id
    String id;
    String name;
    String anonymity;
    String description;

}
