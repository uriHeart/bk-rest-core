package io.cobla.core.domain.repository;

import io.cobla.core.domain.ApiWallet;
import io.cobla.core.dto.WalletSelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository  extends JpaRepository<ApiWallet, Long> {

   /* @Query("SELECT new io.cobla.core.dto.WalletSelDto(" +
            "a.addr,a.coins,a.ico_start,a.ico_end ,a.ico_currency) FROM ApiWallet a WHERE addr=:addr ")
    WalletSelDto findByWallet(@Param("addr")String addr);*/
}
