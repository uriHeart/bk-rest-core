package io.cobla.core.dto.rpc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EthBlockDataDto {

    String author;
    String difficulty;
    String extraData;
    String gasLimit;
    String gasUsed;
    String hash;
    String logsBloom;
    String miner;
    String mixHash;
    String nonce;
    String number;
    String parentHash;
    String receiptsRoot;
    List<String> sealFields;
    String sha3Uncles;
    String size;
    String stateRoot;
    String timestamp;
    String totalDifficulty;
    List<EthTxDto>  transactions;
    String transactionsRoot;
    List<String> uncles;
}
