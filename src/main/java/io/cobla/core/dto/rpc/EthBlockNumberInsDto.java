package io.cobla.core.dto.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EthBlockNumberInsDto {

    boolean isInsert;
    Integer blockNumber;
}
