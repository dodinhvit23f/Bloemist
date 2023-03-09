package com.constant;

public enum OrderState {

  PENDING(0),
  IN_PROCESS(1),
  DONE_PROCESS(2),
  IN_DELIVERY(3),
  DONE_DELIVERY(4),
  IN_DEBIT(5),
  DONE(6),
  CANCEL(7);

  public static final String PENDING_TEXT = "Chưa giải quyết";
  public static final String IN_PROCESS_TEXT = "Đang xử lý";
  public static final String DONE_PROCESS_TEXT = "Đã xử lý";
  public static final String IN_DELIVERY_TEXT = "Đang giao";
  public static final String DONE_DELIVERY_TEXT = "Đã giao";
  public static final String IN_DEBIT_TEXT = "Đang nợ";
  public static final String DONE_TEXT = "Hoàn Thành";
  public static final String CANCEL_TEXT = "Hủy";
  private final int number;

  OrderState(int number) {
    this.number = number;
  }

  public String getStateText() {

    switch (this) {
      case PENDING:
        return PENDING_TEXT;
      case IN_PROCESS:
        return IN_PROCESS_TEXT;
      case DONE_PROCESS:
        return DONE_PROCESS_TEXT;
      case IN_DELIVERY:
        return IN_DELIVERY_TEXT;
      case DONE_DELIVERY:
        return DONE_DELIVERY_TEXT;
      case IN_DEBIT:
        return IN_DEBIT_TEXT;
      case DONE:
        return DONE_TEXT;
      case CANCEL:
        return CANCEL_TEXT;
    }
    return PENDING_TEXT;
  }

  public int getState() {
    return this.number;
  }

  public static int getState(String text) {
    switch (text) {
      case PENDING_TEXT:
        return PENDING.getState();
      case IN_PROCESS_TEXT:
        return IN_PROCESS.getState();
      case DONE_PROCESS_TEXT:
        return DONE_PROCESS.getState();
      case IN_DELIVERY_TEXT:
        return IN_DELIVERY.getState();
      case DONE_DELIVERY_TEXT:
        return DONE_DELIVERY.getState();
      case IN_DEBIT_TEXT:
        return IN_DEBIT.getState();
      case DONE_TEXT:
        return DONE.getState();
      case CANCEL_TEXT:
        return CANCEL.getState();
    }
    return PENDING.getState();
  }


}
