# call_log

A simple demo Flutter app for Android that fetches paginated call information from device call log.
Only supported by Android because, yunno, iOS is all about 'privacy'.

## Call Info Details

- Caller Name (where available)
- Caller Phone Number
- Call Type (Outgoing, Incoming, Missed Rejected). [developer.android.com](https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE)
- Call Date
- Call Duration (in seconds)