package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.FederationMember;
import com.github.agrimint.repository.FederationMemberRepository;
import com.github.agrimint.service.criteria.FederationMemberCriteria;
import com.github.agrimint.service.dto.FederationMemberDTO;
import com.github.agrimint.service.mapper.FederationMemberMapper;
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
 * Service for executing complex queries for {@link FederationMember} entities in the database.
 * The main input is a {@link FederationMemberCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FederationMemberDTO} or a {@link Page} of {@link FederationMemberDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FederationMemberQueryService extends QueryService<FederationMember> {

    private final Logger log = LoggerFactory.getLogger(FederationMemberQueryService.class);

    private final FederationMemberRepository federationMemberRepository;

    private final FederationMemberMapper federationMemberMapper;

    public FederationMemberQueryService(
        FederationMemberRepository federationMemberRepository,
        FederationMemberMapper federationMemberMapper
    ) {
        this.federationMemberRepository = federationMemberRepository;
        this.federationMemberMapper = federationMemberMapper;
    }

    /**
     * Return a {@link List} of {@link FederationMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FederationMemberDTO> findByCriteria(FederationMemberCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FederationMember> specification = createSpecification(criteria);
        return federationMemberMapper.toDto(federationMemberRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FederationMemberDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FederationMemberDTO> findByCriteria(FederationMemberCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FederationMember> specification = createSpecification(criteria);
        return federationMemberRepository.findAll(specification, page).map(federationMemberMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FederationMemberCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FederationMember> specification = createSpecification(criteria);
        return federationMemberRepository.count(specification);
    }

    /**
     * Function to convert {@link FederationMemberCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FederationMember> createSpecification(FederationMemberCriteria criteria) {
        Specification<FederationMember> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FederationMember_.id));
            }
            if (criteria.getFederationId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFederationId(), FederationMember_.federationId));
            }
            if (criteria.getMemberId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMemberId(), FederationMember_.memberId));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), FederationMember_.active));
            }
            if (criteria.getDateCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCreated(), FederationMember_.dateCreated));
            }
        }
        return specification;
    }
}
