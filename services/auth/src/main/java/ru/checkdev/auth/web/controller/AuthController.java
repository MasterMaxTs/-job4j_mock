package ru.checkdev.auth.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.CredentialsDTO;
import ru.checkdev.auth.dto.RegErrorDTO;
import ru.checkdev.auth.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

/**
 * @author parsentev
 * @since 26.09.2016
 */
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final PersonService persons;

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/ping")
    public String ping() {
        String ping = "{}";
        return ping;
    }

    @GetMapping("/auth/activated/{key}")
    public Object activated(@PathVariable String key) {
        if (this.persons.activated(key)) {
            return new Object() {
                public boolean getSuccess() {
                    return true;
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "Notify has already activated";
                }
            };
        }
    }

    @PostMapping("/registration")
    public Object registration(@RequestBody Profile profile) {
        Optional<Profile> result = this.persons.create(profile);
        return result.map(prs -> (Object) prs)
                .orElse(new RegErrorDTO(
                                String.format("Пользователь с почтой %s "
                                                + "уже существует! "
                                        + "Укажите другой email.",
                                        profile.getEmail()))
                );
    }

    @PostMapping("/forgot")
    public Object forgot(@RequestBody Profile profile) {
        String email = profile.getEmail();
        String newRawPassword = profile.getPassword();
        Optional<Profile> result = this.persons.forgot(profile);
        return result
                .stream()
                .findFirst()
                .map(pfl -> {
                    CredentialsDTO credentials =
                            new CredentialsDTO(pfl.getEmail(), newRawPassword);
                    return (Object) credentials;
                })
                .orElse(new RegErrorDTO(
                        String.format("Пользователь с почтой %s"
                                + " не зарегистрирован в приложении!", email)));
    }

    @GetMapping("/revoke")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {

    }
}
