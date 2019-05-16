package com.annotion;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by Dell on 2019/5/15.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SchedulerJob {
    String job();
    String con();
    String triggerName() default "";  //UNION
    String triggerGroupName() default "DEFAULT";
}
