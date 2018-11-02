package com.example.jay.shakunaku.Utils;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Field;
import java.nio.file.NoSuchFileException;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setUpBottomNavigationView(BottomNavigationViewEx bottomNavigationView){
        Log.d(TAG, "setUpBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.setTextVisibility(false);
    }

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view){

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);

        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for(int i=0; i<menuView.getChildCount(); i++){
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.clearAnimation();
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
