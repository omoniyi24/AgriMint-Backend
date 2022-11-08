package com.github.agrimint.extended.repository;

import com.github.agrimint.domain.AppUser;
import com.github.agrimint.repository.AppUserRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

/**
 * @author OMONIYI ILESANMI
 */
//@Repository
public interface ExtendedAppUserRepository extends AppUserRepository {
    //    @EntityGraph(attributePaths = "authorities")
    //    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<AppUser> findOneWithAuthoritiesByLogin(String login);
}
