package com.github.agrimint.extended.util;

import com.github.agrimint.extended.exeception.MemberAlreadyExistExecption;
import com.github.agrimint.service.FederationMemberQueryService;
import com.github.agrimint.service.FederationMemberService;
import com.github.agrimint.service.criteria.FederationMemberCriteria;
import com.github.agrimint.service.dto.FederationMemberDTO;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;
import tech.jhipster.service.filter.LongFilter;

/**
 * @author OMONIYI ILESANMI
 */

@Component
public class QueryUtil {

    private final FederationMemberService federationMemberService;
    private final FederationMemberQueryService federationMemberQueryService;

    public QueryUtil(FederationMemberService federationMemberService, FederationMemberQueryService federationMemberQueryService) {
        this.federationMemberService = federationMemberService;
        this.federationMemberQueryService = federationMemberQueryService;
    }

    public FederationMemberDTO persistFederationMember(Long federationId, Long memberId) throws MemberAlreadyExistExecption {
        if (getFederationMemberCount(federationId, memberId) > 0) {
            throw new MemberAlreadyExistExecption("Member Already Exist In Federation");
        }
        FederationMemberDTO federationMemberDTO = new FederationMemberDTO();
        federationMemberDTO.setMemberId(memberId);
        federationMemberDTO.setFederationId(federationId);
        federationMemberDTO.setActive(true);
        federationMemberDTO.setDateCreated(Instant.now());
        return federationMemberService.save(federationMemberDTO);
    }

    public Optional<FederationMemberDTO> getFederationMember(Long federationId, Long memberId) {
        FederationMemberCriteria federationMemberCriteria = new FederationMemberCriteria();
        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        federationMemberCriteria.setFederationId(federationIdFilter);

        LongFilter memberIdFilter = new LongFilter();
        memberIdFilter.setEquals(memberId);
        federationMemberCriteria.setFederationId(memberIdFilter);

        return Optional.ofNullable(federationMemberQueryService.findByCriteria(federationMemberCriteria).stream().findFirst().orElse(null));
    }

    public long getFederationMemberCount(Long federationId, Long memberId) {
        FederationMemberCriteria federationMemberCriteria = new FederationMemberCriteria();
        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        federationMemberCriteria.setFederationId(federationIdFilter);

        LongFilter memberIdFilter = new LongFilter();
        memberIdFilter.setEquals(memberId);
        federationMemberCriteria.setFederationId(memberIdFilter);

        return federationMemberQueryService.countByCriteria(federationMemberCriteria);
    }
}
