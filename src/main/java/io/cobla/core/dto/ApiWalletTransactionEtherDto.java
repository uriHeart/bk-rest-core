package io.cobla.core.dto;

import io.cobla.core.domain.ApiWalletTransactionEther;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApiWalletTransactionEtherDto {

    String blockNumber;
    String hash;
    String nonce;
    String blockHash;
    String transactionIndex;
    String from;
    String to;
    String value;
    String gas;
    String gasPrice;
    String isError;
    String txreceipt_status;
    String input;
    String contractAddress;
    String cumulativeGasUsed;
    String gasUsed;
    String confirmations;
    String timeStamp;
    String tokenSymbol;
    String tokenName;
    String tokenDecimal;
    String tokenDecimalInt = "1";

    public ApiWalletTransactionEther toEntity(){

        for(int i=0;i < Integer.parseInt(StringUtils.isEmpty(tokenDecimal)?"0":tokenDecimal) ;i++){
            tokenDecimalInt = tokenDecimalInt+"0";
        }

        Double base = Double.valueOf("1".equals(tokenDecimalInt)?"1000000000000000000":tokenDecimalInt);
        Double gasbBase = Double.valueOf("1000000000000000000");

        Double etherDoubleValue = Double.valueOf(this.value);
        Double gasDoubleValue = Double.valueOf(this.gasPrice);

        Double etherValue = etherDoubleValue/base;
        Double gasPrice =  gasDoubleValue/gasbBase;


        return ApiWalletTransactionEther.builder()
                .value(etherValue)
                .nonce(nonce)
                .block_hash(blockHash)
                .addr_from(from)
                .addr_to(to)
                .is_error(isError)
                .txreceipt_status(txreceipt_status)
                .input(input)
                .contract_address(contractAddress)
                .cumulative_gas_used(cumulativeGasUsed)
                .transaction_index(Integer.parseInt(transactionIndex))
                .gas(Integer.parseInt(gas))
                .gas_price(gasPrice)
                .confirmations(Integer.parseInt(confirmations))
                .tx_time(Long.parseLong(timeStamp))
                .tx_hash(hash)
                .token_name(tokenName)
                .token_symbol(tokenSymbol)
                .token_decimal(Integer.parseInt(StringUtils.isEmpty(tokenDecimal)?"0":tokenDecimal))
                .block_number(Integer.parseInt(StringUtils.isEmpty(blockNumber)?"0":blockNumber))
                .build();
    }
}
