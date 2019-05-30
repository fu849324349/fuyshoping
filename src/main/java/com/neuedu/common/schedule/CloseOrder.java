package com.neuedu.common.schedule;

import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时关闭订单
 */
@Component
public class CloseOrder {

    //使下面方法每隔一分钟执行一次
    @Scheduled(cron = "0 */1 ****")
    public void closeOrder(){

        int hour = Integer.parseInt(PropertiesUtils.readByKey("close.order.time"));
        //addHour是可以把当前时间加时间的方法，相当于时间相加
        String date =DateUtils.dateToStr(org.apache.commons.lang.time.DateUtils.addHours(new Date(), -hour));


    }


}
