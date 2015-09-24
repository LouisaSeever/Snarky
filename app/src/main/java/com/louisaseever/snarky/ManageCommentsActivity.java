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
import android.widget.TableLayout;
import android.widget.TableRow;


public class ManageCommentsActivity extends AppCompatActivity {
    private static String TAG = "ManageCommentsActivity";

    private static int ADD_COMMENT;

    private SnarkyFileManager mSnarkyFileManager;

    private TableLayout _commentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_comments);

        //initialize the file manager
        mSnarkyFileManager = new SnarkyFileManager(this.getBaseContext());
        mSnarkyFileManager.refresh();

        //populate the comment table
        _commentTable = (TableLayout) findViewById(R.id.manage_comment_table);
        populateCommentTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_comments, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle return from AddComment
        if (requestCode == ADD_COMMENT) {
            if (resultCode == RESULT_OK) {
                // get comment from returned intent
                String comment = data.getStringExtra("comment");

                // show message in table
                if (!comment.isEmpty()){
                    addCommentToTable(comment);
                }

            }
        }
    }

    public void onAddComment(View view){
        Log.d(TAG, "onAddComment");
        Intent intent = new Intent(this, AddNewCommentActivity.class);
        startActivityForResult(intent, ADD_COMMENT);
    }

/*	public void onSave(View view){
		Log.d(TAG, "onSave");

		saveComment();
	}
*/

    private void populateCommentTable(){
        int size = mSnarkyFileManager.getSize();
        for (int i=0; i<size; i++){
            String comment = mSnarkyFileManager.getComment(i);
            addCommentToTable(comment);
        }


    }

    private void addCommentToTable(String comment) {
        //add a row to the table with the comment as editText
        int index = _commentTable.getChildCount(); //position at end of table
        TableRow nextRow = new TableRow(this);
        TableRow.LayoutParams rowLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        nextRow.setLayoutParams(rowLayout);

        CommentText nextComment = new CommentText(this);
        nextComment.setIndex(index);
        nextComment.setText(comment);
        nextComment.setPadding(2, 0, 0, 0);

        //add the comment to the table
        nextRow.addView(nextComment);
        _commentTable.addView(nextRow);

        //add listener on editCommentText to check for end/save
        addListenerForCommentChange(nextComment);
    }

    private void addListenerForCommentChange(EditText commentText){
        commentText.setOnKeyListener(new OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG,"OnKeyListener");
                CommentText comment = (CommentText)v;
                if (event.getAction() == KeyEvent.ACTION_UP){
                    if (keyCode == KeyEvent.KEYCODE_ENTER){
                        saveComment(comment);
                        return true; //key has been handled here => cannot use "Enter" key in comment
                    }
                }
                return false; //key must be handled by standard EditText
            }
            private void saveComment(CommentText commentText) {
                //update comment in file
                String newComment = commentText.getText().toString();
                if (!newComment.isEmpty()){
                    int pos = commentText.getIndex();
                    mSnarkyFileManager.updateComment(pos, newComment);
                }
            }
        });
    }

}
