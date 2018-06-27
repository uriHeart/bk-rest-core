package io.cobla.core.dto;

import java.sql.Timestamp;
import io.cobla.core.domain.ApiCollect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
public class ApiCollectDto {

    @Id
    String hash;
     String progress;
    Timestamp date;


    public ApiCollect toEntiry(){

        return ApiCollect.builder()
                .hash(hash)
                 .progress(progress)
                .date(date)
                .build();
    }
}
