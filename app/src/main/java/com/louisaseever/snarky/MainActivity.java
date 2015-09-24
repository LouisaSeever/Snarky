package com.louisaseever.snarky;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static String TAG = "MainActiity";
    private SnarkyFileManager mSnarkyFileManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerationSensor;
    private long mPrevious = 0;
    private long mStartShakeTimeSpan = 0;
    private float mPreviousX = 0;
    private float mPreviousY = 0;
    private float mPreviousZ = 0;
    private int mMoveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the file manager
        mSnarkyFileManager = new SnarkyFileManager(this.getBaseContext());

        //start the acceleration sensor listener
        mSensorManager = (SensorManager)getSystemService(Activity.SENSOR_SERVICE);
        mAccelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //show the next button for debugging
        showNextButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void onStart(){
        Log.d(TAG, "onStart");
        //reload the comments
        super.onStart();
        mSnarkyFileManager.refresh();
    }

    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        mSensorManager.registerListener(this, mAccelerationSensor, SensorManager.SENSOR_DELAY_UI);

    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onAccuracyChanged(Sensor arg0, int event) {
        Log.d(TAG, "onAccuracyChanged");

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged");
        long eventTime= event.timestamp;

        long now = System.currentTimeMillis();

        //check for change every 50 milliseconds
        if (now-mPrevious > 50)
            mPrevious = now;

        float x = Math.abs(event.values[0]);
        float y = Math.abs(event.values[1]);
        float z = Math.abs(event.values[2]);

        //Log.d(TAG, "x=".concat(String.valueOf(x)));
        //Log.d(TAG, "y=".concat(String.valueOf(y)));
        //Log.d(TAG, "z=".concat(String.valueOf(z)));

        float diffX = Math.abs(x-mPreviousX);
        float diffY = Math.abs(y-mPreviousY);
        float diffZ = Math.abs(z-mPreviousZ);

        //Log.d(TAG, "diffx=".concat(String.valueOf(diffX)));
        //Log.d(TAG, "deffy=".concat(String.valueOf(diffY)));
        //Log.d(TAG, "diffz=".concat(String.valueOf(diffZ)));

        mPreviousX = x;
        mPreviousY = y;
        mPreviousZ = z;

        if (diffX>1 || diffY>1 || diffZ>1){
            //count this movement as part of shake
            mMoveCount++;
            //Log.d(TAG, "move count=".concat(String.valueOf(mMoveCount)));
        }

        //considered as "shake" if more than 3 moves in 1/2 second
        if (now - mStartShakeTimeSpan > 500)
        {
            mStartShakeTimeSpan = now;
            // Log.d(TAG, "check move count for shake");
            if (mMoveCount > 3){
                showNext();
                mMoveCount = 0;
            }

        }
    }


    /**
     * Show the next snarky comment
     * @param view
     */
    public void onNextClick(View view){
        Log.d(TAG, "onNextClick");
        showNext();
    }

    /**
     * open the manageSnarky activity
     * @param view
     */
    public void onManageSnarkyComments(View view){
        Log.d(TAG, "manageSnarkyComments");
        Intent intent = new Intent(this, ManageCommentsActivity.class);
        startActivity(intent);
    }


    private void showNext(){
        TextView commentView = (TextView) findViewById(R.id.snarkyComment);

        //set background to random color
        int nextBackgroundColor = ChooseColor();
        commentView.setBackgroundColor(nextBackgroundColor);

        //set text color
        int nextTextColor = ChooseColor();
        while(nextTextColor == nextBackgroundColor){
            nextTextColor = ChooseColor();
        }
        commentView.setTextColor(nextTextColor);

        //set the text
        String comment = ChooseComment();
        commentView.setText(comment.toCharArray(),0,comment.length());

    }


    /**
     * Choose a random comment from the Snarkyfilemanager
     * @return
     */
    private String ChooseComment(){
        int count = mSnarkyFileManager.getSize();
        int random = (int)(Math.random()*count);
        String chosenComment = mSnarkyFileManager.getComment(random);
        return chosenComment;
    }


    /**
     * Choose a random color
     * @return
     */
    private int ChooseColor(){
        int random = (int)(Math.random()*10);
        Log.d(TAG,"ChooseColor() random=".concat(String.valueOf(random)));
        int color = android.graphics.Color.WHITE;
        switch (random){
            case 0:
                color = android.graphics.Color.WHITE;
                break;
            case 1:
                color = android.graphics.Color.BLACK;
                break;
            case 2:
                color = android.graphics.Color.BLUE;
                break;
            case 3:
                color = android.graphics.Color.CYAN;
                break;
            case 4:
                color = android.graphics.Color.DKGRAY;
                break;
            case 5:
                color = android.graphics.Color.GRAY;
                break;
            case 6:
                color = android.graphics.Color.GREEN;
                break;
            case 7:
                color = android.graphics.Color.LTGRAY;
                break;
            case 8:
                color = android.graphics.Color.YELLOW;
                break;
            case 9:
                color = android.graphics.Color.MAGENTA;
                break;
            default:
                color = android.graphics.Color.WHITE;
                break;
        }
        return color;
    }


    private void showNextButton() {
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setClickable(true);
        nextButton.setEnabled(true);
    }

}
