package com.xtramile.library2024.service;

import com.xtramile.library2024.config.Constants;
import com.xtramile.library2024.domain.*;
import com.xtramile.library2024.repository.*;
import com.xtramile.library2024.security.AuthoritiesConstants;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.dto.*;
import com.xtramile.library2024.service.mapper.*;
import com.xtramile.library2024.web.rest.vm.RegisterVM;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final LocationService locationService;

    private final LibraryService libraryService;

    private final LibrarianService librarianService;

    private final VisitorService visitorService;

    private final UserMapper userMapper;
    private final LibrarianRepository librarianRepository;
    private final VisitorRepository visitorRepository;
    private final UserCombinedMapper userCombinedMapper;
    private final LibrarianMapper librarianMapper;
    private final VisitorMapper visitorMapper;
    private final FileMapper fileMapper;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;
    private final FileRepository fileRepository;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        LocationService locationService,
        LibraryService libraryService,
        LibrarianService librarianService,
        VisitorService visitorService,
        UserMapper userMapper,
        LibrarianRepository librarianRepository,
        UserCombinedMapper userCombinedMapper,
        VisitorRepository visitorRepository,
        LibrarianMapper librarianMapper,
        VisitorMapper visitorMapper,
        FileMapper fileMapper,
        LocationMapper locationMapper,
        LocationRepository locationRepository,
        FileRepository fileRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.locationService = locationService;
        this.libraryService = libraryService;
        this.librarianService = librarianService;
        this.visitorService = visitorService;
        this.userMapper = userMapper;
        this.librarianRepository = librarianRepository;
        this.visitorRepository = visitorRepository;
        this.userCombinedMapper = userCombinedMapper;
        this.librarianMapper = librarianMapper;
        this.visitorMapper = visitorMapper;
        this.fileMapper = fileMapper;
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
        this.fileRepository = fileRepository;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public User registerUser(RegisterVM userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(true);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);

        UserDTO userDTOForRoles = userMapper.userToUserDTO(newUser);

        LibraryDTO libraryDTO = libraryService.findOne(userDTO.getLibraryId()).orElseThrow(() -> new RuntimeException("Library not found"));

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setStreetAddress(userDTO.getStreetAddress());
        locationDTO.setPosttalCode(userDTO.getPostalCode());
        locationDTO.setCity(userDTO.getCity());
        locationDTO.setStateProvince(userDTO.getStateProvince());
        LocationDTO savedLocationDTO = locationService.save(locationDTO);

        if ("Librarian".equalsIgnoreCase(userDTO.getRoleChoice())) {
            assignRole(newUser, AuthoritiesConstants.ADMIN);
            LibrarianDTO librarian = new LibrarianDTO();
            librarian.setUser(userDTOForRoles);
            librarian.setEmail(newUser.getEmail());
            librarian.setName(newUser.getFirstName() + " " + userDTO.getLastName());
            librarian.setPhoneNumber(userDTO.getPhoneNumber());
            librarian.setDateOfBirth(userDTO.getDateOfBirth());
            librarian.setLibrary(libraryDTO);
            librarian.setLocation(savedLocationDTO);
            librarianService.save(librarian);
        } else {
            assignRole(newUser, AuthoritiesConstants.USER);
            VisitorDTO visitor = new VisitorDTO();
            visitor.setUser(userDTOForRoles);
            visitor.setEmail(userDTO.getEmail());
            visitor.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
            visitor.setPhoneNumber(userDTO.getPhoneNumber());
            visitor.setDateOfBirth(userDTO.getDateOfBirth());
            visitor.setLibrary(libraryDTO);
            visitor.setAddress(savedLocationDTO);
            visitorService.save(visitor);
        }

        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public void assignRole(User user, String role) { //TAMBAHAN====================================
        Set<Authority> authorities = new HashSet<>();
        Authority authority = authorityRepository.findById(role).orElseThrow(() -> new RuntimeException("Role not found"));
        authorities.add(authority);
        user.setAuthorities(authorities);
        userRepository.save(user);
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                //                user.setFile(userDTO.getFile());
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                this.clearUserCaches(user);
                log.debug("Deleted User: {}", user);
            });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl, File file) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                user.setFile(file);
                userRepository.save(user);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
        }
    }

    public Optional<LibrarianDTO> getLibrarian(Long id) {
        return librarianRepository.findById(id).map(librarianMapper::toDto);
    }

    public Optional<VisitorDTO> getVisitor(Long id) {
        return visitorRepository.findById(id).map(visitorMapper::toDto);
    }

    public UserLibrarianDTO getCombinedUserLibrarianResponse(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AdminUserDTO adminUserDTO = userMapper.userToAdminUserDTO(user);

            LibrarianDTO librarianDTO = librarianMapper.toDto(librarianRepository.findByUserId(userId).orElse(null));
            LocationDTO locationDTO = locationMapper.toDto(locationRepository.findById(librarianDTO.getLocation().getId()).orElse(null));
            FileDTO fileDTO = fileMapper.toDto(user.getFile());

            return userCombinedMapper.toCombinedResponseLibrarianDTO(
                new UserLibrarianDTO(),
                adminUserDTO,
                librarianDTO,
                locationDTO,
                fileDTO
            );
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public UserVisitorDTO getCombinedUserVisitorResponse(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AdminUserDTO adminUserDTO = userMapper.userToAdminUserDTO(user);

            VisitorDTO visitorDTO = visitorMapper.toDto(visitorRepository.findByUserId(userId).orElse(null));
            LocationDTO locationDTO = locationMapper.toDto(locationRepository.findById(visitorDTO.getAddress().getId()).orElse(null));
            FileDTO fileDTO = fileMapper.toDto(user.getFile());

            return userCombinedMapper.toCombinedResponseVisitorDTO(new UserVisitorDTO(), adminUserDTO, visitorDTO, locationDTO, fileDTO);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
