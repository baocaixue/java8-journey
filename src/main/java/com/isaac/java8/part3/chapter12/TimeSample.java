package com.isaac.java8.part3.chapter12;

import org.junit.Test;

import java.time.*;
import java.time.chrono.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import static org.junit.Assert.*;
import static java.time.temporal.TemporalAdjusters.*;

public class TimeSample {
    /**
     * LocalDate LocalTime
     * 这里的日期时间对象都是不可修改的
     * 当然，新的日期和时间API也提供了一些便利的方法来创建这些对象的可变版本。比如，你可能希望在已有的LocalDate实例上增加3天 @See testOperateTime
     */
    @Test public void testLocalDateLocalTime(){
        LocalDate date = LocalDate.of(2019, 6, 9);
        int year = date.getYear();
        assertEquals(2019, year);
        Month month = date.getMonth();
        assertEquals("JUNE", month.name());
        assertEquals(6, month.getValue());
        int day = date.getDayOfMonth();
        assertEquals(9, day);
        DayOfWeek dow = date.getDayOfWeek();
        assertEquals("SUNDAY", dow.name());
        assertEquals(7, dow.getValue());
        int len = date.lengthOfMonth();
        assertEquals(30, len);
        //系统时钟
        LocalDate today = LocalDate.now();
        assertNotNull(today);
        //也可以通过传递TemporalField参数给get方法拿到同样的信息
        int year1 = date.get(ChronoField.YEAR);
        assertEquals(year, year1);

        /*一天中的时间，可以用LocalTime表示*/
        LocalTime time = LocalTime.of(11, 21, 45);
        int hour = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        assertEquals(11, hour);
        assertEquals(21, minute);
        assertEquals(45, second);

        /*LocalDate LocalTime都可以通过解析代表它们的字符串创建*/
        LocalDate date1 = LocalDate.parse("2019/06/09", DateTimeFormatter.ofPattern("yyyy/MM/dd"));//默认格式：2019-06-09
        LocalTime time1 = LocalTime.parse("11:31:25");
        assertNotNull(date1);
        assertNotNull(time1);

        /*合并日期和时间——LocalDateTime*/
        LocalDateTime dt1 = LocalDateTime.of(2019, 6, 9, 11, 32, 45);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);
        LocalDate toDate = dt1.toLocalDate();
        LocalTime toTime = dt1.toLocalTime();

        /*
        * 机器的日期和时间格式——对机器来说，建模时间最自然的格式是表示一个持续时间段上某个点的单一大整型数，也就是java.time.Instant类对时间建模的方式，基本上它是以Unix元年时间
        *（传统的设定为UTC时区1970年1月1日午夜时分）开始所经历的秒数进行计算
        * */
        Instant.ofEpochSecond(3);
        Instant.ofEpochSecond(3, 0);
        //2秒之后加上100万纳秒（1秒）
        Instant.ofEpochSecond(2, 1_000_000_000);
        //4秒之前的100万秒
        Instant.ofEpochSecond(4, 1_000_000_000);
        try {
            //Instant是为了便于机器使用，包含由秒及纳秒所构成的数字。所以它无法理解人所容易理解的时间单位
            Instant.now().get(ChronoField.DAY_OF_MONTH);
            assertTrue(false);
        } catch (UnsupportedTemporalTypeException e) {
            assertTrue(true);
        }

