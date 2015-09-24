package com.louisaseever.snarky;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;


public class AddNewCommentActivity extends AppCompatActivity {
    private static String TAG = "ManageCommentsActivity";
    private SnarkyFileManager mSnarkyFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_comment);

        //initialize the file manager
        mSnarkyFileManager = new SnarkyFileManager(this.getBaseContext());
        mSnarkyFileManager.refresh();

        //add listener on editCommentText to check for end/save
        addListenerForCommentChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onNewComment(View view){
        Log.d(TAG, "onNewComment");
        clearText();
    }

    public void onSave(View view){
        Log.d(TAG, "onSave");

        saveComment();

    }

    private void saveComment() {
        //add to comment file
        EditText commentText = (EditText) findViewById(R.id.editCommentText);
        String newComment = commentText.getText().toString().trim();
        if (!newComment.isEmpty()){
            mSnarkyFileManager.saveComment(newComment);
        }

        Intent data = new Intent();
        data.putExtra("comment", newComment);

        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }


    private void clearText() {
        EditText commentText = (EditText) findViewById(R.id.editCommentText);
        char[] emptyString = {};
        commentText.setText(emptyString,0,0);
    }

    private void addListenerForCommentChange(){
        EditText commentText = (EditText) findViewById(R.id.editCommentText);
        commentText.setOnKeyListener(new OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG,"OnKeyListener");
                if (event.getAction() == KeyEvent.ACTION_UP){
                    if (keyCode == KeyEvent.KEYCODE_ENTER){
                        saveComment();
                    }
                }
                return false;
            }
        });
    }


}
