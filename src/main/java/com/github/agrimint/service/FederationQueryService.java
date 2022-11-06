package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.Federation;
import com.github.agrimint.repository.FederationRepository;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.mapper.FederationMapper;
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
 * Service for executing complex queries for {@link Federation} entities in the database.
 * The main input is a {@link FederationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FederationDTO} or a {@link Page} of {@link FederationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FederationQueryService extends QueryService<Federation> {

    private final Logger log = LoggerFactory.getLogger(FederationQueryService.class);

    private final FederationRepository federationRepository;

    private final FederationMapper federationMapper;

    public FederationQueryService(FederationRepository federationRepository, FederationMapper federationMapper) {
        this.federationRepository = federationRepository;
        this.federationMapper = federationMapper;
    }

    /**
     * Return a {@link List} of {@link FederationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FederationDTO> findByCriteria(FederationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Federation> specification = createSpecification(criteria);
        return federationMapper.toDto(federationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FederationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FederationDTO> findByCriteria(FederationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Federation> specification = createSpecification(criteria);
        return federationRepository.findAll(specification, page).map(federationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FederationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Federation> specification = createSpecification(criteria);
        return federationRepository.count(specification);
    }

    /**
     * Function to convert {@link FederationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Federation> createSpecification(FederationCriteria criteria) {
        Specification<Federation> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Federation_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Federation_.name));
            }
            if (criteria.getAlias() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAlias(), Federation_.alias));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Federation_.active));
            }
            if (criteria.getDateCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCreated(), Federation_.dateCreated));
            }
        }
        return specification;
    }
}
