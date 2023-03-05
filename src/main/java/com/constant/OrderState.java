package com.constant;

public  enum OrderState {

  PENDING  ("Chưa giải quyết"),
  IN_PROCESS ( "Đang xử lý"),
  DONE_PROCESS ( "Đã xử lý"),
  IN_DELIVERY ( "Đang giao"),
  DONE_DELIVERY ( "Đã giao"),
  IN_DEBIT ("Đang nợ"),
  DONE ("Hoàn Thành"),
  CANCEL("Hủy");

  private final String text;

  OrderState(String text) {
    this.text = text;
  }

  public String getState() {
    return text;
  }
}
