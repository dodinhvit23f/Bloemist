package com.bloemist.services;

import static com.bloemist.bot.OrderNotificationBot.BOT_NAME;
import static com.utils.Utils.currencyFormat;
import static com.utils.Utils.formatDate;

import com.bloemist.bot.OrderNotificationBot;
import com.bloemist.constant.OrderState;
import com.bloemist.entity.OrderReport;
import com.bloemist.repositories.OrderReportRepository;
import com.utils.Utils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotOrderService {

  static final String TODAY_ORDER = "/order_1";
  static final String NEXT_THREE_DAY_ORDER = "/order_3";
  static final String SEARCH_BY_PHONE_OR_NAME = "/customers";
  static final String SEARCH_BY_DATE = "/date";
  static final String SEARCH_ON_DEBIT = "/debts";
  static final String DELETE = "/clear";
  static final String HELP = "/help";
  public static final String END_LINE = "====================================================\n";

  private static final String COMMON_MESSAGE =
      "%d - [Trạng thái đơn]: %s\n[Giá niêm yết]: %s\n[Giờ giao]: %s\n[Ngày giao]: %s\n"
          + "[Tên người đặt]: %s\n[SĐT]: %s\n[Chi tiết đơn hàng]: %s\n"
          + "[Nội dung banner]: %s \n[Ghi chú]: %s \n[Địa chỉ giao]: %s\n"
          + "[Người nhận]: %s \n[SĐT người nhận]: %s \n[Link ảnh mẫu]: %s \n"
          + "[Link FB người đặt]: %s";
  final OrderReportRepository orderReportRepository;

  public Optional<List<SendMessage>> runCommand(Update update, String inputCommand,
      OrderNotificationBot orderNotificationBot) {

    String[] commands = update.getMessage().getText().split(BOT_NAME, 2);

    if (ObjectUtils.isEmpty(inputCommand)) {
      return Optional.empty();
    }

    LocalDateTime today = LocalDate.now().atStartOfDay();
    switch (inputCommand) {
      case NEXT_THREE_DAY_ORDER:
        return findOrdersByDateRange(update, today.plusDays(1), today.plusDays(4));
      case TODAY_ORDER:
        return findOrdersByDateRange(update, today, today.plusDays(1));
      case SEARCH_BY_PHONE_OR_NAME:
        return findOrderByClientNameOrPhoneNumber(update, commands[1].strip());
      case SEARCH_BY_DATE:
        return findOrderByDate(update, commands[1].strip());
      case SEARCH_ON_DEBIT:
        return findOrdersOnDebit(update);
      case HELP:
        return help(update);
      case DELETE:
         clearAllMessage(update, orderNotificationBot);
    }

    return Optional.empty();
  }

  private void clearAllMessage(Update update, OrderNotificationBot bot){
   /* GetUpdates getUpdates =  GetUpdates.builder()
        .offset(0)
        .allowedUpdates(List.of(""))
        .timeout(0)
        .limit(100).build();

    List<ChatMember> administrators = null;
    try {
       bot.execute(getUpdates);

    *//*  bot.execute(getChatAdministrators)
          .stream().filter( administrator -> administrator.getUser().getId().equals(6334256549L))
          .findFirst()
          .ifPresent(administrator ->{
            administrator.getStatus();
          });*//*
      System.out.println();

    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }*/

    // Process the list of administrators or chats

  }

  public Optional<List<SendMessage>> help(Update update) {
    List<SendMessage> messages = new LinkedList<>();

    SendMessage title = getSendMessage(update);
    messages.add(title);

    AtomicInteger integer = new AtomicInteger(1);

    StringBuilder content = new StringBuilder();
    content.append("Ngài ơi chú ý!!!!! Những câu lệnh hỗ trợ:\n");

    content.append(String.format("%d. %s - Tìm kiếm các đơn trong hôm nay\n"
        , integer.getAndIncrement(), TODAY_ORDER));

    content.append(String.format("%d. %s - Tìm kiếm các đơn trong 3 ngày tiếp theo\n"
        , integer.getAndIncrement(), NEXT_THREE_DAY_ORDER));

    content.append(
        String.format("%d.%s - Tìm kiếm các đơn theo tên hoặc số diện thoại khách hàng\n"
            , integer.getAndIncrement(), SEARCH_BY_PHONE_OR_NAME));

    content.append(String.format("%d.%s - Tìm kiếm các đơn theo ngày như là 20-10-2023\n"
        , integer.getAndIncrement(), SEARCH_BY_DATE));

    content.append(String.format("%d.%s - Tìm kiếm các đơn đang nợ\n"
        , integer.getAndIncrement(), SEARCH_ON_DEBIT));


    title.setText(content.toString());

    return Optional.of(messages);
  }

  public Optional<List<SendMessage>> findOrdersOnDebit(Update update) {

    List<SendMessage> messages = new LinkedList<>();

    Page<OrderReport> ordersReport = orderReportRepository.findOrdersInDebit(Pageable.ofSize(30));

    if (ObjectUtils.isEmpty(ordersReport.getContent())) {
      Optional.of(messages);
    }

    fillDataOnDebit(update, ordersReport, messages);

    return Optional.of(messages);
  }

  private Optional<List<SendMessage>> findOrderByDate(Update update, String strip) {

    List<SendMessage> messages = new LinkedList<>();
    SendMessage title = getSendMessage(update);
    messages.add(title);

    if (ObjectUtils.isEmpty(strip)) {
      title.setText("Ngày không được để trống đâu hoàng thượng, ngài điền như này nhé 20-10-2023");
      return Optional.of(messages);
    }

    LocalDateTime searchDay = LocalDateTime.ofInstant(Utils.toDate(strip).toInstant(),
            ZoneId.systemDefault())
        .toLocalDate().atStartOfDay();
    LocalDateTime nextDay = searchDay.plusDays(1);

    List<OrderReport> ordersReport = orderReportRepository.getOrderInDateRangeRoleAdmin(
        Date.from(searchDay.atZone(ZoneOffset.systemDefault()).toInstant())
        , Date.from(nextDay.atZone(ZoneOffset.systemDefault()).toInstant())
    );

    AtomicInteger integer = new AtomicInteger(1);

    if (ObjectUtils.isEmpty(ordersReport)) {
      return Optional.empty();
    }

    StringBuilder content = new StringBuilder();
    fillData(title, content, ordersReport, integer);

    return Optional.of(messages);
  }
  private Optional<List<SendMessage>> findOrdersByDateRange(Update update, LocalDateTime today,
                                                            LocalDateTime endDate) {
    List<SendMessage> messages = new LinkedList<>();
    SendMessage title = getSendMessage(update);
    messages.add(title);

    StringBuilder content = new StringBuilder();
    content.append("Thần xin gửi mẫu:");

    title.setText(content.toString());

    List<OrderReport> ordersReport = orderReportRepository.getOrderInDateRange(
        Date.from(today.atZone(ZoneOffset.systemDefault()).toInstant())
        , Date.from(endDate.atZone(ZoneOffset.systemDefault()).toInstant())
    );

    if (ObjectUtils.isEmpty(ordersReport)) {
      return Optional.empty();
    }

    for (int i = 0; i < ordersReport.size(); i++) {
      OrderReport orderReport = ordersReport.get(i);
      String orderMessage = String.format(COMMON_MESSAGE,
          i + 1,
          OrderState.values()[orderReport.getOrderStatus()].getStateTextWithoutNumber(),
          currencyFormat(orderReport.getSalePrice().doubleValue()),
          orderReport.getDeliveryTime(),
          formatDate(orderReport.getDeliveryDate()),
          orderReport.getClientName(),
          orderReport.getClientPhone(),
          orderReport.getOrderDescription(),
          orderReport.getBannerContent(),
          orderReport.getRemark(),
          orderReport.getDeliveryAddress(),
          orderReport.getReceiver(),
          orderReport.getReceiverPhone(),
          orderReport.getSamplePictureLink(),
          orderReport.getClientSocialLink());

      SendMessage order = getSendMessage(update);
      order.setText(orderMessage);
      messages.add(order);
    }

    return Optional.of(messages);
  }
  private SendMessage getSendMessage(Update update) {
    SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
    message.setChatId(update.getMessage().getChatId());
    message.setMessageThreadId(update.getMessage().getMessageThreadId());
    message.setReplyToMessageId(update.getMessage().getMessageId());
    return message;
  }

  public Optional<SendMessage> findOrderNeedToShip() {
    SendMessage title = new SendMessage();
    LocalDateTime start = LocalDate.now().atStartOfDay();

    String hour = String.valueOf(LocalDateTime.now().getHour());
    String nextHour = String.valueOf(LocalDateTime.now().getHour() + 1);

    Set<OrderReport> orderReports = orderReportRepository
        .getOrderNeedToShipInDateRange(
            Date.from(start.atZone(ZoneOffset.systemDefault()).toInstant()), hour);

    orderReports.addAll(orderReportRepository
        .getOrderNeedToShipInDateRange(
            Date.from(start.atZone(ZoneOffset.systemDefault()).toInstant()), nextHour));

    orderReports = orderReports.stream()
        .filter(orderReport -> {
          String deliveryTime = orderReport.getDeliveryTime();

          if (deliveryTime.contains("-")) {
            String startTime = deliveryTime.split("")[0];
            return startTime.startsWith(hour) ||
                startTime.startsWith(nextHour);
          }

          return deliveryTime.startsWith(hour) ||
              deliveryTime.startsWith(nextHour);
        }).collect(Collectors.toSet());

    if (ObjectUtils.isEmpty(orderReports)) {
      return Optional.empty();
    }

    StringBuilder content = new StringBuilder();
    content.append("Đơn cần ship trong 1h tới:\n");
    remindContent(content, orderReports, title);

    return Optional.of(title);
  }

  public Optional<SendMessage> getUnDoneOrder() {

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime start =  LocalDateTime.now().toLocalDate().atTime(now.getHour(), now.getMinute());
    LocalDateTime end = start.plusHours(2);

    Set<OrderReport> orderReports = orderReportRepository.getOrderNeedToDoneInDateRange(start,end);

    if (ObjectUtils.isEmpty(orderReports)) {
      return Optional.empty();
    }

    SendMessage title = new SendMessage();
    StringBuilder content = new StringBuilder();
    content.append("Đơn cần làm gấp hoàng thượng ơi:\n ");

    remindContent(content, orderReports, title);

    title.setText(content.toString());

    return Optional.of(title);
  }

  private void remindContent(StringBuilder content, Set<OrderReport> orderReports,
                             SendMessage title) {
    content.append(
        "Giờ giao - giá niêm yết - người đặt - mô tả đơn - banner - ghi chú - người nhận - sdt - địa chỉ: \n");
    orderReports
        .forEach(orderReport -> {
          content.append(String.format("%s - %s - %s - %s - %s - %s - %s - %s - %s\n",
              orderReport.getDeliveryTime(),
              orderReport.getSalePrice(),
              orderReport.getClientName(),
              orderReport.getOrderDescription(),
              orderReport.getBannerContent(),
              orderReport.getRemark(),
              orderReport.getReceiver(),
              orderReport.getReceiverPhone(),
              orderReport.getDeliveryAddress()));
          content.append(END_LINE);
        });

    title.setText(content.toString());
  }
  private Optional<List<SendMessage>> findOrderByClientNameOrPhoneNumber(Update update,
                                                                         String query) {
    List<SendMessage> messages = new LinkedList<>();
    LocalDateTime startTime = LocalDate.now().minusYears(1).atStartOfDay();

    List<OrderReport> ordersReport = orderReportRepository.searchOrderByPhoneOrNameInDateRange(
        query, Date.from(startTime.atZone(ZoneOffset.systemDefault()).toInstant()),
        Pageable.ofSize(10));

    if (ObjectUtils.isEmpty(ordersReport)) {
      return Optional.empty();
    }
    StringBuilder content = new StringBuilder();

    AtomicInteger integer = new AtomicInteger(1);

    ordersReport.forEach(orderReport -> {
      content.append(String.format("%d %s - %s - %s - %s\n",
          integer.getAndIncrement(),
          orderReport.getClientName(),
          orderReport.getClientPhone(),
          orderReport.getClientSocialLink(),
          orderReport.getDeliveryAddress()));
      content.append(END_LINE);
    });

    SendMessage sendMessage = getSendMessage(update);
    sendMessage.setText(content.toString());

    messages.add(sendMessage);

    return Optional.of(messages);
  }
  private void fillData(SendMessage title, StringBuilder content,
                        List<OrderReport> ordersReport, AtomicInteger integer) {
    ordersReport.forEach(orderReport -> {
      content.append(String.format("%d. [Trạng Thái]: %s - [Tên khách]: %s - [SĐT]:%s - [Thời gian]: %s:%s - [Địa chỉ]: %s\n",
          integer.getAndIncrement(),
          OrderState.values()[orderReport.getOrderStatus()].getStateTextWithoutNumber(),
          orderReport.getReceiver(),
          orderReport.getReceiverPhone(),
          formatDate(orderReport.getDeliveryDate()),
          orderReport.getDeliveryTime(),
          orderReport.getDeliveryAddress()));
    });

    title.setText(content.toString());
  }

  private void fillDataOnDebit(Update update, Page<OrderReport> ordersReport,
                               List<SendMessage> messages) {
    AtomicInteger integer = new AtomicInteger(1);
    StringBuilder content = new StringBuilder();


    while (Boolean.TRUE) {
      SendMessage title = getSendMessage(update);
      messages.add(title);

      ordersReport.forEach(orderReport -> {
        content.append(String.format("%d.[Trạng Thái]: %s - [Tên khách]: %s - [SĐT]: %s - [Thời gian đặt]: %s\n",
            integer.getAndIncrement(),
            OrderState.values()[orderReport.getOrderStatus()].getStateText(),
            orderReport.getClientName(),
            orderReport.getClientPhone(),
            formatDate(orderReport.getOrderDate())));
      });

      title.setText(content.toString());

      content.delete(0, title.getText().length());

      if (!ordersReport.hasNext()) {
        return;
      }
      ordersReport = orderReportRepository.findOrdersInDebit(
          PageRequest.of(ordersReport.getNumber() + 1, 30));
    }
  }
}
