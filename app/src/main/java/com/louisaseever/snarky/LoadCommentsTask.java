package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LoadCommentsTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private static String TAG = "LoadCommentsTask";
    FileOutputStream mSnarkyStream;
    private Context mContext;
    Boolean mComplete = false;
    private	ArrayList<String> mSnarkyComments;
    private ArrayList<Integer> mCommentIndex;

    public LoadCommentsTask(Context activityContext){
        mContext = activityContext;
    }

    public Boolean loadIsComplete(){
        return mComplete;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        loadComments();
        return mSnarkyComments;
    }


    protected Boolean onPostExecute(Void... params){
        Log.d(TAG, "onPostExecute called");
        return mComplete;
    }

    public ArrayList<Integer> getCommentIndex(){
        return mCommentIndex;
    }

    private  void loadComments(){
        Log.d(TAG, "loadComments");
        //create snarky file if does not yet exist
        String[] existingFiles = mContext.fileList();
        if (existingFiles.length == 0){
            //create new snarky file for the app and populate with default comments
            LoadDefaultCommentsTask defaultLoader = new LoadDefaultCommentsTask(mContext);
            defaultLoader.loadDefaultComments();
        }

        existingFiles = mContext.fileList();
        Log.d(TAG, "existing file list size=".concat(String.valueOf(existingFiles.length)));


        //open the snarky comments file
        String snarkyFileName = "snarky";
        FileInputStream snarkyStream = null;
        try{
            snarkyStream = mContext.openFileInput(snarkyFileName);
        }
        catch(Exception openException){
            Log.e(TAG,"Error opening/creating snarky file");
            return;
        }

        //read the stream into the snarky string array
        mSnarkyComments = new ArrayList<String>();
        mCommentIndex = new ArrayList<Integer>();
        CharBuffer buffer = CharBuffer.allocate(8000);// 8k buffer
        InputStreamReader reader = new InputStreamReader(snarkyStream);
        StringBuilder nextComment = new StringBuilder();
        try{
            int filePos = 0;
            int sizeRead = reader.read(buffer);
            Log.d(TAG,"LoadComments sizeRead=".concat(String.valueOf(sizeRead)));
            while (sizeRead > 0){
                //examine each char in buffer and build up comments terminated by "||"
                for (int next = 0; next < sizeRead; next++ ){
                    filePos++;
                    char nextChar = buffer.get(next);
                    Log.d(TAG,"LoadComments nextChar=".concat(String.valueOf(nextChar)));
                    //end of comment -> add it to snarky array
                    if ((nextChar == '|') ||(nextChar == '|')) {
                        String comment = nextComment.toString();
                        int nextCommentIndex = filePos - comment.length();
                        comment = comment.trim();
                        if (!comment.isEmpty()){
                            mSnarkyComments.add(nextComment.toString());
                            mCommentIndex.add(nextCommentIndex);
                            nextComment = new StringBuilder();
                        }
                    }
                    else{
                        //add char to end of nextComment string
                        nextComment.append(String.valueOf(nextChar));
                    }
                }

                //read in the next buffer
                sizeRead = reader.read(buffer);
            }
        }
        catch(Exception writeException){
            Log.e(TAG, "Error reading snarky comment");
        }
        finally{
            try {
                snarkyStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Unable to close snarkyStream");
            }
        }

    }

}
