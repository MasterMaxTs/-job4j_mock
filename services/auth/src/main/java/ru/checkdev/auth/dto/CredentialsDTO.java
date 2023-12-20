package ru.checkdev.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CredentialsDTO {

    private String email;
    private String password;
}