        /*目前为止，所看到的类都实现类Temporal接口，Temporal接口定义了如何读取和操作为时间建模的对象的值*/
        /*定义Duration或Period*/
        //两个Temporal对象之间的duration
        Duration d1 = Duration.between(time, time1);
        //LocalDateTime和Instant不能混用，在这两个类的对象之间创建duration会触发DateTimeException。
        //Duration主要用于以秒和纳秒衡量时间的长短，不能向between方法传递LocalDate对象参数
        assertEquals(580, d1.get(ChronoUnit.SECONDS));
        //若需要以年、月、日方式对多个时间单位建模，可以使用Period
        Period p1 = Period.between(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 6, 9));
        assertEquals(5, p1.get(ChronoUnit.MONTHS));
        //创建Duration和Period
        Duration threeMinutes = Duration.ofMinutes(3);
        threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);
    }

    /**
     * 操纵、解析、格式化日期
     */
    @Test public void testOperateTime() {
        //LocalDate对象修改版用withAttribute方法——会创建对象的一个副本
        LocalDate date1 = LocalDate.of(2019, 1, 1);
        LocalDate date2 = date1.withYear(2018);
        assertEquals(2018, date2.getYear());
        LocalDate date3 = date2.withDayOfMonth(10);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 5);
        assertEquals("20180510", date4.format(DateTimeFormatter.BASIC_ISO_DATE));
        LocalDate date5 = date4.plusWeeks(1);
        assertEquals("20180517", date5.format(DateTimeFormatter.BASIC_ISO_DATE));
        LocalDate date6 = date5.minusYears(2);
        assertEquals("20160517", date6.format(DateTimeFormatter.BASIC_ISO_DATE));
        LocalDate date7 = date6.plus(10, ChronoUnit.MONTHS);
        assertEquals("20170317", date7.format(DateTimeFormatter.BASIC_ISO_DATE));

        /*使用TemporalAdjuster*/
        //更细化的时间操作——下周日、下个工作日、本月最后一天等。。。
        LocalDate date8 = date1.with(nextOrSame(DayOfWeek.SUNDAY));
        assertEquals("20190106", date8.format(DateTimeFormatter.BASIC_ISO_DATE));
        LocalDate date9 = date1.with(lastDayOfMonth());
        assertEquals("20190131", date9.format(DateTimeFormatter.BASIC_ISO_DATE));
        //自定义TemporalAdjuster
        LocalDate date = LocalDate.of(2019, 6, 9);
        CustomizedTemporalAdjuster custom = new CustomizedTemporalAdjuster();
        LocalDate customizedResult = date.with(custom);
        assertEquals("20190610", customizedResult.format(DateTimeFormatter.BASIC_ISO_DATE));
        LocalDate customResult1 = date.with(previousOrSame(DayOfWeek.FRIDAY)).with(custom);
        assertEquals("20190610", customResult1.format(DateTimeFormatter.BASIC_ISO_DATE));

        /*打印输出及解析日期——时间对象 java.time.format DateTimeFormatter是线程安全的*/
        String ios_local_date = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertEquals("2019-06-09", ios_local_date);
        LocalDate d1 = LocalDate.parse(ios_local_date, DateTimeFormatter.ISO_LOCAL_DATE);
        assertNotNull(d1);
        System.out.println(date.format(customizedDateTimeFormatter()));

    }

    /**
     * 处理不同时区和历法 java.time.ZoneId
     */
    @Test public void testTimeZoneOperation() {
        //为时间点添加时区信息
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDate date = LocalDate.of(2019, 6, 9);
        ZonedDateTime zdt1 = date.atStartOfDay(zoneId);
        LocalDateTime dateTime = LocalDateTime.of(2019, 6, 9, 15, 47, 32);
        ZonedDateTime zdt2 = dateTime.atZone(zoneId);
        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(zoneId);
        Instant instantFromDateTime = dateTime.toInstant(ZoneOffset.MIN);
        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instantFromDateTime, zoneId);

        /*利用和UTC/格林尼治时间的固定偏差计算时区*/
        //纽约落后伦敦5个小时
        //这种方式定义ZoneOffset并未考虑任何日光时的影响，在大多数情况不推荐使用
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
        OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(dateTime, newYorkOffset);

        /*使用别的日历系统 ThaiBuddhistDate、MinguoDate 、JapaneseDate 以及HijrahDate*/
        //应用中使用LocalDate，包括存储、操作、业务规则的解读；不过如果你需要将程序的输入或者输出本地化，这时你应该使用ChronoLocalDate类。
        JapaneseDate japaneseDate = JapaneseDate.from(date);

        Chronology chinaChronology = Chronology.ofLocale(Locale.CHINA);
        ChronoLocalDate now = chinaChronology.dateNow();
        System.out.println(now);

        //伊斯兰教日历HijrahDate
        //了如何在ISO日历中计算当前伊斯兰年中斋月的起始和终止日期
        //斋月第一天
        HijrahDate ramadanDate = HijrahDate.now().with(ChronoField.DAY_OF_MONTH, 1).with(ChronoField.MONTH_OF_YEAR, 9);
        System.out.println("Ramadan starts on " + IsoChronology.INSTANCE.date(ramadanDate) + " and ends on " + IsoChronology.INSTANCE.date(ramadanDate.with(lastDayOfMonth())));
    }

    public static DateTimeFormatter customizedDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.CHINA);
    }
}
