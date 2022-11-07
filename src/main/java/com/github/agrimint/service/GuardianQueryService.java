package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.Guardian;
import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.service.criteria.GuardianCriteria;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.mapper.GuardianMapper;
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
 * Service for executing complex queries for {@link Guardian} entities in the database.
 * The main input is a {@link GuardianCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link GuardianDTO} or a {@link Page} of {@link GuardianDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class GuardianQueryService extends QueryService<Guardian> {

    private final Logger log = LoggerFactory.getLogger(GuardianQueryService.class);

    private final GuardianRepository guardianRepository;

    private final GuardianMapper guardianMapper;

    public GuardianQueryService(GuardianRepository guardianRepository, GuardianMapper guardianMapper) {
        this.guardianRepository = guardianRepository;
        this.guardianMapper = guardianMapper;
    }

    /**
     * Return a {@link List} of {@link GuardianDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<GuardianDTO> findByCriteria(GuardianCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Guardian> specification = createSpecification(criteria);
        return guardianMapper.toDto(guardianRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link GuardianDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<GuardianDTO> findByCriteria(GuardianCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Guardian> specification = createSpecification(criteria);
        return guardianRepository.findAll(specification, page).map(guardianMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(GuardianCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Guardian> specification = createSpecification(criteria);
        return guardianRepository.count(specification);
    }

    /**
     * Function to convert {@link GuardianCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Guardian> createSpecification(GuardianCriteria criteria) {
        Specification<Guardian> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Guardian_.id));
            }
            if (criteria.getMemberId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMemberId(), Guardian_.memberId));
            }
            if (criteria.getNodeNumber() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNodeNumber(), Guardian_.nodeNumber));
            }
            if (criteria.getSecret() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSecret(), Guardian_.secret));
            }
            if (criteria.getInvitationSent() != null) {
                specification = specification.and(buildSpecification(criteria.getInvitationSent(), Guardian_.invitationSent));
            }
            if (criteria.getInvitationAccepted() != null) {
                specification = specification.and(buildSpecification(criteria.getInvitationAccepted(), Guardian_.invitationAccepted));
            }
        }
        return specification;
    }
}
