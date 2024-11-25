package com.example.healthconnect.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthconnect.R;

import java.util.ArrayList;
import java.util.List;

public class SearchRecyclerView<T> extends LinearLayout {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<T> itemList = new ArrayList<>();
    private List<T> filteredList = new ArrayList<>();
    private InnerAdapter adapter;
    private int itemLayoutResId;
    private OnBindItemListener<T> onBindItemListener;
    private OnItemClickListener<T> onItemClickListener;
    private OnSearchListener<T> onSearchListener;
    private Boolean isEnabled = true;
    public SearchRecyclerView(Context context) {
        super(context);
        initialize(context);
    }

    public SearchRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public SearchRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        // Inflate the custom layout defined in view_search_recycler.xml
        LayoutInflater.from(context).inflate(R.layout.view_search_recycler, this, true);

        // Initialize SearchView and RecyclerView
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Set up SearchView listeners for real-time filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    // 1. Method to set the item layout resource ID
    public void setItemLayout(int itemLayoutResId) {
        this.itemLayoutResId = itemLayoutResId;
        notifyDataChanged();
    }

    // 2. Method to set the item list
    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
        this.filteredList = new ArrayList<>(itemList); // Initialize filtered list with all items
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
        notifyDataChanged();
    }

    // 3. Method to set the item click listener
    public void setOnClickItem(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
        notifyDataChanged();
    }

    // 4. Method to set the search listener
    public void setOnSearch(OnSearchListener<T> listener) {
        this.onSearchListener = listener;
        notifyDataChanged();
    }

    // 5. Method to set up the binding of each item in the RecyclerView
    public void setOnBindItem(OnBindItemListener<T> listener) {
        this.onBindItemListener = listener;
        notifyDataChanged();
    }

    private void notifyDataChanged() {
//        Log.d("SearchRecyclerView", "Initializing adapter...");
//        Log.d("SearchRecyclerView", "Item Layout Res ID: " + itemLayoutResId);
//        Log.d("SearchRecyclerView", "Item List: " + itemList);
//        Log.d("SearchRecyclerView", "Bind Item Listener: " + onBindItemListener);

        if (itemLayoutResId != 0 && onBindItemListener != null && itemList != null) {
            if (adapter == null) {
                adapter = new InnerAdapter();
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    // Method to filter the list based on the search query
    private void filter(String query) {
        if (onSearchListener != null) {
            filteredList = onSearchListener.onSearch(query);
        } else {
            // Default filtering logic if no custom search logic is provided
            filteredList.clear();
            if (query == null || query.isEmpty()) {
                filteredList.addAll(itemList);
            } else {
                for (T item : itemList) {
                    if (item.toString().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }
        }
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }
        notifyDataChanged();
    }

    // Inner adapter class for managing item views in RecyclerView
    private class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            T item = filteredList.get(position);
            if (onBindItemListener != null) {
                onBindItemListener.onBindItem(holder.itemView, item);
            }
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredList != null ? filteredList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    // Listener interface for binding items
    public interface OnBindItemListener<T> {
        void onBindItem(View itemView, T item);
    }

    // Listener interface for item click events
    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    // Listener interface for custom search logic
    public interface OnSearchListener<T> {
        List<T> onSearch(String query);
    }

    public void setInputEnable(boolean enabled) {
        isEnabled = enabled;
        searchView.setEnabled(enabled);
        searchView.setFocusable(enabled);
        searchView.setFocusableInTouchMode(enabled);
        searchView.setVisibility(enabled ? View.VISIBLE : View.GONE);
        isEnabled = enabled;
    }
}
