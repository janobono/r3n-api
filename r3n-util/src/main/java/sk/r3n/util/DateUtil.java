/*
 * Copyright 2014 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.util;

import java.util.Calendar;
import java.util.Date;


/**
 * {@link Date} utility methods.
 *
 * @author janobono
 * @Deprecated Use {@link java.time.LocalDate}, {@link java.time.LocalTime}, {@link java.time.LocalDateTime} rather than {@link Date}
 * @since 18 August 2014
 */
@Deprecated
public class DateUtil {

    /**
     * Time part of {@link Date} set to 0.
     *
     * @param date source date
     * @return modified date
     */
    public static Date getDateOnly(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Date part of {@link Date} set to 1.1.1970.
     *
     * @param date source date
     * @return modified date
     */
    public static Date getTimeOnly(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, 1970);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * Day of {@link Date} set to first day of month.
     *
     * @param date source date
     * @return modified date
     */
    public static Date getDateOnlyFirstDayOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        date = DateUtil.getDateOnly(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        return date;
    }
}
