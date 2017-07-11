package com.example.android.doyouknowmath;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Robert on 7/10/2017.
 */

@IgnoreExtraProperties
public class Number {
    public int Number;

    public Number() {

    }

    @Override
    public String toString() {
        return "Number{" +
                "Number=" + Number +
                '}';
    }
}
