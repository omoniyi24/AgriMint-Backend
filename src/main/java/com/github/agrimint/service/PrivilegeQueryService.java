package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.Privilege;
import com.github.agrimint.repository.PrivilegeRepository;
import com.github.agrimint.service.criteria.PrivilegeCriteria;
import com.github.agrimint.service.dto.PrivilegeDTO;
import com.github.agrimint.service.mapper.PrivilegeMapper;
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
 * Service for executing complex queries for {@link Privilege} entities in the database.
 * The main input is a {@link PrivilegeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PrivilegeDTO} or a {@link Page} of {@link PrivilegeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PrivilegeQueryService extends QueryService<Privilege> {

    private final Logger log = LoggerFactory.getLogger(PrivilegeQueryService.class);

    private final PrivilegeRepository privilegeRepository;

    private final PrivilegeMapper privilegeMapper;

    public PrivilegeQueryService(PrivilegeRepository privilegeRepository, PrivilegeMapper privilegeMapper) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeMapper = privilegeMapper;
    }

    /**
     * Return a {@link List} of {@link PrivilegeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PrivilegeDTO> findByCriteria(PrivilegeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Privilege> specification = createSpecification(criteria);
        return privilegeMapper.toDto(privilegeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PrivilegeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PrivilegeDTO> findByCriteria(PrivilegeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Privilege> specification = createSpecification(criteria);
        return privilegeRepository.findAll(specification, page).map(privilegeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PrivilegeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Privilege> specification = createSpecification(criteria);
        return privilegeRepository.count(specification);
    }

    /**
     * Function to convert {@link PrivilegeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Privilege> createSpecification(PrivilegeCriteria criteria) {
        Specification<Privilege> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Privilege_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Privilege_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Privilege_.description));
            }
            if (criteria.getGroup() != null) {
                specification = specification.and(buildStringSpecification(criteria.getGroup(), Privilege_.group));
            }
            if (criteria.getRolesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getRolesId(), root -> root.join(Privilege_.roles, JoinType.LEFT).get(Role_.id))
                    );
            }
        }
        return specification;
    }
}
