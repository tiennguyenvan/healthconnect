package com.example.healthconnect.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.healthconnect.R;

import java.util.Calendar;
import java.util.Locale;

public class LabeledInputField extends LinearLayout {

    private TextView tvLabel;
    private EditText etInput;
    private TextView tvError;
    private int inputType;
    private boolean isEnabled = true;

    public LabeledInputField(Context context) {
        super(context);
        init(context, null);
    }

    public LabeledInputField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LabeledInputField(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        // Inflate the custom layout
        LayoutInflater.from(context).inflate(R.layout.view_labeled_input, this, true);

        // Initialize views
        tvLabel = findViewById(R.id.tvLabel);
        etInput = findViewById(R.id.etInput);
        tvError = findViewById(R.id.tvError);

        // Load custom attributes (if any)
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.InputAttrs, 0, 0);

            try {
                String labelText = a.getString(R.styleable.InputAttrs_labelText);
                String hintText = a.getString(R.styleable.InputAttrs_hintText);
                inputType = a.getInt(R.styleable.InputAttrs_inputType, -1);

                if (!TextUtils.isEmpty(labelText)) {
                    tvLabel.setText(labelText);
                }
                if (!TextUtils.isEmpty(hintText)) {
                    etInput.setHint(hintText);
                }
                if (inputType != -1) {
                    etInput.setInputType(inputType);
                }

                // Attach listeners for date/time picker based on input type
                setupInputTypeBehavior(context);

            } finally {
                a.recycle();
            }
        }
    }
    private void setupInputTypeBehavior(Context context) {
        if (inputType == 2) { // Date Picker
            etInput.setFocusable(false); // Prevent keyboard popup
            etInput.setOnClickListener(v -> showDatePicker(context, 0));
        } else if (inputType == 3) { // Time Picker
            etInput.setFocusable(false);
            etInput.setOnClickListener(v -> showTimePicker(context));
        } else if (inputType == 4) { // Past Date Only
            etInput.setFocusable(false);
            etInput.setOnClickListener(v -> showDatePicker(context, -1));
        } else if (inputType == 5) {
            etInput.setFocusable(false);
            etInput.setOnClickListener(v -> showDatePicker(context, 1));
        }
    }

    private void showDatePicker(Context context, int timeline) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etInput.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (timeline < 0) {
            datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis()); // past day only
        } else if (timeline > 0) {
            datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()); // future day only
        }

        datePickerDialog.show();
    }


    private void showTimePicker(Context context) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etInput.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true); // Use 24-hour format
        timePickerDialog.show();
    }

    // Method to set the label text
    public void setLabelText(String text) {
        tvLabel.setText(text);
    }

    // Method to set the hint text
    public void setHintText(String hint) {
        etInput.setHint(hint);
    }

    // Method to set input type for the EditText
    public void setInputType(int inputType) {
        etInput.setInputType(inputType);
    }
    public int getInputType() {
        return etInput.getInputType();
    }

    public void setText(String text) {
        setInputText(text);
    }
    public void setInputText(String text) {
        etInput.setText(text);
    }
    public String getText() {
        return getInputText();
    }
    public String getInputText() {
        return etInput.getText().toString();
    }

    // Method to set an error message
    public void setError(String error) {
        if (!TextUtils.isEmpty(error)) {
            tvError.setText(error);
            tvError.setVisibility(VISIBLE);
        } else {
            tvError.setVisibility(GONE);
        }
    }

    // Method to clear the error message
    public void clearError() {
        tvError.setText("");
        tvError.setVisibility(GONE);
    }

    // Method to get the EditText, if further customization is needed
    public EditText getEditText() {
        return etInput;
    }

    public void setInputEnable(boolean enabled) {
        etInput.setEnabled(enabled);
        etInput.setFocusable(enabled);
        etInput.setFocusableInTouchMode(enabled);
//        etInput.setAlpha(enabled ? 1.0f : 0.5f);
        isEnabled = enabled;
    }
}
