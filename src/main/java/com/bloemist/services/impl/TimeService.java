package com.bloemist.services.impl;

import com.bloemist.events.MessageWarning;
import com.bloemist.services.ITimeService;
import com.constant.Constants;
import com.utils.Utils;
import java.math.BigInteger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class TimeService implements ITimeService {

  ApplicationEventPublisher publisher;

  @Override
  public  boolean validateTime(String[] deliveryTimeAr) {
    if (deliveryTimeAr.length != BigInteger.TWO.intValue()) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_003));
      return Boolean.FALSE;
    }

    if (!Utils.isNumber(deliveryTimeAr[BigInteger.ZERO.intValue()]) ||
        !Utils.isNumber(deliveryTimeAr[BigInteger.ONE.intValue()])) {
      publisher.publishEvent(new MessageWarning(Constants.ERR_ORDER_INFO_002));
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }
}
