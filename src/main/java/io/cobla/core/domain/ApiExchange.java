package io.cobla.core.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name="api_exchange")
public class ApiExchange {

    @Id
    String id;
    String name;
    String url;
    Boolean verify;
    Boolean domestic;
}
