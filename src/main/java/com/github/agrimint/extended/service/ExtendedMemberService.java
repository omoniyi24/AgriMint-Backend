package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.MemberDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedMemberService {
    /**
     * Create a member.
     *
     * @param memberDTO the entity to save.
     * @return the persisted entity.
     */
    MemberDTO create(CreatMemberRequestDTO memberDTO, boolean active, boolean guardian, boolean checkFederation, String invitationCode)
        throws MemberExecption, FederationExecption, UserException;

    /**
     * Get all the members.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    List<MemberDTO> getAll(Long federationId, Pageable pageable);

    /**
     * Get the "id" member.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MemberDTO> getOne(Long id);
}
