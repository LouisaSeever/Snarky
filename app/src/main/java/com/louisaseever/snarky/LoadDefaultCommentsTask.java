package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class LoadDefaultCommentsTask  {
    private static String TAG = "LoadDefaultCommentsTask";
    private String mSnarkyName = "snarky";
    FileOutputStream mSnarkyStream;
    private Context mContext;
    Boolean mComplete = false;

    public LoadDefaultCommentsTask(Context activityContext){
        mContext = activityContext;
    }

    public  void loadDefaultComments(){
        //create snarky file if does not yet exist
        String[] existingFiles = mContext.fileList();
        if (existingFiles.length == 0){
            //create new snarky file for the app
            try{
                mSnarkyStream = mContext.openFileOutput(mSnarkyName,Context.MODE_PRIVATE);
            }
            catch(Exception creatException){
                Log.e(TAG,"Error creating snarky file");
                return;
            }

            //read in the default file as raw resource and populate snarky with default comments
            Resources appResources = mContext.getResources();
            InputStream defaultCommentStream = appResources.openRawResource(R.raw.snarky);
            CharBuffer buffer = CharBuffer.allocate(8000);// 8k buffer
            InputStreamReader reader = new InputStreamReader(defaultCommentStream);
            StringBuilder nextComment = new StringBuilder();
            try{
                int sizeRead = reader.read(buffer);
                Log.d(TAG,"loadDefaultComments sizeRead=".concat(String.valueOf(sizeRead)));
                for (int next = 0; next < sizeRead; next++ ){
                    char nextChar = buffer.get(next);
                    nextComment.append(nextChar);
                    Log.d(TAG,"LoadComments nextChar=".concat(String.valueOf(nextChar)));
                }

                //append the comment to the snarky file
                mSnarkyStream.write(nextComment.toString().getBytes());
                nextComment = new StringBuilder();
            }
            catch(Exception writeException){
                Log.e(TAG, "Error loading default snarky file");
            }
            finally{
                try {
                    defaultCommentStream.close();
                    mSnarkyStream.flush();
                    mSnarkyStream.close();
                    mSnarkyStream = null;
                    mComplete = true;
                    Log.d(TAG,"LoadComments Completed");
                }
                catch (IOException e) {
                    Log.e(TAG, "Unable to close default comment file");
                }
            }
        }
        else{
            //file already loaded = nothing to do
            mComplete = true;
        }
    }



}
