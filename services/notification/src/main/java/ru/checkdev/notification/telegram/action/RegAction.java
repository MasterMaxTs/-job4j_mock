package ru.checkdev.notification.telegram.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.dto.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;
import ru.checkdev.notification.telegram.util.TgEmailValidatorUtil;
import ru.checkdev.notification.telegram.util.TgPasswordGeneratorUtil;

import java.util.Calendar;
import java.util.Map;

/**
 * 3. Мидл
 * Класс реализует пункт меню регистрации нового пользователя в телеграм бот
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 12.09.2023
 */
@AllArgsConstructor
@Slf4j
public class RegAction implements Action {

    private static final String DELIMITER = "/";

    private static final String URL_AUTH_REGISTRATION = "/registration";

    private final TgAuthCallWebClient authCallWebClint;

    private final String urlSiteAuth;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите ваше ФИО/email для регистрации:";
        return new SendMessage(chatId, text);
    }

    /**
     * Метод формирует ответ пользователю.
     * Весь метод разбит на 4 этапа проверки.
     * 1. Проверка на соответствие формату Email введенного текста.
     * 2. Отправка данных в сервис Auth и если сервис не доступен сообщаем
     * 3. Если сервис доступен, получаем от него ответ и обрабатываем его.
     * 3.1 ответ при ошибке регистрации
     * 3.2 ответ при успешной регистрации.
     *
     * @param message Message
     * @return BotApiMethod<Message>
     */
    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var stringBuilder = new StringBuilder();
        var sl = System.lineSeparator();
        var input = message.getText().split(DELIMITER);
        if (input.length == 1) {
            stringBuilder
                    .append("Ошибка ввода данных: ").append(sl)
                    .append("Введите ваше ФИО/ваш email").append(sl)
                    .append("/new");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var username = input[0];
        var email = input[1];
        if (!TgEmailValidatorUtil.isEmail(email)) {
            stringBuilder
                    .append("Email: ").append(email).append(" не корректный.").append(sl)
                    .append("попробуйте снова.").append(sl)
                    .append("/new");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var password = TgPasswordGeneratorUtil.getPassword();
        var person = new PersonDTO(username, email, password, true, null,
                Calendar.getInstance());
        Object result;
        try {
            result = authCallWebClint.doPost(URL_AUTH_REGISTRATION, person).block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            stringBuilder
                    .append("Сервис не доступен попробуйте позже").append(sl)
                    .append("/start");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var mapObject = new ObjectMapper().convertValue(result, Map.class);
        if (mapObject.containsKey(ERROR_ACTION_KEY)) {
            stringBuilder
                    .append("Ошибка регистрации: ").append(mapObject.get(ERROR_ACTION_KEY))
                    .append(sl).append("/new");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        stringBuilder
                .append("Вы зарегистрированы: ").append(sl)
                .append("Логин: ").append(email).append(sl)
                .append("Пароль: ").append(password).append(sl)
                .append("Ссылка для входа: ").append(urlSiteAuth).append(sl)
                .append("/start");
        return new SendMessage(chatId, stringBuilder.toString());
    }
}
