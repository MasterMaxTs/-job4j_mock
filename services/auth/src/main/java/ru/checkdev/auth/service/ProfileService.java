package ru.checkdev.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * CheckDev пробное собеседование
 * Класс получения ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 22.09.2023'T'23:41
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final PersonRepository personRepository;
    @Autowired
    private PasswordEncoder encoding;

    /**
     * Получить ProfileDTO по ID
     *
     * @param id int
     * @return ProfileDTO
     */
    public Optional<ProfileDTO> findProfileByID(int id) {
        return Optional.ofNullable(personRepository.findProfileById(id));
    }

    public Optional<ProfileDTO> findProfileByPassword(String password) {
        Iterable<Profile> profiles = personRepository.findAll();
        Optional<ProfileDTO> rsl = Optional.empty();
        for (Profile profile
                : profiles) {
            if (this.encoding.matches(password, profile.getPassword())) {
                rsl =
                        Optional.of(profile)
                                .map(pr -> new ProfileDTO())
                                .stream()
                                .peek(pdto -> {
                                                pdto.setId(profile.getId());
                                                pdto.setEmail(profile.getEmail());
                                                pdto.setUsername(profile.getUsername());
                                })
                                .findFirst();
            }
        }
        return rsl;
    }

    /**
     * Получить список всех PersonDTO
     *
     * @return List<PersonDTO>
     */
    public List<ProfileDTO> findProfilesOrderByCreatedDesc() {
        return personRepository.findProfileOrderByCreatedDesc();
    }

    public Optional<ProfileDTO> findProfileByEmail(String email) {
        return Optional.ofNullable(personRepository.findProfileByEmail(email));
    }
}
