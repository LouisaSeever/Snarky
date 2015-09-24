package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateCommentsTask extends AsyncTask<String, Void, Void> {
    private static String TAG = "UpdateCommentsTask";
    private String mSnarkyName = "snarky";

    private ArrayList<String> mComments;
    private FileOutputStream mSnarkyStream;
    private Context mContext;

    public UpdateCommentsTask(Context activityContext){
        mContext = activityContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, "doInBackground");

        //open the snarky comments file
        try{
            //try to open existing file
            mSnarkyStream = mContext.openFileOutput(mSnarkyName, Context.MODE_PRIVATE);
        }
        catch(Exception openException){
            Log.e(TAG,"Error opening snarky file");
            return null;
        }

        //write out the comment at the specified position of the snarky file
        String commentIndex = params[0];
        int index = Integer.valueOf(commentIndex);
        String inputString = params[1];
        String comment = inputString.concat("||");

        try{
            mSnarkyStream.write(comment.getBytes(), index, comment.length());

            // and all following comments - update index
            for (int i=index+1; i<mComments.size(); i++){
                //?????????????????
            }
            //close file
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
