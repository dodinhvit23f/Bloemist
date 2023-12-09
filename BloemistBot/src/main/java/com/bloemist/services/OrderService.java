package com.bloemist.services;

import static com.utils.Utils.formatDate;

import com.bloemist.constant.OrderState;
import com.bloemist.entity.OrderReport;
import com.bloemist.repositories.OrderReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderService {

  static final String NEXT_THREE_DAY_ORDER = "/next_three_days_order";
  static final String SEARCH_BY_PHONE_OR_NAME = "/search_by_phone_or_name";

  final OrderReportRepository orderReportRepository;

  public Optional<List<SendMessage>> runCommand(Update update) {

    String[] command = update.getMessage().getText().split(" ");

    if (ObjectUtils.isEmpty(command)) {
      return Optional.empty();
    }

    switch (command[0]) {
      case NEXT_THREE_DAY_ORDER:
        return findNextThreeDaysOrders(update);
      case SEARCH_BY_PHONE_OR_NAME:
        return findOrderByClientNameOrPhoneNumber(update, command[1]);
    }

    return Optional.empty();
  }

  private Optional<List<SendMessage>> findNextThreeDaysOrders(Update update) {
    List<SendMessage> messages = new LinkedList<>();
    SendMessage title = getSendMessage(update);
    title.setText("Thần xin gửi mẫu: \nTên khách - Trạng Thái - SĐT - Thời gian - Địa chỉ");
    messages.add(title);

    LocalDateTime endDate = LocalDate.now().atStartOfDay().plusDays(3);
    LocalDateTime today = LocalDate.now().atStartOfDay();

    List<OrderReport> ordersReport = orderReportRepository.getOrderInDateRange(
        Date.from(today.atZone(ZoneOffset.systemDefault()).toInstant())
        , Date.from(endDate.atZone(ZoneOffset.systemDefault()).toInstant())
    );

    if (ObjectUtils.isEmpty(ordersReport)) {
      return Optional.empty();
    }

    ordersReport.forEach(orderReport -> {
      SendMessage orderMessage = getSendMessage(update);
      orderMessage.setText(String.format("%s - %s - %s - %s:%s - %s", orderReport.getReceiver(),
          OrderState.values()[orderReport.getOrderStatus()].getStateText(),
          orderReport.getReceiverPhone(),
          formatDate(orderReport.getDeliveryDate()),
          orderReport.getDeliveryTime(),
          orderReport.getDeliveryAddress()));
      messages.add(orderMessage);
    });

    return Optional.of(messages);
  }

  private Optional<List<SendMessage>> findOrderByClientNameOrPhoneNumber(Update update,
      String query) {
    List<SendMessage> messages = new LinkedList<>();
    LocalDateTime startTime = LocalDate.now().minusYears(1).atStartOfDay();

    List<OrderReport> ordersReport = orderReportRepository.searchOrderByPhoneOrNameInDateRange(
        query, Date.from(startTime.atZone(ZoneOffset.systemDefault()).toInstant()),
        Pageable.ofSize(1));

    if (ObjectUtils.isEmpty(ordersReport)) {
      return Optional.empty();
    }

    ordersReport.forEach(orderReport -> {
      SendMessage orderMessage = getSendMessage(update);
      orderMessage.setText(String.format("%s - %s - %s - %s", orderReport.getClientName(),
          orderReport.getClientPhone(),
          orderReport.getClientSocialLink(),
          orderReport.getDeliveryAddress()));
      messages.add(orderMessage);
    });

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
    String nextHour = String.valueOf(LocalDateTime.now().getHour()+ 1);

    Set<OrderReport> orderReports = orderReportRepository
        .getOrderNeedToShipInDateRange(
            Date.from(start.atZone(ZoneOffset.systemDefault()).toInstant()), hour);

    orderReports.addAll(orderReportRepository
        .getOrderNeedToShipInDateRange(
            Date.from(start.atZone(ZoneOffset.systemDefault()).toInstant()), nextHour));

    orderReports = orderReports.stream()
        .filter( orderReport -> {
          String deliveryTime = orderReport.getDeliveryTime();

          if(deliveryTime.contains("-")){
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

    orderReports.stream()
        .map(orderReport -> String.format("%s - %s - Giờ giao %s - %s\n",
            orderReport.getReceiver(),
            orderReport.getReceiverPhone(),
            orderReport.getDeliveryTime(),
            orderReport.getDeliveryAddress()))
        .forEach(content::append);

    title.setText(content.toString());

    return Optional.of(title);
  }

  public Optional<SendMessage> getUnDoneOrder() {
    Date start = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant());

    List<Integer> hours =
        IntStream.range(LocalDateTime.now().getHour(), LocalDateTime.now().getHour() + 2)
        .boxed()
        .toList();

    Set<OrderReport> orderReports = hours.stream()
        .map( hour -> orderReportRepository.getOrderNeedToDoneInDateRange(start,
            String.valueOf(hour)))
        .flatMap(Set::stream)
        .collect(Collectors.toSet());

    orderReports = orderReports.stream()
        .filter( orderReport -> {
          String deliveryTime = orderReport.getDeliveryTime();

          if(deliveryTime.contains("-")){
            String startTime = deliveryTime.split("-")[0];
            return hours.stream().anyMatch(hour -> startTime.startsWith(String.valueOf(hour)));
          }

          return hours.stream().anyMatch(hour -> deliveryTime.startsWith(String.valueOf(hour)));
        }).collect(Collectors.toSet());

    if (ObjectUtils.isEmpty(orderReports)) {
      return Optional.empty();
    }

    SendMessage title = new SendMessage();
    StringBuilder content = new StringBuilder();

    content.append("Đơn cần làm gấp hoàng thượng ơi:\n ");

    orderReports.stream()
        .map(orderReport -> String.format("%s - %s - Giờ giao %s - %s\n",
            orderReport.getReceiver(),
            orderReport.getReceiverPhone(),
            orderReport.getDeliveryTime(),
            orderReport.getDeliveryAddress()))
        .forEach(content::append);

    title.setText(content.toString());

    return Optional.of(title);
  }
}
