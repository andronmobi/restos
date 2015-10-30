package com.andronblog.restos.contract;

import android.net.Uri;
import android.provider.BaseColumns;

public class RestosContract {

    /** The authority for the restos provider */
    public static final String AUTHORITY = "com.andronblog.restos";

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CitiesColumns {
        String CITY_NAME = "city_name";
        String POPULATION = "population";
    }

    /**
     * Constants for the cities table.
     */
    public static class Cities implements BaseColumns, CitiesColumns {

        /**
         * This utility class cannot be instantiated
         */
        private Cities() {}

        /**
         * The content:// style URI for this table
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "cities");

        /**
         * The MIME type of {link #CONTENT_URI} providing a directory of
         * city.
         */
        public static String CONTENT_TYPE = "vnd.android.cursor.dir/city";

        /**
         * The MIME type of {link #CONTENT_URI} subdirectory of a single
         * city.
         */
        public static String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/city";
    }
}
