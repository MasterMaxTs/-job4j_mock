package ru.checkdev.notification.telegram.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TgPasswordGeneratorUtil {

    private static String passPrefix;
    private static int passSize;

    @Value("${tg.userpass.prefix}")
    public void setPassPrefix(String prefix) {
        TgPasswordGeneratorUtil.passPrefix = prefix;
    }

    @Value("${tg.userpass.size}")
    public void setPassSize(int size) {
        TgPasswordGeneratorUtil.passSize = size;
    }

    public static String getPassword() {
        String password = passPrefix + UUID.randomUUID();
        return password.substring(0, passSize);
    }
}
