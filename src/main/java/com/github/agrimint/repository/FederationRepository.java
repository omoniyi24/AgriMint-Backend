package com.github.agrimint.repository;

import com.github.agrimint.domain.Federation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Federation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FederationRepository extends JpaRepository<Federation, Long>, JpaSpecificationExecutor<Federation> {}
