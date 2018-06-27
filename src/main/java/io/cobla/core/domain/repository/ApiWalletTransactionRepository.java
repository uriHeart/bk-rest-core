package io.cobla.core.domain.repository;

import io.cobla.core.domain.ApiWalletTransactionEther;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiWalletTransactionRepository extends JpaRepository<ApiWalletTransactionEther, Long> {
}
