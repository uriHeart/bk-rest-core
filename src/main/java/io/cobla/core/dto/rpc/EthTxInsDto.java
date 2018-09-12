package io.cobla.core.dto.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class EthTxInsDto {
    String author;
    String difficulty;
    String extraData;
    String gasLimit;
    String gasUsed;
    String blockHash;
    String blockNonce;
    String logsBloom;
    String miner;
    String mixHash;
    String nonce;
    String parentHash;
    String receiptsRoot;
    String sha3Uncles;
    String size;
    String stateRoot;
    LocalDateTime timestamp;
    String totalDifficulty;
    String transactionsRoot;
    String blockNumber;
    String chainId;
    String condition;
    String creates;
    String from;
    String gas;
    String gasPrice;
    String hash;
    String input;
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
