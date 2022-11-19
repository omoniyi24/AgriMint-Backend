package com.github.agrimint.repository;

import com.github.agrimint.domain.Invite;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Invite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InviteRepository extends JpaRepository<Invite, Long>, JpaSpecificationExecutor<Invite> {}
