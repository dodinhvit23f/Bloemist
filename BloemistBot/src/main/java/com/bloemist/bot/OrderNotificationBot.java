package com.bloemist.bot;

import static com.bloemist.BotConstant.FAIL_MESSAGE;
import static com.bloemist.BotConstant.REPOSE_MESSAGE;

import com.bloemist.services.BotOrderService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderNotificationBot extends TelegramLongPollingBot {

  public static final String MENTION = "mention";
  public static final String BOT_COMMAND = "bot_command";
  @Value("${application.bot.username}")
  String username;

  @Value("${application.bot.token}")
  String token;
  public static final String BOT_NAME = "@bloemist_bot";

  final BotOrderService orderService;


  final Set<Long> chatGroup = new HashSet<>(List.of(-1001895145596L));

  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasMessage() && update.getMessage().hasText() &&
        chatGroup.contains(update.getMessage().getChatId()) &&
        !ObjectUtils.isEmpty(update.getMessage().getEntities())) {

      Optional<MessageEntity> messageEntity = update.getMessage().getEntities()
          .stream()
          .filter(entity -> entity.getType().equals(MENTION) &&
              entity.getText().equals(BOT_NAME))
          .findFirst();

      messageEntity.ifPresent( m -> {

        Optional<MessageEntity> messageCommand = update.getMessage().getEntities()
            .stream()
            .filter(entity -> entity.getType().equals(BOT_COMMAND))
            .findFirst();

        messageCommand.ifPresent(command -> {
          SendMessage message = getSendMessage(update, REPOSE_MESSAGE);
          sendMessage(message);

          Optional<List<SendMessage>> optional =  orderService.runCommand(update, command.getText());
          optional.ifPresentOrElse(sendMessages -> sendMessages.forEach(this::sendMessage),
              () -> sendMessage(getSendMessage(update, FAIL_MESSAGE)));
        });
      });


    }
  }

  @Override
  public String getBotUsername() {
    return username;
  }

  @Override
  public String getBotToken() {
    return token;
  }

  private SendMessage getSendMessage(Update update, String messageText) {
    SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
    message.setChatId(update.getMessage().getChatId());
    message.setText(messageText);
    message.setMessageThreadId(update.getMessage().getMessageThreadId());
    message.setReplyToMessageId(update.getMessage().getMessageId());
    return message;
  }

  private void sendMessage(SendMessage message) {
    try {
      execute(message); // Call method to send the message
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  // Run every hour
/*  @Scheduled(cron = "0 0 * * * *")
  public void shippingNotification(){
    Optional<SendMessage> optional =  orderService.findOrderNeedToShip();
    optional.ifPresent(message -> {
      chatGroup.stream().findFirst().ifPresent(id -> message.setChatId(id));
      sendMessage(message);
    });
  }

  @Scheduled(cron = "0 0 * * * *")
  public void notifyUnDoneOrder(){
    Optional<SendMessage> optional =  orderService.getUnDoneOrder();
    optional.ifPresent(message -> {
      chatGroup.stream().findFirst().ifPresent(id -> message.setChatId(id));
      sendMessage(message);
    });
  }*/


}
