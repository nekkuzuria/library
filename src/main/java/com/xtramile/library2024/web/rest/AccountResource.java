package com.xtramile.library2024.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.domain.*;
import com.xtramile.library2024.repository.*;
import com.xtramile.library2024.security.AuthoritiesConstants;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.*;
import com.xtramile.library2024.service.dto.*;
import com.xtramile.library2024.service.mapper.LibrarianMapper;
import com.xtramile.library2024.service.mapper.UserCombinedMapper;
import com.xtramile.library2024.web.rest.errors.*;
import com.xtramile.library2024.web.rest.errors.EmailAlreadyUsedException;
import com.xtramile.library2024.web.rest.errors.InvalidPasswordException;
import com.xtramile.library2024.web.rest.vm.BookVM;
import com.xtramile.library2024.web.rest.vm.KeyAndPasswordVM;
import com.xtramile.library2024.web.rest.vm.ManagedUserVM;
import com.xtramile.library2024.web.rest.vm.RegisterVM;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final LibrarianService librarianService;
    private final LibrarianRepository librarianRepository;
    private final LibrarianMapper librarianMapper;
    private final VisitorService visitorService;

    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    private final FileService fileService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;
    private final UserCombinedMapper userCombinedMapper;
    private final SecurityUtils securityUtils;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        LibrarianService librarianService,
        LibrarianRepository librarianRepository,
        LibrarianMapper librarianMapper,
        VisitorService visitorService,
        FileService fileService,
        LocationService locationService,
        ObjectMapper objectMapper,
        UserCombinedMapper userCombinedMapper,
        SecurityUtils securityUtils
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.librarianService = librarianService;
        this.librarianRepository = librarianRepository;
        this.librarianMapper = librarianMapper;
        this.visitorService = visitorService;
        this.fileService = fileService;
        this.locationService = locationService;
        this.objectMapper = objectMapper;
        this.userCombinedMapper = userCombinedMapper;
        this.securityUtils = securityUtils;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody RegisterVM registerVM) {
        if (isPasswordLengthInvalid(registerVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(registerVM, registerVM.getPassword());
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(AdminUserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@RequestPart("user") String userJson, @RequestPart(value = "file", required = false) MultipartFile file)
        throws IOException {
        AdminUserDTO userDTO = objectMapper.readValue(userJson, AdminUserDTO.class);
        String userLogin = securityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }

        if (file != null) {
            File savedFile = fileService.saveImage(file);
            userService.updateUserImage(savedFile);
        }

        UserLibrarianDTO userLibrarianDTO = objectMapper.readValue(userJson, UserLibrarianDTO.class);
        LocationDTO locationDTO = userCombinedMapper.toLocationDTO(userLibrarianDTO);
        if (librarianService.getLibrarianByUserId(user.get().getId()) != null) {
            LibrarianDTO librarianDTO = objectMapper.readValue(userJson, LibrarianDTO.class);
            librarianService.update(librarianDTO, user.get(), userDTO);
        } else {
            VisitorDTO visitorDTO = objectMapper.readValue(userJson, VisitorDTO.class);
            visitorService.update(visitorDTO, user.get(), userDTO);
        }
        locationService.update(locationDTO, user.get());

        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<User> user = userService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail");
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return (
            StringUtils.isEmpty(password) ||
            password.length() < ManagedUserVM.PASSWORD_MIN_LENGTH ||
            password.length() > ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }

    @GetMapping("/user-settings")
    public ResponseEntity<?> getUserSettings() {
        return userService
            .getUserWithAuthorities()
            .map(user -> {
                Long userId = SecurityUtils.getCurrentUserId();
                if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_ADMIN")) {
                    return ResponseEntity.ok(userService.getCombinedUserLibrarianResponse(userId));
                } else if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_USER")) {
                    return ResponseEntity.ok(userService.getCombinedUserVisitorResponse(userId));
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
                }
            })
            .orElseThrow(() -> new UserResource.UserNotFoundException("User could not be found"));
    }
}
