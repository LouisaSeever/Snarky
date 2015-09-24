package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import java.io.FileOutputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SaveCommentsTask extends AsyncTask<String, Void, Void> {
    private static String TAG = "SaveCommentsTask";
    private String mSnarkyName = "snarky";
    FileOutputStream mSnarkyStream;
    private Context mContext;

    public SaveCommentsTask(Context activityContext){
        mContext = activityContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        //open the snarky comments file
        try{
            //try to open existing file
            mSnarkyStream = mContext.openFileOutput(mSnarkyName, Context.MODE_APPEND);
        }
        catch(Exception openException){
            Log.e(TAG,"Error opening snarky file");

            //try to create it
            try{
                mSnarkyStream = mContext.openFileOutput(mSnarkyName,Context.MODE_PRIVATE);
            }
            catch(Exception creatException){
                Log.e(TAG,"Error creating snarky file");
                return null;
            }
        }

        //write out the comment at the end of the snarky file
        String inputString = params[0];
        String comment = inputString.concat("||");

        try{
            mSnarkyStream.write(comment.getBytes());
            mSnarkyStream.flush();
            mSnarkyStream.close();
            mSnarkyStream = null;
        }
        catch(Exception writeException){
            Log.e(TAG, "Error writing snarky comment:  ".concat(writeException.getMessage()));
        }

        return null;
    }

}
