package com.xtramile.library2024.web.rest;

import com.xtramile.library2024.domain.*;
import com.xtramile.library2024.repository.*;
import com.xtramile.library2024.security.AuthoritiesConstants;
import com.xtramile.library2024.security.SecurityUtils;
import com.xtramile.library2024.service.MailService;
import com.xtramile.library2024.service.UserService;
import com.xtramile.library2024.service.dto.AdminUserDTO;
import com.xtramile.library2024.service.dto.PasswordChangeDTO;
import com.xtramile.library2024.web.rest.errors.*;
import com.xtramile.library2024.web.rest.vm.KeyAndPasswordVM;
import com.xtramile.library2024.web.rest.vm.ManagedUserVM;
import jakarta.validation.Valid;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    private final LibrarianRepository librarianRepository; //TAMBAHAN========================
    private final VisitorRepository visitorRepository;
    private final LibraryRepository libraryRepository;
    private final LocationRepository locationRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        LibrarianRepository librarianRepository,
        VisitorRepository visitorRepository,
        LibraryRepository libraryRepository,
        LocationRepository locationRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.librarianRepository = librarianRepository;
        this.visitorRepository = visitorRepository;
        this.libraryRepository = libraryRepository;
        this.locationRepository = locationRepository;
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
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        Library library = libraryRepository
            .findById(managedUserVM.getLibraryId())
            .orElseThrow(() -> new RuntimeException("Library not found"));
        Location location = locationRepository
            .findById(managedUserVM.getLocationId())
            .orElseThrow(() -> new RuntimeException("Location not found"));
        logger.info("Library found: {}", library);

        if ("Librarian".equalsIgnoreCase(managedUserVM.getRoleChoice())) { //TAMBAHAN=========================
            userService.assignRole(user, AuthoritiesConstants.ADMIN);
            Librarian librarian = new Librarian();
            librarian.setUser(user);
            librarian.setEmail(user.getEmail());
            librarian.setName(user.getFirstName() + " " + user.getLastName());
            librarian.setPhoneNumber(managedUserVM.getPhoneNumber());
            librarian.setDateOfBirth(managedUserVM.getDateOfBirth());
            librarian.setLibrary(library);
            librarian.setLocation(location);
            librarianRepository.save(librarian);
        } else {
            userService.assignRole(user, AuthoritiesConstants.USER);
            Visitor visitor = new Visitor();
            visitor.setUser(user);
            visitor.setEmail(user.getEmail());
            visitor.setName(user.getFirstName() + " " + user.getLastName());
            visitor.setPhoneNumber(managedUserVM.getPhoneNumber());
            visitor.setDateOfBirth(managedUserVM.getDateOfBirth());
            visitor.setLibrary(library);
            visitor.setAddress(location);
            visitorRepository.save(visitor);
        }

        mailService.sendActivationEmail(user);
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
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
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
}
