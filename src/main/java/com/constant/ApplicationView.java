package com.constant;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC)
public enum ApplicationView {

  LOGIN {
    @Override
    public String getTitle() {
      return Constants.LOGIN_TITLE;
    }

    @Override
    public String getUrl() {
      return "ui/[0001]Login.fxml";
    }
  },
  REGISTRATOR {
    @Override
    public String getTitle() {
      return Constants.ID_REGISTRATION_TITLE;
    }

    @Override
    public String getUrl() {
      return "ui/[0002]IDRegistration.fxml";
    }
  },
  CREATE_ORDER {

    @Override
    public String getUrl() {
      return "ui/[0003]NewOrder.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.NEW_ORDER_TITLE;
    }
  },
  USER_APPOINTMENT {

    @Override
    public String getUrl() {
      return "ui/[0004]UserAppointment.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.USER_APPOINTMENT_TITLE;
    }

  },
  CHANGE_PASSWORD {
    @Override
    public String getUrl() {
      return "ui/[0005]ChangePassword.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.CHANGE_PASSWORD_TITLE;
    }

  },
  RECOVER_PASSWORD {
    @Override
    public String getUrl() {
      return "ui/[0006]RecoverPassword.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.RECOVER_PASSWORD_TITLE;
    }

  },
  MASTER_ORDER {
    @Override
    public String getUrl() {
      return "ui/[0007]MasterOrder.fxml.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.MASTER_ORDER_TITLE;
    }

  },
  INQUIRY_ORDER {
    @Override
    public String getUrl() {
      return "ui/[0008]InquiryOrder.fxml";
    }

    @Override
    public String getTitle() {
      return Constants.INQUIRY_ORDER;
    }

  };

  public abstract String getUrl();

  public abstract String getTitle();


}
