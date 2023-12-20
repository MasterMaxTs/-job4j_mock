package ru.checkdev.notification.telegram.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.dto.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;
import ru.checkdev.notification.telegram.util.TgEmailValidatorUtil;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class SubscribeAction implements Action {

    private static final String DELIMITER = ":";

    private static final String URL_AUTH_PERSON = "/person";

    private final TgAuthCallWebClient tgAuthCallWebClient;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите email:password для подписки на уведомления:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var credentials = message.getText().split(DELIMITER);
        var stringBuilder = new StringBuilder();
        var sl = System.lineSeparator();
        if (credentials.length == 1) {
            stringBuilder
                    .append("Ошибка ввода данных: ").append(sl)
                    .append("Введите ваш email:ваш password").append(sl)
                    .append("/subscribe");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var email = credentials[0];
        var password = credentials[1];
        if (!TgEmailValidatorUtil.isEmail(email)) {
            stringBuilder
                    .append("Email: ").append(email)
                    .append(" не корректный.").append(sl)
                    .append("попробуйте снова.").append(sl)
                    .append("/subscribe");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var personDTO = new PersonDTO();
        personDTO.setEmail(email);
        personDTO.setPassword(password);
        Object result;
        try {
             result = tgAuthCallWebClient.doPost(
                     "%s/subscribe".formatted(URL_AUTH_PERSON), personDTO)
                     .block();
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
                    .append("Ошибка аутентификации: ").append(mapObject.get(ERROR_ACTION_KEY))
                    .append(sl).append("/subscribe");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        stringBuilder
                .append(mapObject.get(Keys.INFO_MESSAGE_KEY.getKeyName())).append(sl)
                .append("/start");
        return new SendMessage(chatId, stringBuilder.toString());
    }
}
