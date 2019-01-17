package com.neuedu.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateUtils {

    private static final String STANARD_FORMAT="yyy-mm-dd:HH:mm:ss";

    /**
     * date格式的时间转成String格式的时间
     */
    public static String dateToStr(Date date,String formate){

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formate);
    }
    //对于上个方法D的重载
    public static String dateToStr(Date date){

        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANARD_FORMAT);
    }
    /**
     * String -->Date
     */
    public static Date strToDate(String str){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANARD_FORMAT);
        DateTime dateTime =dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }
    public static Date strToDate(String str,String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime =dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }



}
