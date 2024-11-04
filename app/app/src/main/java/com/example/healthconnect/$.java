package com.example.healthconnect;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class $ {
    private final Activity activity;
    private int viewId = -1;  // Store viewId instead of the view itself
    private final String LogTag = "$theSuperHelper";

    public enum EventType {
        NONE,
        CLICK,
        FOCUS
    }

    private EventType eventType = EventType.NONE;

    // Private constructor that takes the current Activity
    private $(Activity activity) {
        this.activity = activity;
    }

    // Static factory method to instantiate the class with an Activity
    public static $ in(Activity activity) {
        return new $(activity);
    }

    // Event selector with viewId retrieval at runtime
    private void registerEvent(Runnable action) {
        Log.d(LogTag, "registerEvent");
        if (viewId == -1) {
            Log.d(LogTag, "View -1");
            if (action == null) {
                Log.d(LogTag, "View -1, action = null");
                return;
            }
            Log.d(LogTag, "Action should run");
            action.run();
            resetEvent();
            return;
        }

        View localView = activity.findViewById(viewId);  // Retrieve the view using viewId
        if (localView == null) {
            return;
        }

        switch (eventType) {
            case CLICK:
                localView.setOnClickListener(v -> {
                    action.run();
                });
                break;

            case FOCUS:
                localView.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) action.run();
                });
                break;
        }
        resetEvent();
    }

    private void resetEvent() {
        eventType = EventType.NONE;
        viewId = -1;
    }

    // Event Listener and chaining
    public $ onClick(int viewId) {
        this.viewId = viewId;
        eventType = EventType.CLICK;
        return this;
    }

    public $ onFocus(int viewId) {
        this.viewId = viewId;
        eventType = EventType.FOCUS;
        return this;
    }

    // Runnable methods
    public void doAction(Runnable customAction) {
        registerEvent(customAction);
    }

    public void goToScreen(Class<?> targetActivity) {
        Runnable action = () -> {
            try {
                Intent intent = new Intent(activity, targetActivity);
                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e("Navigation Error", "Failed to navigate to " + targetActivity.getSimpleName(), e);
            }
        };
        registerEvent(action);
    }

    public void pickDate() {
        View localView = activity.findViewById(viewId);
        Runnable action = () -> {
            if (!(localView instanceof EditText)) {
                Log.e("pickDateError", "pickDate() should be used with an EditText view only.");
                return;
            }

            Calendar calendar = Calendar.getInstance();
            String currentDateText = ((EditText) localView).getText().toString().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if (!currentDateText.isEmpty()) {
                try {
                    calendar.setTime(dateFormat.parse(currentDateText));
                } catch (ParseException e) {
                    // If parsing fails, the calendar remains set to the current date
                }
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, selectedYear, selectedMonth, selectedDay) -> {
                String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                ((EditText) localView).setText(date);
            }, year, month, day);

            datePickerDialog.show();
        };
        registerEvent(action);
    }

    public void showToast(String message) {
        Runnable action = () -> {
            Log.d(LogTag, "inside the toast action");
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        };
        registerEvent(action);
    }

    // Utilities
    public String getTextFrom(int viewId) {
        EditText et = activity.findViewById(viewId);
        return et.getText().toString().trim();
    }

    public double getDoubleFrom(int viewId) {
        try {
            return Double.parseDouble(this.getTextFrom(viewId));
        } catch (Exception e) {
            return 0;
        }
    }

    // Validation Helper
    private void setError(TextView errorView, String message) {
        if (errorView != null) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void clearError(TextView errorView) {
        if (errorView != null) errorView.setVisibility(View.GONE);
    }

    public boolean validateInput(int inputViewId, int errorViewId) {
        EditText inputView = activity.findViewById(inputViewId);
        TextView errorView = errorViewId != 0 ? activity.findViewById(errorViewId) : null;
        String inputText = inputView.getText().toString().trim();

        // Check if the input is empty
        if (inputText.isEmpty()) {
            setError(errorView, activity.getString(R.string.warning_field_required));
            return false;
        } else {
            clearError(errorView);
        }

        // Detect the input type based on inputType attribute
        int inputType = inputView.getInputType();
        switch (inputType) {
            case android.text.InputType.TYPE_CLASS_NUMBER:
            case android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL:
                try {
                    Double.parseDouble(inputText);
                    clearError(errorView);
                } catch (NumberFormatException e) {
                    setError(errorView, activity.getString(R.string.warning_invalid_number));
                    return false;
                }
                break;

            case android.text.InputType.TYPE_CLASS_PHONE:
                if (!inputText.matches("\\d{3}-?\\d{3}-?\\d{4}")) {
                    setError(errorView, activity.getString(R.string.warning_invalid_phone));
                    return false;
                } else {
                    clearError(errorView);
                }
                break;

            case android.text.InputType.TYPE_CLASS_DATETIME:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    dateFormat.parse(inputText);
                    clearError(errorView);
                } catch (ParseException e) {
                    setError(errorView, activity.getString(R.string.warning_invalid_date));
                    return false;
                }
                break;

            default:
                clearError(errorView);
                break;
        }

        return true;
    }
}
