package com.github.agrimint.extended.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * A DTO for the {@link com.github.agrimint.domain.Member} entity.
 */

@Data
public class CreateFederationRequestDTO implements Serializable {

    @NotNull
    private String name;

    private String alias;
}
