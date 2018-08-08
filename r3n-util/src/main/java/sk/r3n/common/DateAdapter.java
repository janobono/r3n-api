/*
 * Copyright 2016 janobono. All rights reserved.
 * Use of this source code is governed by a Apache 2.0
 * license that can be found in the LICENSE file.
 */
package sk.r3n.common;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date adapter.
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        DateAdapter.DATE_FORMAT.setTimeZone(DateAdapter.TIME_ZONE);
    }

    @Override
    public Date unmarshal(String string) throws Exception {
        if (string != null) {
            return DATE_FORMAT.parse(string);
        } else {
            return null;
        }
    }

    @Override
    public String marshal(Date date) throws Exception {
        if (date != null) {
            return DATE_FORMAT.format(date);
        } else {
            return null;
        }
    }
}
