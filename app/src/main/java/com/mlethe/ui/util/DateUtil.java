package com.mlethe.ui.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * 获取今年是哪一年
     *
     * @return
     */
    public static int getNowYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取本月是哪一月
     *
     * @return
     */
    public static int getNowMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取今天是多少号
     *
     * @return
     */
    public static int getNowDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取当前星期几
     *
     * @return
     */
    public static int getNowWeek() {
        Calendar cal = Calendar.getInstance();
        int a = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (a == 0) {
            a = 7;
        }
        return a;
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static int getNowHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟
     *
     * @return
     */
    public static int getNowMinute() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @return
     */
    public static int getNowSecond() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.SECOND);
    }

    /**
     * 获取当前毫秒
     *
     * @return
     */
    public static int getNowMillisecond() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MILLISECOND);
    }

    /**
     * 获取本周的开始时间
     *
     * @return
     */
    public static Date getBeginDayOfWeek() {
        return getDayBeginOfWeek(1, 0, 0, 0);
    }

    /**
     * 获取本周的结束时间
     *
     * @return
     */
    public static Date getEndDayOfWeek() {
        return getDayEndOfWeek(7, 0, 23, 59);
    }

    /**
     * 获取本周的某一天某时某分
     *
     * @param dayOfWeek 周几
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getDayOfWeek(int dayOfWeek, int hour, int minute) {
        return getDayEndOfWeek(dayOfWeek, 0, hour, minute);
    }

    /**
     * 获取下一周的某一天某时某分
     *
     * @param dayOfWeek 周几
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getDayOfNextWeek(int dayOfWeek, int hour, int minute) {
        return getDayEndOfWeek(dayOfWeek, 1, hour, minute);
    }

    /**
     * 获取上一周的某一天某时某分
     *
     * @param dayOfWeek 周几
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getDayOfPreWeek(int dayOfWeek, int hour, int minute) {
        return getDayEndOfWeek(dayOfWeek, -1, hour, minute);
    }

    /**
     * 获取某周某天某时某分59秒999毫秒
     *
     * @param dayOfWeek  周几
     * @param weekOffset 周偏移量
     * @param hour       小时
     * @param minute     分钟
     * @return
     */
    public static Date getDayEndOfWeek(int dayOfWeek, int weekOffset, int hour, int minute) {
        return getDayOfWeek(dayOfWeek, weekOffset, hour, minute, 59, 999);
    }

    /**
     * 获取某周某天某时某分0秒0毫秒
     *
     * @param dayOfWeek  周几
     * @param weekOffset 周偏移量
     * @param hour       小时
     * @param minute     分钟
     * @return
     */
    public static Date getDayBeginOfWeek(int dayOfWeek, int weekOffset, int hour, int minute) {
        return getDayOfWeek(dayOfWeek, weekOffset, hour, minute, 0, 0);
    }

    /**
     * 获取当天整点
     *
     * @return
     */
    public static Date getHourWholePoint() {
        return getHourWholePoint(0);
    }

    /**
     * 获取的第二天当前整点
     *
     * @return
     */
    public static Date getNextDayWholePoint() {
        return getDayHourWholePoint(1);
    }

    /**
     * 获取第二天某时的整点
     *
     * @param hour 小时
     * @return
     */
    public static Date getNextDayWholePoint(int hour) {
        return getDayHourWholePoint(1, hour);
    }

    /**
     * 获取当天结束时间
     *
     * @return
     */
    public static Date getDayEnd() {
        return getDay(0, 23, 59, 59, 999);
    }

    /**
     * 获取当天开始时间
     *
     * @return
     */
    public static Date getDayBegin() {
        return getDay(0, 0, 0, 0, 0);
    }

    /**
     * 获取明天某时某分59秒999毫秒
     *
     * @param hour   小时
     * @param minute 分钟
     * @return
     */
    public static Date getNextDayEnd(int hour, int minute) {
        return getDay(1, hour, minute, 59, 999);
    }

    /**
     * 获取明天某时某分0秒0毫秒
     *
     * @param hour   小时
     * @param minute 分钟
     * @return
     */
    public static Date getNextDayBegin(int hour, int minute) {
        return getDay(1, hour, minute, 0, 0);
    }

    /**
     * 获取昨天某时某分59秒999毫秒
     *
     * @param hour   小时
     * @param minute 分钟
     * @return
     */
    public static Date getPreDayEnd(int hour, int minute) {
        return getDay(-1, hour, minute, 59, 999);
    }

    /**
     * 获取昨天某时某分0秒0毫秒
     *
     * @param hour   小时
     * @param minute 分钟
     * @return
     */
    public static Date getPreDayBegin(int hour, int minute) {
        return getDay(-1, hour, minute, 0, 0);
    }

    /**
     * 获取某天某时某分59秒999毫秒
     *
     * @param dayOffset 天偏移量
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getDayEnd(int dayOffset, int hour, int minute) {
        return getDay(dayOffset, hour, minute, 59, 999);
    }

    /**
     * 获取某天某时某分0秒0毫秒
     *
     * @param dayOffset 天偏移量
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getDayBegin(int dayOffset, int hour, int minute) {
        return getDay(dayOffset, hour, minute, 0, 0);
    }

    /**
     * 获取某小时整点
     *
     * @param hour 小时
     * @return
     */
    public static Date getHourBegin(int hour) {
        return getDayBegin(0, hour, 0);
    }

    /**
     * 获取某小时结束
     *
     * @param hour 小时
     * @return
     */
    public static Date getHourEnd(int hour) {
        return getDayEnd(0, hour, 59);
    }

    /**
     * 获取本月的结束时间
     *
     * @return
     */
    public static Date getDayEndOfMonth() {
        return getDayEndOfCurMonth(0);
    }

    /**
     * 获取本月的开始时间
     *
     * @return
     */
    public static Date getDayBeginOfMonth() {
        return getDayBeginOfCurMonth(0);
    }

    /**
     * 获取某月最后一天结束时间
     *
     * @param monthOfYear 月份
     * @return
     */
    public static Date getDayEndOfMonth(int monthOfYear) {
        return getDayEndOfMonth(monthOfYear, 23, 59, 59, 999);
    }

    /**
     * 获取某月第一天开始时间
     *
     * @param monthOfYear 月份
     * @return
     */
    public static Date getDayBeginOfMonth(int monthOfYear) {
        return getDayBeginOfMonth(monthOfYear, 0, 0, 0, 0);
    }

    /**
     * 获取某月最后一天结束时间
     *
     * @param monthOffset 月偏移量
     * @return
     */
    public static Date getDayEndOfCurMonth(int monthOffset) {
        return getDayEndOfCurMonth(monthOffset, 23, 59, 59, 999);
    }

    /**
     * 获取某月第一天开始时间
     *
     * @param monthOffset 月偏移量
     * @return
     */
    public static Date getDayBeginOfCurMonth(int monthOffset) {
        return getDayBeginOfCurMonth(monthOffset, 0, 0, 0, 0);
    }

    /**
     * 获取某月某天某时某分59秒999毫秒
     *
     * @param monthOffset   月偏移量
     * @param hour          小时
     * @param minute        分钟
     * @return
     */
    public static Date getDayEndOfMonth(int dayOfMonth, int monthOffset, int hour, int minute) {
        return getDayOfMonth(dayOfMonth, monthOffset, hour, minute, 59, 999);
    }

    /**
     * 获取某月某天某时某分某秒某毫秒
     *
     * @param dayOfMonth    几号
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayOfMonth(int dayOfMonth, int hour, int minute, int second, int millisecond) {
        return getDayOfMonth(dayOfMonth, 0, hour, minute, second, millisecond);
    }

    /**
     * 获取某月某天某时某分0秒0毫秒
     *
     * @param monthOffset   月偏移量
     * @param hour          小时
     * @param minute        分钟
     * @return
     */
    public static Date getDayBeginOfMonth(int dayOfMonth, int monthOffset, int hour, int minute) {
        return getDayOfMonth(dayOfMonth, monthOffset, hour, minute, 0, 0);
    }

    /**
     * 获取本年第一天
     *
     * @return
     */
    public static Date getDayBeginOfYear() {
        return getDayBeginOfYear(0);
    }

    /**
     * 获取本年最后一天
     *
     * @return
     */
    public static Date getDayEndOfYear() {
        return getDayEndOfYear(0);
    }

    /**
     * 获取某年第一天
     *
     * @param yearOffset 年偏移量
     * @return
     */
    public static Date getDayBeginOfYear(int yearOffset) {
        return getDayBeginOfYear(yearOffset, 0, 0, 0, 0);
    }

    /**
     * 获取某年最后一天
     *
     * @param yearOffset 年偏移量
     * @return
     */
    public static Date getDayEndOfYear(int yearOffset) {
        return getDayEndOfYear(yearOffset, 23, 59, 59, 999);
    }

    /**
     * 获取某小时整点
     *
     * @param hourOffset 小时偏移量
     * @return
     */
    public static Date getHourWholePoint(int hourOffset) {
        return getHour(hourOffset, 0, 0, 0);
    }

    /**
     * 获取某天当前整点
     *
     * @param dayOffset 天偏移量
     * @return
     */
    public static Date getDayHourWholePoint(int dayOffset) {
        return getDayHour(dayOffset, 0, 0, 0);
    }

    /**
     * 获取某天某小时整点
     *
     * @param dayOffset 天偏移量
     * @param hour      小时
     * @return
     */
    public static Date getDayHourWholePoint(int dayOffset, int hour) {
        return getDay(dayOffset, hour, 0, 0, 0);
    }

    /**
     * 获取当天某时某分
     *
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getHour(int hour, int minute) {
        return getDay(0, hour, minute, 0, 0);
    }

    /**
     * 获取某天某时某分
     *
     * @param dayOffset 天偏移量
     * @param hour      小时
     * @param minute    分钟
     * @return
     */
    public static Date getHour(int dayOffset, int hour, int minute) {
        return getDay(dayOffset, hour, minute, 0, 0);
    }

    /**
     * 获取某天某时某分某秒
     *
     * @param hourOffset    小时偏移，上一小时为-1，本小时为0，下一小时为1，以此类推
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getHour(int hourOffset, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //小时减一，即上一小时
        cal.add(Calendar.HOUR_OF_DAY, hourOffset);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某天当时某分某秒
     *
     * @param dayOffset     天偏移量
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayHour(int dayOffset, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //号数减一，即上一天
        cal.add(Calendar.DATE, dayOffset);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某天某时某分某秒
     *
     * @param dayOffset     天偏移，上一天为-1，本月为0，下一天为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDay(int dayOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //号数减一，即上一天
        cal.add(Calendar.DATE, dayOffset);
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某周某天某时某分某秒
     *
     * @param dayOfWeek  周几
     * @param weekOffset 周偏移，上周为-1，本周为0，下周为1，以此类推
     * @return
     */
    public static Date getDayOfWeek(int dayOfWeek, int weekOffset, int hour, int minute, int second, int millisecond) {
        if (dayOfWeek > Calendar.SATURDAY || dayOfWeek < Calendar.SUNDAY) {
            return null;
        }
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        //周数减一，即上周
        cal.add(Calendar.WEEK_OF_MONTH, weekOffset);
        //日子设为周几
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某月某日某时某分某秒
     *
     * @param dayOfMonth    几号
     * @param monthOffset   月偏移，上一月为-1，本月为0，下一月为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayOfMonth(int dayOfMonth, int monthOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //月份减一，即上一月
        cal.add(Calendar.MONTH, monthOffset);
        //日子设为几号
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取本月第一天某时某分某秒
     *
     * @param monthOffset   月偏移，上一月为-1，本月为0，下一月为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayBeginOfCurMonth(int monthOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //月份减一，即上一月
        cal.add(Calendar.MONTH, monthOffset);
        //第一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取本月最后一天某时某分某秒
     *
     * @param monthOffset   月偏移，上一月为-1，本月为0，下一月为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayEndOfCurMonth(int monthOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //月份减一，即上一月
        cal.add(Calendar.MONTH, monthOffset);
        //最后一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取本年某月第一天某时某分某秒
     *
     * @param monthOfYear   几月
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayBeginOfMonth(int monthOfYear, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //日子设为几月
        cal.set(Calendar.MONTH, monthOfYear - 1);
        //第一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取本年某月最后一天某时某分某秒
     *
     * @param monthOfYear   几月
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayEndOfMonth(int monthOfYear, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        //日子设为几月
        cal.set(Calendar.MONTH, monthOfYear - 1);
        //第一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某年某月某日某时某分某秒
     *
     * @param monthOfYear   几月
     * @param dayOfYear     几号
     * @param yearOffset    年偏移，上一年为-1，本年为0，下一年为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayOfYear(int monthOfYear, int dayOfYear, int yearOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        // 年份减一，即上一年
        cal.add(Calendar.YEAR, yearOffset);
        //日子设为几月
        cal.set(Calendar.MONTH, monthOfYear - 1);
        //日子设为几号
        cal.set(Calendar.DAY_OF_MONTH, dayOfYear);
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某年某月某日某时某分某秒
     *
     * @param yearOffset    年偏移，上一年为-1，本年为0，下一年为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayBeginOfYear(int yearOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        // 年份减一，即上一年
        cal.add(Calendar.YEAR, yearOffset);
        //一月
        cal.set(Calendar.MONTH, 0);
        //第一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某年某月某日某时某分某秒
     *
     * @param yearOffset    年偏移，上一年为-1，本年为0，下一年为1，以此类推
     * @param hour          小时
     * @param minute        分钟
     * @param second        秒
     * @param millisecond   毫秒
     * @return
     */
    public static Date getDayEndOfYear(int yearOffset, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        // 年份减一，即上一年
        cal.add(Calendar.YEAR, yearOffset);
        //12月
        cal.set(Calendar.MONTH, 11);
        //最后一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        //时分秒全部置0
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * 获取某个日期的开始时间
     *
     * @param d 时间
     * @return
     */
    public static long getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取某个日期的结束时间
     *
     * @param d 时间
     * @return
     */
    public static long getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取时间
     *
     * @param year 年
     * @return
     */
    public static Date getTime(int year) {
        return getTime(year, 0);
    }

    /**
     * 获取时间
     *
     * @param year  年
     * @param month 月
     * @return
     */
    public static Date getTime(int year, int month) {
        return getTime(year, month, 1);
    }

    /**
     * 获取时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static Date getTime(int year, int month, int day) {
        return getTime(year, month, day, 0);
    }

    /**
     * 获取时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @param hour  时
     * @return
     */
    public static Date getTime(int year, int month, int day, int hour) {
        return getTime(year, month, day, hour, 0);
    }

    /**
     * 获取时间
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @return
     */
    public static Date getTime(int year, int month, int day, int hour, int minute) {
        return getTime(year, month, day, hour, minute, 0);
    }

    /**
     * 获取时间
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @return
     */
    public static Date getTime(int year, int month, int day, int hour, int minute, int second) {
        return getTime(year, month, day, hour, minute, second, 0);
    }

    /**
     * 获取时间
     *
     * @param year        年
     * @param month       月
     * @param day         日
     * @param hour        时
     * @param minute      分
     * @param second      秒
     * @param millisecond 毫秒
     * @return
     */
    public static Date getTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }
}
