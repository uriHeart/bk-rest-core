package io.cobla.core.dto.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EthTxDto {
    String blockHash;
    String blockNumber;
    String chainId;
    String condition;
    String creates;
    String from;
    String gas;
    String gasPrice;
    String hash;
    String input;
    String nonce;
    String publicKey;
    String r;
    String raw;
    String s;
    String standardV;
    String to;
    String transactionIndex;
    String v;
    String value;
}
