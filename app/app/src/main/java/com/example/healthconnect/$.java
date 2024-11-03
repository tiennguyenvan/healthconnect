package com.example.healthconnect;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class $ {
    private Activity activity;
    private View view;

    // Private constructor that takes the current Activity
    public $(Activity activity) {
        this.activity = activity;
    }

    // Static factory method to instantiate the class with an Activity
    public static $ in(Activity activity) {
        return new $(activity);
    }

    // Sets an OnClickListener for the specified view and returns the instance for chaining
    public $ onClick(int viewId) {
        view = activity.findViewById(viewId);
        return this;
    }

    // Switches to the specified screen when the view is clicked
    public $ goToScreen(Class<?> targetActivity) {
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(activity, targetActivity);
                activity.startActivity(intent);
            });
        }
        return this; // Return the instance for further chaining if needed
    }
}
