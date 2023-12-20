package ru.checkdev.notification.telegram.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Keys {

    ERROR_ACTION_KEY("error"), INFO_MESSAGE_KEY("infoMessage"),
    USERNAME_KEY("username"), EMAIL_KEY("email"),
    PASSWORD_KEY("password");

    private final String keyName;
}
