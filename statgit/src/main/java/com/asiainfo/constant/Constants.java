package com.asiainfo.constant;

/**
 * Created by Jacky on 2015/7/13.
 */
public interface Constants {

    public static interface StaticParam {
        public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
        public static final long DAY_MILLIONSECOND = 24*60*60*1000;
    }

    public static interface GitConstants {
        public static final int STAT_METHOD_DAY = 1;
        public static final int STAT_METHOD_WEEK = 2;
        public static final int STAT_METHOD_MONTH = 3;
        public static final int STAT_METHOD_CUSTOM = 0;
    }
}
