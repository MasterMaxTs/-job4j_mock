package ru.checkdev.notification.telegram.action;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UnknownCommandAction implements Action {

    @Override
    public BotApiMethod<Message> handle(Message message) {
        var chatId = message.getChatId().toString();
        var stringBuilder = new StringBuilder();
        var sl = System.lineSeparator();
        stringBuilder
                .append("Команда не поддерживается! Список доступных команд:")
                .append(sl)
                .append("/start:");
        return new SendMessage(chatId, stringBuilder.toString());
    }

    @Override
    public BotApiMethod<Message> callback(Message message) {
        return handle(message);
    }
}
