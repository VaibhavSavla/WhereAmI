package com.example.vaibhav.whereami.mycity;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MyLocationActivityTest {

    @Rule
    public ActivityTestRule<MyLocationActivity> mActivityTestRule = new ActivityTestRule<>(MyLocationActivity.class);
}
