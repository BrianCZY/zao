package com.hzu.zao.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间 格式化、截取、转换 工具类
 */
public class DateUtils {

    public static SimpleDateFormat dateFormat;
    public static SimpleDateFormat timeFormat;
    public static SimpleDateFormat longFormat;
    public static Calendar calendar;

    private static final String TAG = "DateUtils";
    private static final String YESTERDAY = "昨天 ";
    private static final String BEFORE_YESTERDAY = "前天 ";


    private static final int HOUR_SDUR = 1;//两个时间段相隔的时间

    private static final int MINUT = 1;
    private static final int HOUR = 2;


    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("hh:mm:ss");
        longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        calendar = Calendar.getInstance();
        String timezoneID = TimeZone.getDefault().getID();

    }

    public static String getOffsetDate(String date, int offset) {
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, offset);
        return dateFormat.format(calendar.getTime());
    }

    public static int getDayOfWeek(String date) {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getCurrentStringDate() {
        return dateFormat.format(calendar.getTime());
    }

    public static String getCurrentStringTime() {
        return timeFormat.format(calendar.getTime());
    }


    public static String getCurrentLongDate() {
        return longFormat.format(calendar.getTime());
    }

    public static Date getgetCurrentDate() {
        return calendar.getTime();
    }

    public static Date convertStr2Date(String str) {
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将长的字符串转换成Date格式
     *
     * @param str
     * @return
     */
    public static Date convertStr2LongDate(String str) {
        try {
            return longFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isFuture(String goalDate) {
        return convertStr2Date(goalDate).after(calendar.getTime());
    }

    /**
     * @param smdate 最新一个时间
     * @param bdate  一个时间
     * @param i      0-->天，  1 --> 分钟，  2-->小时
     * @return 两个时间差
     */
    public static int Between(String smdate, String bdate, int i) {

        long between_days = 0;

        try {
            calendar.setTime(dateFormat.parse(smdate));
            long time1 = calendar.getTimeInMillis();
            calendar.setTime(dateFormat.parse(bdate));
            long time2 = calendar.getTimeInMillis();
            between_days = (time2 - time1) / (1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (i) {
            case MINUT:
                return Integer.parseInt(String.valueOf(between_days / 60));
            case HOUR:
                return Integer.parseInt(String.valueOf(between_days / 3600));
            default:
                return Integer.parseInt(String.valueOf(between_days / 3600 / 24));

        }


    }

    /**
     * 小时
     * 比较两个时间之间相差多少小时
     *
     * @param bdate 前一个时间
     * @param adate 当前时间
     * @return
     */
    public static int dayBetween(String bdate, String adate) {

        return Between(bdate, adate, 0);
    }

    /**
     * 小时
     * 比较两个时间之间相差多少小时
     *
     * @param bdate 前一个时间
     * @param adate 当前时间
     * @return
     */
    public static int hourBetween(String bdate, String adate) {
        LogUtils.e("时间差 " + Between(bdate, adate, 2) + " bdate = " + bdate + " adate " + adate);
        return Between(bdate, adate, 2);
    }

    /**
     * 分钟
     * 比较两个时间之间相差多少小时
     *
     * @param bdate 前一个时间
     * @param adate 当前时间
     * @return
     */
    public static int minuBetween(String bdate, String adate) {

        return Between(bdate, adate, 1);
    }


    /**
     * 比较两个时间 超过一个小时的记录则显示时间
     *
     * @param tempTime
     * @param currentTime
     * @return
     */
    public static boolean compearString(String tempTime, String currentTime) {

        int hours = DateUtils.hourBetween(tempTime, currentTime);

        return HOUR_SDUR <= hours;
    }

    /**
     * 转换成字符串，比较时间，是当天显示小时分钟，前一天显示昨天，再之前就显示完整一天
     *
     * @param date
     * @return
     */
    public static String convertDate2Str(Date date) {

        String mDate = null;

        //获取当前时间
        Calendar calendar = Calendar.getInstance();
        Long timeStamp = calendar.getTimeInMillis();
        Date nowDate = new Date(timeStamp);

        //得到两个时间相差的天数
        int day = (int) ((nowDate.getTime() - date.getTime()) / (24 * 60 * 60 * 1000));

        switch (day) {
            case 0:
                mDate = longFormat.format(date).substring(11, 16);
                break;
            case 1:
                mDate = YESTERDAY + longFormat.format(date).substring(11, 16);
                break;
            case 2:
                mDate = BEFORE_YESTERDAY + longFormat.format(date).substring(11, 16);
                break;
            default:
                mDate = longFormat.format(date).substring(5, 16);
                break;
        }


        return mDate;
    }

    /**
     * @param date
     * @return
     */

    public static String convertDate2Str(String date) {
        return convertDate2Str(convertStr2LongDate(date));

    }

    /**
     * 返回时间字符串的日期
     *
     * @param dateStr
     * @return
     */
    public static String getDay(String dateStr) {
        return dateStr.substring(8, 10);

    }

    /**
     * 返回时间字符串的月份
     *
     * @param dateStr
     * @return
     */
    public static String getMonth(String dateStr) {

        return String.valueOf(Integer.valueOf(dateStr.substring(5, 7)));
    }


    public static String convertTimeStamp2Time(long timestamp) {
        Date date = new Date(timestamp);
        return DateUtils.convertDate2Str(date);
    }

    /***
     * 比较两个字符串的时间，是否是同一天
     * 同一天 return true
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isTheSameDay(String date1, String date2) {
        Date formateDate1 = convertStr2LongDate(date1);
        Date formateDate2 = convertStr2LongDate(date2);
//        boolean isTheSameDay =
        return formateDate1 != null && formateDate2 != null
                && formateDate1.getYear() == formateDate2.getYear()
                && formateDate1.getMonth() == formateDate2.getMonth()
                && formateDate1.getDay() == formateDate2.getDay();

//        LogUtils.E("date1 = " + date1 + " date2 = " + date2 + " is the same day = " + isTheSameDay);
//        return isTheSameDay;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static Date getNextDay(Date d ,int delay) {
        try {
            String mdate = "";
            long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
            d.setTime(myTime * 1000);
//            mdate = dateFormat.format(d);
            return d;
        } catch (Exception e) {
            return calendar.getTime();
        }
    }

}