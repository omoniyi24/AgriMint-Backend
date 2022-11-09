package com.github.agrimint.extended.dto;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExtendedGuardianDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long federationId;

    private String fedimintId;

    private Long userId;

    private String phoneNumber;

    private String countryCode;

    private Boolean active;

    private Boolean guardian;

    private Instant dateCreated;

    private Integer nodeNumber;

    private Integer secret;

    private Boolean invitationSent;

    private Boolean invitationAccepted;

    public ExtendedGuardianDTO(
        Long id,
        String name,
        Long federationId,
        String fedimintId,
        Long userId,
        String phoneNumber,
        String countryCode,
        Boolean active,
        Boolean guardian,
        Instant dateCreated,
        Integer nodeNumber,
        Integer secret,
        Boolean invitationSent,
        Boolean invitationAccepted
    ) {
        this.id = id;
        this.name = name;
        this.federationId = federationId;
        this.fedimintId = fedimintId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.active = active;
        this.guardian = guardian;
        this.dateCreated = dateCreated;
        this.nodeNumber = nodeNumber;
        this.secret = secret;
        this.invitationSent = invitationSent;
        this.invitationAccepted = invitationAccepted;
    }
}
