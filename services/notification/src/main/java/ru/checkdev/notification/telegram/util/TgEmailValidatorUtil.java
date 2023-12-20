package ru.checkdev.notification.telegram.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TgEmailValidatorUtil {

    public static final String EMAIL_PATTERN = "\\w+([\\.-]?\\w+)*@\\w+([\\"
            + ".-]?\\w+)*\\.\\w{2,4}";

    public static boolean isEmail(String email) {
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}
