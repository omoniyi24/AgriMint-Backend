package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.OtpRequest;
import com.github.agrimint.repository.OtpRequestRepository;
import com.github.agrimint.service.criteria.OtpRequestCriteria;
import com.github.agrimint.service.dto.OtpRequestDTO;
import com.github.agrimint.service.mapper.OtpRequestMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OtpRequest} entities in the database.
 * The main input is a {@link OtpRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OtpRequestDTO} or a {@link Page} of {@link OtpRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OtpRequestQueryService extends QueryService<OtpRequest> {

    private final Logger log = LoggerFactory.getLogger(OtpRequestQueryService.class);

    private final OtpRequestRepository otpRequestRepository;

    private final OtpRequestMapper otpRequestMapper;

    public OtpRequestQueryService(OtpRequestRepository otpRequestRepository, OtpRequestMapper otpRequestMapper) {
        this.otpRequestRepository = otpRequestRepository;
        this.otpRequestMapper = otpRequestMapper;
    }

    /**
     * Return a {@link List} of {@link OtpRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OtpRequestDTO> findByCriteria(OtpRequestCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<OtpRequest> specification = createSpecification(criteria);
        return otpRequestMapper.toDto(otpRequestRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OtpRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OtpRequestDTO> findByCriteria(OtpRequestCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OtpRequest> specification = createSpecification(criteria);
        return otpRequestRepository.findAll(specification, page).map(otpRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OtpRequestCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OtpRequest> specification = createSpecification(criteria);
        return otpRequestRepository.count(specification);
    }

    /**
     * Function to convert {@link OtpRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OtpRequest> createSpecification(OtpRequestCriteria criteria) {
        Specification<OtpRequest> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OtpRequest_.id));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), OtpRequest_.phoneNumber));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), OtpRequest_.countryCode));
            }
            if (criteria.getOtp() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOtp(), OtpRequest_.otp));
            }
            if (criteria.getOtpType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOtpType(), OtpRequest_.otpType));
            }
            if (criteria.getOtpLength() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOtpLength(), OtpRequest_.otpLength));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStatus(), OtpRequest_.status));
            }
            if (criteria.getDateValidated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateValidated(), OtpRequest_.dateValidated));
            }
        }
        return specification;
    }
}
