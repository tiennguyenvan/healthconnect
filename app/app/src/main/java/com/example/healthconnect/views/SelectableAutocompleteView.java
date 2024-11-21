package com.example.healthconnect.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.healthconnect.R;

import java.util.ArrayList;
import java.util.List;

public class SelectableAutocompleteView extends LinearLayout {

    private TextView tvLabel;
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout selectedItemsContainer;
    private ArrayAdapter<String> suggestionAdapter;
    private List<String> selectedItems = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Boolean allowOneItem = false;
    private TextView tvError;


    public Boolean getAllowOneItem() {
        return allowOneItem;
    }

    public void setAllowOneItem(Boolean allowOneItem) {
        this.allowOneItem = allowOneItem;
    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public SelectableAutocompleteView(Context context) {
        super(context);
        init(context, null);
    }

    public SelectableAutocompleteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectableAutocompleteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_selectable_auto_complete, this, true);

        tvLabel = findViewById(R.id.tvLabel);
        tvError = findViewById(R.id.tvError);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        selectedItemsContainer = findViewById(R.id.selectedItemsContainer);

        // Default orientation for selected items container
        selectedItemsContainer.setOrientation(LinearLayout.HORIZONTAL);

        // Load custom attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputAttrs);

            // Set label text
            String labelText = a.getString(R.styleable.InputAttrs_labelText);
            if (labelText != null) {
                tvLabel.setText(labelText);
            }

            // Set hint text
            String hintText = a.getString(R.styleable.InputAttrs_hintText);
            if (hintText != null) {
                autoCompleteTextView.setHint(hintText);
            }

            // Set display direction for selected items (horizontal or vertical)
            int direction = a.getInt(R.styleable.InputAttrs_displayDirection, 0);
            selectedItemsContainer.setOrientation(
                    direction == 1 ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);

            a.recycle();
        }

        // Set up item click listener for AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = suggestionAdapter.getItem(position);
            if (selectedItem != null) {
                addSelectedItem(selectedItem);

                // Clear the AutoCompleteTextView input after selection
                autoCompleteTextView.setText("");

                // Notify listener of item click if it’s set
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(selectedItem);
                }
            }
        });

        // Set up focus listener to show suggestions on focus
        autoCompleteTextView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown(); // Show suggestions immediately
            }
        });

        // Set up touch listener to show suggestions when clicked
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());
    }

    public void setError(String errorMessage) {
        if (errorMessage != null && !errorMessage.isEmpty()) {
            tvError.setText(errorMessage);
            tvError.setVisibility(View.VISIBLE);
        } else {
            clearError(); // If errorMessage is empty or null, clear the error
        }
    }

    public void clearError() {
        tvError.setText("");
        tvError.setVisibility(View.GONE);
    }


    // Method to set suggestion list
    public void setSuggestions(List<String> suggestions) {
        suggestionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
        autoCompleteTextView.setAdapter(suggestionAdapter);
    }

    // Method to set the onClick listener for each item selection
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Method to add a selected item and update the display
    private void addSelectedItem(String item) {
        if (allowOneItem) {
            selectedItems.clear();
        }
        if (!selectedItems.contains(item)) {
            selectedItems.add(item);
            updateSelectedItemsDisplay();
        }
    }

    // Method to update the selected items display in the container
    private void updateSelectedItemsDisplay() {
        selectedItemsContainer.removeAllViews(); // Clear existing views
        for (String item : selectedItems) {
            SelectedItemView itemView = new SelectedItemView(getContext());
            itemView.setItemText(item);

            // Set up the remove click listener for each item
            itemView.setOnRemoveClickListener(selectedItem -> removeSelectedItem(selectedItem));

            selectedItemsContainer.addView(itemView);
        }
    }

    // Method to remove a selected item and update the display
    private void removeSelectedItem(String item) {
        if (allowOneItem) {
            selectedItems.clear();
            updateSelectedItemsDisplay();
            return;
        }
        if (selectedItems != null && selectedItems.contains(item)) {
            selectedItems.remove(item);
            updateSelectedItemsDisplay();
        }

    }

    public void setSelectedItems(List<String> selectedItems) {
//        this.selectedItems = selectedItems;
        this.selectedItems = new ArrayList<>(selectedItems);
        updateSelectedItemsDisplay();
    }
    // Method to clear all selected items (optional)
    public void clearSelectedItems() {
        selectedItems.clear();
        updateSelectedItemsDisplay();
    }

    // Method to get all selected items
    public List<String> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }
}
