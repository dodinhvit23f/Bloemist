package com.bloemist;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import com.bloemist.BloemistUIApplication.StageReadyEvent;

/**
 * @author Do Dinh Tien
 * @date Dec 2, 2022
 */
@Component
public class BloemistStageInitializer implements ApplicationListener<StageReadyEvent>{

  @Override
  public void onApplicationEvent(StageReadyEvent event) {
   event.getSource();
    
  }

}
