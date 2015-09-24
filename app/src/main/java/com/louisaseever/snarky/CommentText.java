package com.louisaseever.snarky;

/**
 * Created by LouisaSeever on 9/24/2015.
 */
import android.content.Context;
import android.widget.EditText;

public class CommentText extends EditText {

    private int _index = -1; // index in comments array
    private int _offset = -1; //offset in comments file

    public CommentText(Context context) {
        super(context);
    }

    //Index = position in the comment array
    public void setIndex(int i){
        _index = i;
    }

    public int getIndex(){
        return _index;
    }


}
