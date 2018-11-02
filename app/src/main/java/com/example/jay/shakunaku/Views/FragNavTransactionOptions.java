package com.example.jay.shakunaku.Views;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FragNavTransactionOptions {

    List<Pair<View, String>> sharedElements;

    //FragNavController.Transit
    int transition = FragmentTransaction.TRANSIT_NONE;

    int enterAnimation = 0;

    int exitAnimation = 0;

    int popEnterAnimation = 0;

    int popExitAnimation = 0;

    int transitionStyle = 0;

    public String breadCrumbTitle;
    public String breadCrumbShortTitle;

    private FragNavTransactionOptions(Builder builder){

        sharedElements = builder.sharedElements;
        transition = builder.transition;
        enterAnimation = builder.enterAnimation;
        exitAnimation = builder.exitAnimation;
        popEnterAnimation = builder.popEnterAnimation;
        popExitAnimation = builder.popExitAnimation;
        transitionStyle = builder.transitionStyle;
        breadCrumbTitle = builder.breadCrumbTitle;
        breadCrumbShortTitle = builder.breadCrumbShortTitle;
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder{
        private List<Pair<View, String>> sharedElements;
        private int transition;
        private int enterAnimation;
        private int exitAnimation;
        private int transitionStyle;
        private int popEnterAnimation;
        private int popExitAnimation;
        private String breadCrumbTitle;
        private String breadCrumbShortTitle;

        private Builder(){

        }

        public Builder addSharedElements(Pair<View, String> val){
            if(sharedElements == null){
                sharedElements = new ArrayList<>(3);
            }

            sharedElements.add(val);
            return this;
        }

        public Builder sharedElements(List<Pair<View, String>> val){
            sharedElements = val;
            return this;
        }

        public Builder transition(int val){
            transition = val;
            return this;
        }

        public Builder customAnimations(int enterAnimation, int exitAnimation){
            this.enterAnimation = enterAnimation;
            this.exitAnimation = exitAnimation;
            return this;
        }

        public Builder customAnimations(int enterAnimation, int exitAnimation, int popEnterAnimation, int popExitAnimation){
            this.popEnterAnimation = popEnterAnimation;
            this.popExitAnimation = popExitAnimation;
            return customAnimations(enterAnimation, exitAnimation);
        }

        public Builder transitionStyle(int val){
            transitionStyle = val;
            return this;
        }

        public Builder breadCrumbTitle(String val){
            breadCrumbTitle = val;
            return this;
        }

        public Builder breadCrumbShortTitle(String val){
            breadCrumbShortTitle = val;
            return this;
        }

        public FragNavTransactionOptions build(){
            return new FragNavTransactionOptions(this);
        }

    }
}
