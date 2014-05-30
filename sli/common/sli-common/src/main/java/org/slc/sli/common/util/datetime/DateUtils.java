package org.slc.sli.common.util.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtils class, which is abstracted to avoid blending with the questionable DateHelper class which should be merged into this class (and fixed).
 *
 * Created by tfritz on 5/29/14.
 */
public class DateUtils {
    private DateUtils() {}

    public enum DateUtilFormat {
        DATE_YYYY_MM_dd("yyyy-MM-dd"),
        DATE_YYYY_MM_ddTHH_mm_ss_SSSXXX("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");   //2001-07-04T12:08:56.235-07:00

        final String mask;

        private DateUtilFormat(final String mask) {
            this.mask = mask;
        }

        public String getMask() {
            return mask;
        }
    }

    public static String getFormattedDate(final DateUtilFormat dateUtilFormat, final Date date) {
        DateFormat dateFormat = new SimpleDateFormat(dateUtilFormat.getMask());
        String formattedDt = dateFormat.format(date);
        return formattedDt;
    }

}

