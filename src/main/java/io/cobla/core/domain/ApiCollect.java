package io.cobla.core.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Table(name="api_collect")
public class ApiCollect {

    @Id
    String hash;
    String progress;
    Timestamp date;

    @Builder
    public ApiCollect(String hash,String progress, Timestamp date){
        this.hash = hash;
        this.progress = progress;
        this.date = date;
    };
}
