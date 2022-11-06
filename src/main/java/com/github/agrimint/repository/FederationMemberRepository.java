package com.github.agrimint.repository;

import com.github.agrimint.domain.FederationMember;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the FederationMember entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FederationMemberRepository extends JpaRepository<FederationMember, Long>, JpaSpecificationExecutor<FederationMember> {}
