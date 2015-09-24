package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import android.content.Context;
import android.util.Log;

public class SnarkyFileManager {
    private static String TAG = "SnarkyFileManager";
    private FileOutputStream mSnarkyStream;
    private Context mContext;
    private LoadCommentsTask mCommentLoader;

    private	List<String> mSnarkyComments;
    private ArrayList<Integer> mCommentFileIndex;


    public SnarkyFileManager(Context activityContext){
        mContext = activityContext;

    }

    public int getSize(){
        loadComments();
        return mSnarkyComments.size();
    }

    public String getComment(int i){
        loadComments();
        return mSnarkyComments.get(i);
    }

    public void saveComment(String comment)
    {
        //make sure comments are loaded
        loadComments();

        if (comment.length() > 0){
            //append the new comment to the file and to the list - don't wait
            SaveCommentsTask saveCommentsTask = new SaveCommentsTask(mContext);
            saveCommentsTask.execute(comment, null, null);

            //add the comment to the working list
            mSnarkyComments.add(comment);
        }
    }

    public void updateComment(int i, String comment)
    {
        //make sure comments are loaded
        loadComments();

        if (comment.length() > 0){
            //replace the comment in the working list
            mSnarkyComments.set(i,comment);

            //update the comment in the file - don't wait
            UpdateCommentsTask updateTask = new UpdateCommentsTask(mContext);
            int fileIndex = mCommentFileIndex.get(i);
            String pos = String.valueOf(fileIndex); //position in file

            updateTask.execute(pos, comment);
        }
    }


    public void refresh(){
        Log.d(TAG, "refresh");
        mSnarkyComments = null;
        mCommentLoader = new LoadCommentsTask(mContext);
        mCommentLoader.execute();
    }

    public void close(){
        if (mSnarkyStream != null){
            try{
                mSnarkyStream.close();
                mSnarkyStream = null;
            }
            catch(Exception closeException){
                Log.e(TAG, "Error closing snarky comments stream:  ".concat(closeException.getMessage()));
            }
        }
    }



    private void loadComments(){
        Log.d(TAG, "loadComments");
        if(mSnarkyComments != null){
            //comment array has been built
            return;
        }

        //make sure comment file has been loaded
        Log.d(TAG, "wait for loader");
        ArrayList<String> loadedComments = null;
        String errorMessage = "";
        try {
            loadedComments = mCommentLoader.get(11000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            errorMessage = "InterruptedException waiting for comments to load";
            Log.e(TAG,errorMessage);
        } catch (ExecutionException e1) {
            errorMessage = "ExecutionException waiting for comments to load";
            Log.e(TAG,errorMessage);
        } catch (TimeoutException e1) {
            errorMessage = "TimeoutException waiting for comments to load";
            Log.e(TAG,errorMessage);
        }

        //get comment offsets in file
        mCommentFileIndex = mCommentLoader.getCommentIndex();

        //put error message in array if file failed to load
        if (loadedComments == null) {
            loadedComments = new ArrayList<String>();
            if (!errorMessage.isEmpty()){
                loadedComments.add(errorMessage);
            }
        }

        //no comments entered -> request user to add
        if ((errorMessage.isEmpty()) &&(loadedComments.size() == 0)){
            loadedComments.add("You have no snarky comments.  Please add some.");
        }

        Log.d(TAG, "push loaded comments into new snarkyComments array");
        mSnarkyComments = new ArrayList<String>();
        mSnarkyComments.addAll(loadedComments);

    }
}

