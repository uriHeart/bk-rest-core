package io.cobla.core.domain;


import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
//@SequenceGenerator(name = "api_wallet_transaction_ether_seq", sequenceName = "api_wallet_transaction_ether_id", initialValue = 1, allocationSize = 1)
@Table(name="api_wallet_transaction_ether")
public class ApiWalletTransactionEther {

    @Id
    @Column(name="tx_hash")
    String tx_hash;
    // @GeneratedValue(strategy = GenerationType. SEQUENCE, generator = "api_wallet_transaction_ether_seq")
   // Long transaction_id;
    Double value;
    String nonce;
    String block_hash;
    String addr_from;
    String addr_to;
    String is_error;
    String txreceipt_status;
    String input;
    String contract_address;
    String cumulative_gas_used;
    Integer transaction_index;
    Integer gas;
    Double gas_price;
    Integer confirmations;
    Long tx_time;
    Integer block_number;
    String token_name;
    String token_symbol;
    Integer token_decimal;

    @Builder
    public ApiWalletTransactionEther(
            Double value,
            String nonce,
            String block_hash,
            String addr_from,
            String addr_to,
            String is_error,
            String txreceipt_status,
            String input,
            String contract_address,
            String cumulative_gas_used,
            Integer transaction_index,
            Integer gas,
            Double gas_price,
            Integer confirmations,
            String tx_hash,
            Long tx_time,
            String token_name,
            String token_symbol,
            Integer token_decimal,
            Integer block_number){

        this.value = value;
        this.nonce = nonce;
        this.block_hash = block_hash;
        this.addr_from = addr_from;
        this.addr_to = addr_to;
        this.is_error = is_error;
        this.txreceipt_status = txreceipt_status;
        this.input = input;
        this.contract_address = contract_address;
        this.cumulative_gas_used = cumulative_gas_used;
        this.transaction_index = transaction_index;
        this.gas = gas;
        this.gas_price = gas_price;
        this.confirmations = confirmations;
        this.tx_hash = tx_hash;
        this.tx_time = tx_time;
        this.token_name = token_name;
        this.token_symbol = token_symbol;
        this.token_decimal = token_decimal;
        this.block_number = block_number;

    }

}
