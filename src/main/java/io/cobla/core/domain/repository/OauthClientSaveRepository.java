package io.cobla.core.domain.repository;

import io.cobla.core.domain.OauthClientDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientSaveRepository extends JpaRepository<OauthClientDetail, String> {
}
