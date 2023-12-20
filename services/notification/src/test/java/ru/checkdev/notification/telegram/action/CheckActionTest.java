package ru.checkdev.notification.telegram.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Mono;
import ru.checkdev.notification.dto.ErrorDTO;
import ru.checkdev.notification.dto.PersonDTO;
import ru.checkdev.notification.telegram.service.TgAuthCallWebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

class CheckActionTest {

    public static final String SL = System.lineSeparator();

    private TgAuthCallWebClient tgAuthCallWebClient;

    private Action action;

    private Message message;

    @BeforeEach
    public void whenSetUp() {
        tgAuthCallWebClient = Mockito.mock(TgAuthCallWebClient.class);
        action = new CheckAction(tgAuthCallWebClient);
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    void whenHandleThanGetText() {
        SendMessage handle = (SendMessage) action.handle(message);
        assertThat(handle.getText())
                .isEqualTo("Введите пароль для входа:");
    }


    @Test
    void whenCallbackThenGetError() {
        String password = "invalid";
        PersonDTO personDTO = new PersonDTO();
        personDTO.setPassword(password);
        message.setText(password);
        Mono<ErrorDTO> errorDTOMono = Mono.fromSupplier(
                () -> new ErrorDTO("Введенный пароль не верный! "
                                            + "Укажите другой пароль."));
        doReturn(errorDTOMono)
                .when(tgAuthCallWebClient)
                .doPost("/profiles/getByPassword", personDTO);
        SendMessage callBack = (SendMessage) action.callback(message);
        assertThat(callBack.getText())
                .isEqualTo("Ошибка запроса данных: "
                        + "Введенный пароль не верный! "
                        + "Укажите другой пароль."
                        + SL
                        + "/check");
    }

    @Test
    void whenCallbackThenGet() {
        String password = "valid";
        final PersonDTO personDTO = new PersonDTO();
        personDTO.setPassword(password);
        message.setText(password);
        Mono<PersonDTO> personDTOMono =
                Mono.fromSupplier(() -> {
                                        personDTO.setUsername("username");
                                        personDTO.setEmail("email");
                                        return personDTO;
                                    });
        doReturn(personDTOMono)
                .when(tgAuthCallWebClient)
                .doPost("/profiles/getByPassword", personDTO);
        SendMessage callBack = (SendMessage) action.callback(message);
        assertThat(callBack.getText())
                .isEqualTo("Ваши учётные данные:"
                        + SL
                        + "username: " + "username"
                        + SL
                        + "email: " + "email"
                        + SL
                        + "/start");
    }
}