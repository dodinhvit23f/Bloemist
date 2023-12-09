package com.bloemist.schedules;

import com.bloemist.bot.OrderNotificationBot;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BotScheduleRegistry {

  final OrderNotificationBot orderNotificationBot;
  final TelegramBotsApi telegramBotsApi;

  @Scheduled(fixedDelay = Long.MAX_VALUE)
  public void registryBot() throws TelegramApiException {
    telegramBotsApi.registerBot(orderNotificationBot);
  }


}
