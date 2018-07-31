package io.cobla.core.dto.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RpcReqDto {

    String jsonrpc ="2.0";
    String id ="1";
    String method;
    String[] params;
    String result;


}
