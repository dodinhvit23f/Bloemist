package com.bloemist.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfiguration {

  @Bean
  @Primary
  TelegramBotsApi telegramBotsApi() throws TelegramApiException {
    return new TelegramBotsApi(DefaultBotSession.class);
  }

}
