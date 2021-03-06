package io.cobla.core.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Table(name="api_exchange")
public class ApiExchange implements Serializable {

    @Id
    String id;
    String name;
    String url;
    Boolean verify;
    Boolean domestic;
}
