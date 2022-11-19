package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.Invite;
import com.github.agrimint.repository.InviteRepository;
import com.github.agrimint.service.criteria.InviteCriteria;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.mapper.InviteMapper;
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
 * Service for executing complex queries for {@link Invite} entities in the database.
 * The main input is a {@link InviteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InviteDTO} or a {@link Page} of {@link InviteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InviteQueryService extends QueryService<Invite> {

    private final Logger log = LoggerFactory.getLogger(InviteQueryService.class);

    private final InviteRepository inviteRepository;

    private final InviteMapper inviteMapper;

    public InviteQueryService(InviteRepository inviteRepository, InviteMapper inviteMapper) {
        this.inviteRepository = inviteRepository;
        this.inviteMapper = inviteMapper;
    }

    /**
     * Return a {@link List} of {@link InviteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InviteDTO> findByCriteria(InviteCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Invite> specification = createSpecification(criteria);
        return inviteMapper.toDto(inviteRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InviteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InviteDTO> findByCriteria(InviteCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Invite> specification = createSpecification(criteria);
        return inviteRepository.findAll(specification, page).map(inviteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InviteCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Invite> specification = createSpecification(criteria);
        return inviteRepository.count(specification);
    }

    /**
     * Function to convert {@link InviteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Invite> createSpecification(InviteCriteria criteria) {
        Specification<Invite> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Invite_.id));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), Invite_.phoneNumber));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), Invite_.countryCode));
            }
            if (criteria.getInvitationCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getInvitationCode(), Invite_.invitationCode));
            }
            if (criteria.getFederationId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFederationId(), Invite_.federationId));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Invite_.active));
            }
        }
        return specification;
    }
}
