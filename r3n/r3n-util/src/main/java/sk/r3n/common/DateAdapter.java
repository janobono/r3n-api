package sk.r3n.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author jan
 */
public class DateAdapter extends XmlAdapter<String, Date> {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");

    static {
        DateAdapter.DATE_FORMAT.setTimeZone(DateAdapter.TIME_ZONE);
    }

    @Override
    public Date unmarshal(String string) throws Exception {
        return DATE_FORMAT.parse(string);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return DATE_FORMAT.format(date);
    }

}
