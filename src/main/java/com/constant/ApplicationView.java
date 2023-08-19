package com.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
public enum ApplicationView {
  PRINT_ORDER {
    @Override
    public String getUrl() {
      return "classpath:ui/0014-SubScreen-PrintVoucher.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.PRINT_ORDER;
    }

    @Override
    public String getCode() {
      return "0014";
    }
  },
  SUB_ORDER_SCREEN {
    @Override
    public String getUrl() {
      return "classpath:ui/0010-SubScreen-OrderStatus.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.SUB_ORDER_SCREEN;
    }

    @Override
    public String getCode() {
      return "0010";
    }
  },
  LOGIN {
    @Override
    public String getTitle() {
      return TitleConstants.LOGIN_TITLE;
    }

    @Override
    public String getUrl() {
      return "classpath:ui/0001-Login.fxml";
    }

    @Override
    public String getCode() {
      return "0001";
    }
  },
  REGISTRATOR {
    @Override
    public String getTitle() {
      return TitleConstants.ID_REGISTRATION_TITLE;
    }

    @Override
    public String getUrl() {
      return "classpath:ui/0002-IDRegistration.fxml";
    }

    @Override
    public String getCode() {
      return "0002";
    }
  },
  CREATE_ORDER {

    @Override
    public String getUrl() {
      return "classpath:ui/0003-NewOrder.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.NEW_ORDER_TITLE;
    }

    @Override
    public String getCode() {
      return "0003";
    }
  },
  USER_APPOINTMENT {

    @Override
    public String getUrl() {
      return "classpath:ui/0004-UserAppointment.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.USER_APPOINTMENT_TITLE;
    }

    @Override
    public String getCode() {
      return "0004";
    }
  },
  CHANGE_PASSWORD {
    @Override
    public String getUrl() {
      return "classpath:ui/0005-ChangePassword.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.CHANGE_PASSWORD_TITLE;
    }

    @Override
    public String getCode() {
      return "0005";
    }
  },
  RECOVER_PASSWORD {
    @Override
    public String getUrl() {
      return "classpath:ui/0006-RecoverPassword.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.RECOVER_PASSWORD_TITLE;
    }

    @Override
    public String getCode() {
      return "0006";
    }
  },
  MASTER_ORDER {
    @Override
    public String getUrl() {
      return "classpath:ui/0007-TotalOrdersReport.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.MASTER_ORDER_TITLE;
    }

    @Override
    public String getCode() {
      return "0007";
    }
  },
  INQUIRY_ORDER {
    @Override
    public String getUrl() {
      return "classpath:ui/0008-OrdersReport.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.INQUIRY_ORDER_TITLE;
    }

    @Override
    public String getCode() {
      return "0008";
    }
  },
  EMPLOYEES_MANAGEMENT {
    @Override
    public String getUrl() {
      return "classpath:ui/0009-EmployeesManagement.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.INQUIRY_ORDER_TITLE;
    }

    @Override
    public String getCode() {
      return "0008";
    }
  },
  HOME {
    @Override
    public String getUrl() {
      return "classpath:ui/Home.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.HOME_TITLE;
    }

    @Override
    public String getCode() {
      return "0000";
    }
  },
  CHANGE_USER_INFO {
    @Override
    public String getUrl() {
      return "classpath:ui/0013-ChangEmpInfo.fxml";
    }

    @Override
    public String getTitle() {
      return TitleConstants.CHANGE_USER_INFORMATION_TITLE;
    }

    @Override
    public String getCode() {
      return "0013";
    }
  };

  public abstract String getUrl();

  public abstract String getTitle();

  public abstract String getCode();


}
