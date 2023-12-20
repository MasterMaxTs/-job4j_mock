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
import ru.checkdev.notification.telegram.util.TgPasswordGeneratorUtil;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class ForgetAction implements Action {

    public static final String URL_AUTH_FORGOT= "/forgot";

    private final TgAuthCallWebClient tgAuthCallWebClient;

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var text = "Введите ваш email для входа:";
        return new SendMessage(chatId, text);
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        var chatId = message.getChatId().toString();
        var email = message.getText();
        var stringBuilder = new StringBuilder();
        var sl = System.lineSeparator();
        if (!TgEmailValidatorUtil.isEmail(email)) {
            stringBuilder
                    .append("Email: ").append(email).append(" не корректный.")
                    .append(sl)
                    .append("попробуйте снова.")
                    .append(sl)
                    .append("/forget");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        var personDTO = new PersonDTO();
        personDTO.setEmail(email);
        personDTO.setPassword(TgPasswordGeneratorUtil.getPassword());
        Object result;
        try {
            result =
                    tgAuthCallWebClient.doPost(URL_AUTH_FORGOT, personDTO).block();
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
                    .append("Ошибка выполнения операции: ").append(mapObject.get(ERROR_ACTION_KEY))
                    .append(sl)
                    .append("/start");
            return new SendMessage(chatId, stringBuilder.toString());
        }
        stringBuilder
                .append("Ваши новые данные для входа: ")
                .append(sl)
                .append("email: ").append(mapObject.get(Keys.EMAIL_KEY.getKeyName()))
                .append(sl)
                .append("password: ").append(mapObject.get(Keys.PASSWORD_KEY.getKeyName()))
                .append(sl)
                .append("/start");
        return new SendMessage(chatId, stringBuilder.toString());
    }
}
