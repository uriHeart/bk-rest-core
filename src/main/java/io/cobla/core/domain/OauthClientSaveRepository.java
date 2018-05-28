package io.cobla.core.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientSaveRepository extends JpaRepository<OauthClientDetail, String> {
}
