package com.example.healthconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.example.healthconnect.R;

public class SelectedItemView extends LinearLayout {

    private TextView tvRemoveIcon;
    private TextView tvItemText;
    private OnRemoveClickListener onRemoveClickListener;

    // Interface to handle the remove icon click
    public interface OnRemoveClickListener {
        void onRemoveClick(String itemText);
    }

    public SelectedItemView(Context context) {
        super(context);
        init(context);
    }

    public SelectedItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_selected_item, this, true);
        tvRemoveIcon = findViewById(R.id.tvRemoveIcon);
        tvItemText = findViewById(R.id.tvItemText);

        // Set up click listener for the remove icon
        tvRemoveIcon.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(tvItemText.getText().toString());
            }
        });
    }

    // Method to set the item text
    public void setItemText(String text) {
        tvItemText.setText(text);
    }

    // Method to set the remove click listener
    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.onRemoveClickListener = listener;
    }
}
