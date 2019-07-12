package ar.org.example.service;


import ar.org.example.domain.User;
import ar.org.example.repository.UserRepository;
import ar.org.example.service.dto.UserDTO;
import ar.org.example.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing User.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Save a user.
     *
     * @param userDTO the entity to save
     * @return the persisted entity
     */
    public UserDTO save(UserDTO userDTO) {
        log.debug("Request to save User : {}", userDTO);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * Get all the users.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserSidcas");
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }


    /**
     * Get one user by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findOne(Long id) {
        log.debug("Request to get User : {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    /**
     * Get one user by login.
     *
     * @param login the login of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByLogin(String login) {
        log.debug("Request to get User : {}", login);
        return userRepository.findByLogin(login)
                .map(userMapper::toDto);
    }

    /**
     * Delete the user by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        userRepository.deleteById(id);
    }
}
