package com.example.healthconnect;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class $ {
    private final Activity activity;
    private int viewId = -1;  // Store viewId instead of the view itself
    private String LogTag;

    public enum EventType {
        NONE,
        CLICK,
        FOCUS
    }

    private EventType eventType = EventType.NONE;

    // Private constructor that takes the current Activity
    private $(Activity activity) {
        LogTag = activity.getClass().getSimpleName();
        this.activity = activity;
    }

    // Static factory method to instantiate the class with an Activity
    public static $ in(Activity activity) {
        return new $(activity);
    }

    // Event selector with viewId retrieval at runtime
    private void registerEvent(Runnable action) {
        if (viewId == -1) {
            if (action == null) {
                return;
            }
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
    // Set the view ID for chaining
    public $ on(int viewId) {
        this.viewId = viewId;
        eventType = EventType.NONE;
        return this;
    }
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
                //
            }
        };
        registerEvent(action);
    }

    public void passToScreen(Class<?> targetActivity, Object... extras) {
        Runnable action = () -> {
            try {
                Intent intent = new Intent(activity, targetActivity);

                for (int i = 0; i < extras.length - 1; i += 2) {
                    String key;
                    if (extras[i] instanceof Integer) {
                        key = activity.getString((Integer) extras[i]);
                    } else {
                        key = extras[i].toString();
                    }
                    Object value = extras[i + 1];

                    if (value instanceof Integer) {
                        intent.putExtra(key, (Integer) value);
                    } else if (value instanceof Long) {
                        intent.putExtra(key, (Long) value);
                    } else if (value instanceof String) {
                        intent.putExtra(key, (String) value);
                    } else if (value instanceof Boolean) {
                        intent.putExtra(key, (Boolean) value);
                    } else if (value instanceof Serializable) {
                        intent.putExtra(key, (Serializable) value);
                    } else {
                        Log.e(LogTag, "Unsupported extra type for key: " + ((String) key));
                    }
                }
                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e(LogTag, "Error starting activity with extras", e);
            }
        };
        registerEvent(action);
    }

    public void pickDate() {
        View localView = activity.findViewById(viewId);
        Runnable action = () -> {
            if (!(localView instanceof EditText)) {
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
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        };
        registerEvent(action);
    }

    // Utilities
    public String getTextFrom(int viewId) {
        EditText et = activity.findViewById(viewId);
        return et.getText().toString().trim();
    }
    public void setText(String text) {
        View view = activity.findViewById(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        resetEvent();
    }
    public void setText(int textId) {
        View view = activity.findViewById(viewId);
        String text = activity.getString(textId);
        if (view instanceof TextView && !text.isEmpty()) {
            ((TextView) view).setText(text);
        }
        resetEvent();
    }
    public void setTextTo(int viewId, String text) {
        View view = activity.findViewById(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        resetEvent();
    }

    public double getDoubleFrom(int viewId) {
        try {
            return Double.parseDouble(this.getTextFrom(viewId));
        } catch (Exception e) {
            return 0;
        }
    }
    public void log(String msg) {
        Log.d(LogTag, msg);
    }
    public String formatDateOfBirth(String dateOfBirth) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        try {
            Date date = originalFormat.parse(dateOfBirth);
            return targetFormat.format(date);
        } catch (ParseException e) {
            Log.e(LogTag, "Date parsing error: " + e.getMessage());
            return dateOfBirth; // Return the original date if parsing fails
        }
    }

    public String formatPhoneNumber(String contactNumber) {
        // Check if the phone number already has dashes
        if (contactNumber.matches("\\d{3}-\\d{3}-\\d{4}")) {
            return contactNumber; // Return as is if already formatted
        }

        // Remove any non-numeric characters
        contactNumber = contactNumber.replaceAll("[^\\d]", "");

        // Format the number if it has 10 digits
        if (contactNumber.length() == 10) {
            return contactNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else {
            // Return as is if it doesn't match the expected 10-digit length
            Log.e(LogTag, "Unexpected phone number length: " + contactNumber);
            return contactNumber;
        }
    }

    // Validation Helper
    private void validationSetError(TextView errorView, String message) {
        if (errorView != null) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void validationClearError(TextView errorView) {
        if (errorView != null) errorView.setVisibility(View.GONE);
    }

    public boolean validateInput(int inputViewId, int errorViewId) {
        EditText inputView = activity.findViewById(inputViewId);
        TextView errorView = errorViewId != 0 ? activity.findViewById(errorViewId) : null;
        String inputText = inputView.getText().toString().trim();

        // Check if the input is empty
        if (inputText.isEmpty()) {
            validationSetError(errorView, activity.getString(R.string.warning_field_required));
            return false;
        } else {
            validationClearError(errorView);
        }

        // Detect the input type based on inputType attribute
        int inputType = inputView.getInputType();
        switch (inputType) {
            case android.text.InputType.TYPE_CLASS_NUMBER:
            case android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL:
                try {
                    Double.parseDouble(inputText);
                    validationClearError(errorView);
                } catch (NumberFormatException e) {
                    validationSetError(errorView, activity.getString(R.string.warning_invalid_number));
                    return false;
                }
                break;

            case android.text.InputType.TYPE_CLASS_PHONE:
                if (!inputText.matches("\\d{3}-?\\d{3}-?\\d{4}")) {
                    validationSetError(errorView, activity.getString(R.string.warning_invalid_phone));
                    return false;
                } else {
                    validationClearError(errorView);
                }
                break;

            case android.text.InputType.TYPE_CLASS_DATETIME:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    dateFormat.parse(inputText);
                    validationClearError(errorView);
                } catch (ParseException e) {
                    validationSetError(errorView, activity.getString(R.string.warning_invalid_date));
                    return false;
                }
                break;

            default:
                validationClearError(errorView);
                break;
        }

        return true;
    }
}
