package com.grocerylist.shruti.grocerylist.log;

/**
 * Created by shruti on 9/9/2017.
 */
import android.util.Log;

import com.grocerylist.shruti.grocerylist.Utils.Constants;


public class Logger {

    /**
     * Log a line
     */
    public static int logCreate(Object o) {

        if (Constants.TRACE_ALLOWED && Constants.TRACE_OBJECTS) {
            if (o instanceof String) {
                return Log.d((String) o, "Create ");
            } else {
                return Log.d(o.getClass().getName(),  "Create ");
            }
        }
        return 0;
    }

}
