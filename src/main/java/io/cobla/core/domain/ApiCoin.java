package io.cobla.core.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name="api_coin")
public class ApiCoin {

    @Id
    String id;
    String name;
    String anonymity;
    String description;

}
