package org.manage.service;

import org.manage.config.Constants;
import org.manage.domain.Authority;
import org.manage.domain.Member;
import org.manage.domain.User;
import org.manage.security.AuthoritiesConstants;
import org.manage.security.BCryptPasswordHasher;
import org.manage.security.RandomUtil;
import org.manage.service.dto.UserDTO;
import io.quarkus.panache.common.Page;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Transactional
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    final BCryptPasswordHasher passwordHasher;

    @Inject
    public UserService(BCryptPasswordHasher passwordHasher) {
        this.passwordHasher = passwordHasher;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return User
            .findOneByActivationKey(key)
            .map(
                user -> {
                    // activate given user for the registration key.
                    user.activated = true;
                    user.activationKey = null;
                    //                this.clearUserCaches(user);
                    log.debug("Activated user: {}", user);
                    return user;
                }
            );
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return User
            .findOneByResetKey(key)
            .filter(user -> user.resetDate.isAfter(Instant.now().minusSeconds(86400)))
            .map(
                user -> {
                    user.password = passwordHasher.hash(newPassword);
                    user.resetKey = null;
                    user.resetDate = null;
                    //                this.clearUserCaches(user);
                    return user;
                }
            );
    }

    public Optional<User> requestPasswordReset(String mail) {
        return User
            .findOneByEmailIgnoreCase(mail)
            .filter(user -> user.activated)
            .map(
                user -> {
                    user.resetKey = RandomUtil.generateResetKey();
                    user.resetDate = Instant.now();
                    //                this.clearUserCaches(user);
                    return user;
                }
            );
    }

    public User registerUser(UserDTO userDTO, String password) {
        User
            .findOneByLogin(userDTO.login.toLowerCase())
            .ifPresent(
                existingUser -> {
                    var removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                }
            );
        User
            .findOneByEmailIgnoreCase(userDTO.email)
            .ifPresent(
                existingUser -> {
                    var removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                }
            );
        var encryptedPassword = passwordHasher.hash(password);
        var newUser = new User();
        newUser.login = userDTO.login.toLowerCase();
        // new user gets initially a generated password
        newUser.password = encryptedPassword;
        newUser.firstName = userDTO.firstName;
        newUser.lastName = userDTO.lastName;
        if (userDTO.email != null) {
            newUser.email = userDTO.email.toLowerCase();
        }
        newUser.imageUrl = userDTO.imageUrl;
        newUser.langKey = userDTO.langKey;
        // new user is not active
        newUser.activated = false;
        // new user gets registration key
        newUser.activationKey = RandomUtil.generateActivationKey();
        Set<Authority> authorities = new HashSet<>();
        Authority.<Authority>findByIdOptional(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.authorities = authorities;
        User.persist(newUser);
        //        this.clearUserCaches(newUser);
        log.debug("Created Information for User: {}", newUser);

        Member.persist(userDtoToMember(userDTO));
        return newUser;
    }

    private Member userDtoToMember(UserDTO userDTO) {
        Member newMember = new Member();
        newMember.login = userDTO.login.toLowerCase();
        newMember.firstName = userDTO.firstName == null ? "" : userDTO.firstName;
        newMember.middleName = null;
        newMember.lastName = userDTO.lastName == null ? "" : userDTO.lastName;
        newMember.defaultProject = null;
        log.debug("Created Information for Member: {}", newMember);
        return newMember;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.activated) {
            return false;
        }
        User.delete("id", existingUser.id);
        //        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.login = userDTO.login.toLowerCase();
        user.firstName = userDTO.firstName;
        user.lastName = userDTO.lastName;
        if (userDTO.email != null) {
            user.email = userDTO.email.toLowerCase();
        }
        user.imageUrl = userDTO.imageUrl;
        if (userDTO.langKey == null) {
            user.langKey = Constants.DEFAULT_LANGUAGE; // default language
        } else {
            user.langKey = userDTO.langKey;
        }
        String encryptedPassword = passwordHasher.hash(RandomUtil.generatePassword());
        user.password = encryptedPassword;
        user.resetKey = RandomUtil.generateResetKey();
        user.resetDate = Instant.now();
        user.activated = true;
        if (userDTO.authorities != null) {
            Set<Authority> authorities = userDTO
                .authorities.stream()
                .map(authority -> Authority.<Authority>findByIdOptional(authority))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.authorities = authorities;
        }
        User.persist(user);
        //        this.clearUserCaches(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param login     the login to find the user to update.
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String login, String firstName, String lastName, String email, String langKey, String imageUrl) {
        User
            .findOneByLogin(login)
            .ifPresent(
                user -> {
                    user.firstName = firstName;
                    user.lastName = lastName;
                    if (email != null) {
                        user.email = email.toLowerCase();
                    }
                    user.langKey = langKey;
                    user.imageUrl = imageUrl;
                    //                this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                }
            );
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return User
            .<User>findByIdOptional(userDTO.id)
            .map(
                user -> {
                    user.login = userDTO.login.toLowerCase();
                    user.firstName = userDTO.firstName;
                    user.lastName = userDTO.lastName;
                    if (userDTO.email != null) {
                        user.email = userDTO.email.toLowerCase();
                    }
                    user.imageUrl = userDTO.imageUrl;
                    user.activated = userDTO.activated;
                    user.langKey = userDTO.langKey;
                    Set<Authority> managedAuthorities = user.authorities;
                    managedAuthorities.clear();
                    userDTO
                        .authorities.stream()
                        .map(authority -> Authority.<Authority>findByIdOptional(authority))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                    //                this.clearUserCaches(user);
                    log.debug("Changed Information for User: {}", user);
                    return user;
                }
            )
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        User
            .findOneByLogin(login)
            .ifPresent(
                user -> {
                    User.delete("id", user.id);
                    //            this.clearUserCaches(user);
                    log.debug("Deleted User: {}", user);
                }
            );
    }

    public void changePassword(String login, String currentClearTextPassword, String newPassword) {
        User
            .findOneByLogin(login)
            .ifPresent(
                user -> {
                    String currentEncryptedPassword = user.password;
                    if (!passwordHasher.checkPassword(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordHasher.hash(newPassword);
                    user.password = encryptedPassword;
                    //                this.clearUserCaches(user);
                    log.debug("Changed password for User: {}", user);
                }
            );
    }

    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return User.findOneWithAuthoritiesByLogin(login);
    }

    public List<UserDTO> getAllManagedUsers() {
        return User.findAllByLoginNot(Page.ofSize(20), Constants.ANONYMOUS_USER).stream().map(UserDTO::new).collect(Collectors.toList());
    }

    public List<String> getAuthorities() {
        return Authority.<Authority>streamAll().map(authority -> authority.name).collect(Collectors.toList());
    }
}
