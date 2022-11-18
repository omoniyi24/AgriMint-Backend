package com.github.agrimint.extended.repository;

import com.github.agrimint.domain.Member;
import com.github.agrimint.extended.dto.ExtendedGuardianDTO;
import com.github.agrimint.repository.FederationRepository;
import com.github.agrimint.repository.MemberRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedMemberRepository extends MemberRepository {
    @Query(
        "select new com.github.agrimint.extended.dto.ExtendedGuardianDTO (m.id, m.name, m.federationId, m.fedimintId, " +
        "m.userId, m.phoneNumber, m.countryCode, m.active, m.guardian, m.dateCreated, g.nodeNumber, g.secret, " +
        "g.invitationSent, g.invitationAccepted, g.id, g.memberId, f.fedimintId) from Member m " +
        "left join Guardian g on m.id=g.memberId left join Federation f on m.federationId=f.id where m.federationId =:federationId " +
        "and m.guardian =:guardian  and m.active =:active "
    )
    List<ExtendedGuardianDTO> findByFederationIdAndGuardianAndActive(
        @Param("federationId") Long federationId,
        @Param("guardian") Boolean guardian,
        @Param("active") Boolean active
    );
}
