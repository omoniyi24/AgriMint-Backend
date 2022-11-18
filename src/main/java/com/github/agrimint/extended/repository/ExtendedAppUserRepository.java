package com.github.agrimint.extended.repository;

import com.github.agrimint.domain.AppUser;
import com.github.agrimint.repository.AppUserRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedAppUserRepository extends AppUserRepository {
    Optional<AppUser> findOneWithAuthoritiesByLogin(String login);

    Optional<AppUser> findByPhoneNumberAndCountryCode(String phoneNumber, String countryCode);
}
