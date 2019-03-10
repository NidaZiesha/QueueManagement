package com.example.nida.queuemanagement;

import android.provider.BaseColumns;

/**
 * Created by Nida on 2/22/2019.
 */

public class Contact {

    private Contact() {
    }

    public static final class SMSEntry implements BaseColumns {

        public static final String TABLE_NAME = "smsList";
        public static final String COLUMN_NAME = "contactname";
        public static final String COLUMN_NUMBER = "contactnumber";
        public static final String COLUMN_DATE = "smsdate";
        public static final String COLUMN_TIME = "smstime";

    }
}