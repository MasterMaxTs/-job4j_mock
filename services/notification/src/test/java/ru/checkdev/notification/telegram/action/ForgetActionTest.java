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

class ForgetActionTest {

    private static final String SL = System.lineSeparator();

    private TgAuthCallWebClient tgAuthCallWebClient;

    private Action action;

    private Message message;


    @BeforeEach
    void whenSetUp() {
        tgAuthCallWebClient = Mockito.mock(TgAuthCallWebClient.class);
        action = new ForgetAction(tgAuthCallWebClient);
        message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
    }

    @Test
    void whenHandleThanGetText() {
        SendMessage handle = (SendMessage) action.handle(message);
        assertThat(handle.getText())
                .isEqualTo("Введите ваш email для входа:");
    }

//    @Test
//    void whenInvalidEmailThenCallbackWithError() {
//        String email = "invalid@mail.com";
//        PersonDTO personDTO = new PersonDTO();
//        personDTO.setEmail(email);
//        message.setText(email);
//        Mono<ErrorDTO> errorDTOMono = Mono.fromSupplier(
//                () -> new ErrorDTO("Пользователь с почтой mail"
//                        + "не зарегистрирован в приложении!"));
//        doReturn(errorDTOMono)
//                .when(tgAuthCallWebClient)
//                .doPost(ForgetAction.URL_AUTH_FORGOT, personDTO);
//        SendMessage callback = (SendMessage) action.callback(message);
//        assertThat(callback.getText())
//                .isEqualTo( "Ошибка выполнения операции: "
//                                + "Пользователь с почтой mail"
//                                + " не зарегистрирован в приложении!"
//                                + SL
//                                 + "/forget"
//                );
//    }
//
//    @Test
//    void whenCallbackThenGet() {
//        String email = "test@mail.com";
//        final PersonDTO personDTO = new PersonDTO();
//        personDTO.setEmail(email);
//        message.setText(email);
//        Mono<PersonDTO> personDTOMono =
//                Mono.fromSupplier(() -> {
//                                    personDTO.setPassword("newRawPassword");
//                                    return personDTO;
//                });
//        doReturn(personDTOMono)
//                .when(tgAuthCallWebClient)
//                .doPost(ForgetAction.URL_AUTH_FORGOT, personDTO);
//        SendMessage callback = (SendMessage) action.callback(message);
//        assertThat(callback.getText())
//                .isEqualTo(
//                        "Ваши новые данные для входа: "
//                        + SL
//                        + "email: " + personDTO.getEmail()
//                        + SL
//                        + "password: " + personDTO.getPassword()
//                        + SL
//                        + "/start"
//                );
//    }
}