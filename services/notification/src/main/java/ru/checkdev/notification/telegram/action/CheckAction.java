package ru.checkdev.notification.telegram.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.checkdev.notification.dto.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CheckAction implements Action {

    public static final String URL_AUTH_PROFILES = "/profiles";

    private final TgAuthCallWebClient tgAuthCallWebClient;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите пароль для входа:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var password = message.getText();
        var stringBuilder = new StringBuilder();
        var sl = System.lineSeparator();
        var personDTO = new PersonDTO();
        personDTO.setPassword(password);
        Object result;
        try {
            result = tgAuthCallWebClient
                    .doPost("%s/getByPassword".formatted(URL_AUTH_PROFILES), personDTO)
                    .block();
        } catch (Exception e) {
            log.error("WebClient doPost error: {}", e.getMessage());
            stringBuilder
                    .append("Сервис не доступен попробуйте позже")
                    .append(sl)
                    .append("/start");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var mapObject = new ObjectMapper().convertValue(result, Map.class);
        if (mapObject.containsKey(ERROR_ACTION_KEY)) {
            stringBuilder
                    .append("Ошибка запроса данных: ")
                    .append(mapObject.get(ERROR_ACTION_KEY))
                    .append(sl)
                    .append("/check");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        stringBuilder
                .append("Ваши учётные данные:")
                .append(sl)
                .append("username: ").append(mapObject.get(Keys.USERNAME_KEY.getKeyName()))
                .append(sl)
                .append("email: ").append(mapObject.get(Keys.EMAIL_KEY.getKeyName()))
                .append(sl)
                .append("/start");
        return new SendMessage(chatId, stringBuilder.toString());
    }
}
