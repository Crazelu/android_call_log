package com.example.call_log;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.HashMap;

public class CallLogManager {
    private Context context;
    private Cursor cursor;

    CallLogManager(Context context){
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<HashMap<String, Object>> getCallLog(int page, int limit) {
        ArrayList<HashMap<String, Object>> callLog = new ArrayList<>();

         cursor = cursor == null? context.getContentResolver().query(
                 CallLog.Calls.CONTENT_URI,
                null,null, null,
                 CallLog.Calls.DATE+" DESC")
                 : cursor;

        int number = cursor.getColumnIndex( CallLog.Calls.NUMBER );
        int type = cursor.getColumnIndex( CallLog.Calls.TYPE );
        int date = cursor.getColumnIndex( CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex( CallLog.Calls.DURATION);
        int name = cursor.getColumnIndex( CallLog.Calls.CACHED_NAME);

        int count = (page -1) * limit;

        while ( cursor.moveToNext() ) {
            if(count == limit * page) break;

            count++;

            String callerName = cursor.getString( name );
            String phone = cursor.getString( number );
            String callDate = cursor.getString( date );
            String callDuration = cursor.getString( duration );
            String callType;
            int callTypeCode = Integer.parseInt( cursor.getString( type ) );
            switch( callTypeCode ) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;

                case CallLog.Calls.REJECTED_TYPE:
                    callType = "REJECTED";
                    break;

                default:
                    callType = cursor.getString( type );
            }
            HashMap<String, Object> callInfo = new HashMap<String, Object>();

            callInfo.put("name", callerName);
            callInfo.put("phoneNumber", phone);
            callInfo.put("callType", callType);
            callInfo.put("callTime", callDate);
            callInfo.put("callDuration", callDuration);

            callLog.add(callInfo);
           }

        return callLog;
    }

    public void release(){
        cursor.close();
    }
}
