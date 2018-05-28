package io.cobla.core.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface MemberRepository extends PagingAndSortingRepository<Member, Long>{
}
