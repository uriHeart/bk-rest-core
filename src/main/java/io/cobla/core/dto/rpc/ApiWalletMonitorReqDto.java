package io.cobla.core.dto.rpc;

import io.cobla.core.domain.rpc.ApiWalletMonitor;
import io.cobla.core.domain.rpc.WalletMonitorId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiWalletMonitorReqDto {

    String addr;
    Integer userId;
    String currency;
    Double balance;
    Integer txCount;
    LocalDateTime lastTxTime;
    String etc;
    Integer monitorCycleSec;
    LocalDateTime monitorReqTime;
    String monitorYN;
    Integer monitorEffectiveTime;


    public ApiWalletMonitor toEntity(){
        WalletMonitorId id = new WalletMonitorId();
        id.setUser_id(this.userId);
        id.setAddr(this.addr);
        id.setCurrency_id(this.currency);

        return  ApiWalletMonitor.builder()
            .id(id)
            .balance(this.balance)
            .tx_count(this.txCount)
            .last_tx_time(this.lastTxTime)
            .etc(this.etc)
            .monitor_cycle_sec(this.monitorCycleSec)
            .monitor_req_time(this.monitorReqTime)
            .monitor_yn(this.monitorYN)
            .monitor_effective_time(this.monitorEffectiveTime)
            .build();
    };

}
