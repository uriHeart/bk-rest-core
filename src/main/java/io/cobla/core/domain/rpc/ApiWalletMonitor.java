package io.cobla.core.domain.rpc;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
@Table(name="api_wallet_monitor")
public class ApiWalletMonitor {

    @EmbeddedId
    WalletMonitorId id;

    Double balance;
    Integer tx_count;
    LocalDateTime last_tx_time;
    String etc;
    Integer monitor_cycle_sec;
    LocalDateTime monitor_req_time;
    String monitor_yn;
    Integer monitor_effective_time;


    @Builder
    public ApiWalletMonitor(Double balance, Integer tx_count, LocalDateTime last_tx_time , String etc, Integer monitor_cycle_sec, WalletMonitorId id, LocalDateTime monitor_req_time, String monitor_yn, Integer monitor_effective_time){
        this.id = id;
        this.balance = balance;
        this.tx_count = tx_count;
        this.last_tx_time = last_tx_time;
        this.etc = etc;
        this.monitor_cycle_sec=monitor_cycle_sec;
        this.monitor_req_time = monitor_req_time;
        this.monitor_yn = monitor_yn;
        this.monitor_effective_time = monitor_effective_time;

    }
}
