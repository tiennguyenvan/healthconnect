package com.example.healthconnect.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.healthconnect.R;

public class LabeledInputField extends LinearLayout {

    private TextView tvLabel;
    private EditText etInput;
    private TextView tvError;

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
                int inputType = a.getInt(R.styleable.InputAttrs_android_inputType, -1);

                if (!TextUtils.isEmpty(labelText)) {
                    tvLabel.setText(labelText);
                }
                if (!TextUtils.isEmpty(hintText)) {
                    etInput.setHint(hintText);
                }
                if (inputType != -1) {
                    etInput.setInputType(inputType);
                }
            } finally {
                a.recycle();
            }
        }
    }

    // Method to set the label text
    public void setText(String text) {
        setLabelText(text);
    }
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

    // Method to retrieve the input text
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
}
