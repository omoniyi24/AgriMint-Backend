package com.github.agrimint.repository;

import com.github.agrimint.domain.OtpRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the OtpRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long>, JpaSpecificationExecutor<OtpRequest> {}
