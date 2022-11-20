package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.AdminAppUserDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.util.ApplicationUrl;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL)
public class UserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList("id", "login", "firstName", "lastName", "email", "activated", "langKey")
    );

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExtendedAppUserService extendedAppUserService;

    public UserResource(ExtendedAppUserService extendedAppUserService) {
        this.extendedAppUserService = extendedAppUserService;
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/user")
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<AppUserDTO> createUser(
        @Valid @RequestBody AdminAppUserDTO userDTO,
        @RequestHeader(value = "channel", required = false) String channel
    ) throws URISyntaxException, UserException {
        log.debug("REST request to save User : {}", userDTO);

        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByPhoneNumberAndCountryCode(
            userDTO.getPhoneNumber(),
            userDTO.getCountryCode()
        );
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            throw new UserException("User Already Exist");
        } else {
            AppUserDTO appUser = extendedAppUserService.createAppUser(userDTO, channel);
            return ResponseEntity.created(new URI("/api/user/" + appUser.getId())).build();
        }
    }
}
