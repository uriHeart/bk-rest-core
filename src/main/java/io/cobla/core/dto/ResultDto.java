package io.cobla.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultDto {
    /***
     *    0 : success
     *    1 : error
     */
    String result_code;
    String result_text;
}
