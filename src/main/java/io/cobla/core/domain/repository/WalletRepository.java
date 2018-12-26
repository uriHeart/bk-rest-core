package io.cobla.core.domain.repository;

import io.cobla.core.domain.ApiWallet;
import io.cobla.core.dto.WalletReturnDto;
import io.cobla.core.dto.WalletSelDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface WalletRepository  extends JpaRepository<ApiWallet, Long> {

   /* @Query("SELECT new io.cobla.core.dto.WalletSelDto(" +
            "a.addr,a.coins,a.ico_start,a.ico_end ,a.ico_currency) FROM ApiWallet a WHERE addr=:addr ")
    WalletSelDto findByWallet(@Param("addr")String addr);*/


    Optional<ApiWallet> findDistinctFirstApiWalletByAddrAndCoins_Id(String addr, String currency);

    ArrayList<ApiWallet> findDistinctApiWalletByAddrInAndCoins_Id(List<String> addr , String currency);

    ArrayList<ApiWallet> findDistinctApiWalletByCoins_Id(String currency);

}
