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
    String result_code ="0";
    String result_text ="success";

    public void defaultFail(String message){
        this.result_code ="1";
        this.result_text =message;
    }
}
