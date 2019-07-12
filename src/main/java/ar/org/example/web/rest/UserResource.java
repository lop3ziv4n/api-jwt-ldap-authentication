package ar.org.example.web.rest;


import ar.org.example.service.UserService;
import ar.org.example.service.dto.UserDTO;
import ar.org.example.web.rest.errors.BadRequestAlertException;
import ar.org.example.web.rest.util.HeaderUtil;
import ar.org.example.web.rest.util.PaginationUtil;
import ar.org.example.web.rest.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing User.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST  /users : Create a new user.
     *
     * @param userDTO the userDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userDTO, or with status 400 (Bad Request) if the user has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserDTO result = userService.save(userDTO);
        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /users : Updates an existing user.
     *
     * @param userDTO the userDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userDTO,
     * or with status 400 (Bad Request) if the userDTO is not valid,
     * or with status 500 (Internal Server Error) if the userDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/users")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to update User : {}", userDTO);
        if (userDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserDTO result = userService.save(userDTO);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userDTO.getId().toString()))
                .body(result);
    }

    /**
     * GET  /users : get all the users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of users in body
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable) {
        log.debug("REST request to get a page of Users");
        Page<UserDTO> page = userService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:id : get the "id" user.
     *
     * @param id the id of the userDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userDTO, or with status 404 (Not Found)
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        Optional<UserDTO> userDTO = userService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userDTO);
    }

    /**
     * GET  /users/login/:login : get the "login" user.
     *
     * @param login the login of the userDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userDTO, or with status 404 (Not Found)
     */
    @GetMapping("/users/login/{login}")
    public ResponseEntity<UserDTO> getUserByLogin(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        Optional<UserDTO> userDTO = userService.findByLogin(login);
        return ResponseUtil.wrapOrNotFound(userDTO);
    }

    /**
     * DELETE  /users/:id : delete the "id" user.
     *
     * @param id the id of the userDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User : {}", id);
        userService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
