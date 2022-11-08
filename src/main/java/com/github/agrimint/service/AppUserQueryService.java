package com.github.agrimint.service;

import com.github.agrimint.domain.*; // for static metamodels
import com.github.agrimint.domain.AppUser;
import com.github.agrimint.repository.AppUserRepository;
import com.github.agrimint.service.criteria.AppUserCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.mapper.AppUserMapper;
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
 * Service for executing complex queries for {@link AppUser} entities in the database.
 * The main input is a {@link AppUserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AppUserDTO} or a {@link Page} of {@link AppUserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppUserQueryService extends QueryService<AppUser> {

    private final Logger log = LoggerFactory.getLogger(AppUserQueryService.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public AppUserQueryService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Return a {@link List} of {@link AppUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AppUserDTO> findByCriteria(AppUserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserMapper.toDto(appUserRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AppUserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AppUserDTO> findByCriteria(AppUserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserRepository.findAll(specification, page).map(appUserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppUserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<AppUser> specification = createSpecification(criteria);
        return appUserRepository.count(specification);
    }

    /**
     * Function to convert {@link AppUserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AppUser> createSpecification(AppUserCriteria criteria) {
        Specification<AppUser> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AppUser_.id));
            }
            if (criteria.getLogin() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLogin(), AppUser_.login));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), AppUser_.name));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), AppUser_.phoneNumber));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), AppUser_.countryCode));
            }
            if (criteria.getSecret() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSecret(), AppUser_.secret));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), AppUser_.active));
            }
            if (criteria.getDateCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCreated(), AppUser_.dateCreated));
            }
            if (criteria.getAuthoritiesId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAuthoritiesId(),
                            root -> root.join(AppUser_.authorities, JoinType.LEFT).get(Role_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
