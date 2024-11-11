package com.example.healthconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthconnect.R;

import java.util.ArrayList;
import java.util.List;

public class SRvAdapter<T> {

    private Context context;
    private View parentView;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<T> itemList;
    private List<T> filteredList;
    private InnerAdapter adapter;
    private int itemLayoutResId;
    private OnBindItemListener<T> onBindItemListener;
    private OnItemClickListener<T> onItemClickListener;
    private OnSearchListener<T> onSearchListener;

    public SRvAdapter(Activity activity, int parentViewResId) {
        this.context = activity;
        this.parentView = activity.findViewById(parentViewResId);
        if (this.parentView == null) {
            throw new IllegalArgumentException("View with id " + parentViewResId + " not found");
        }
        // Initialize searchView and recyclerView from parentView
        this.searchView = parentView.findViewById(R.id.searchView);
        this.recyclerView = parentView.findViewById(R.id.recyclerView);
        if (recyclerView == null) {
            throw new IllegalArgumentException("RecyclerView with id 'recyclerView' not found in the parent view");
        }
        if (searchView == null) {
            throw new IllegalArgumentException("SearchView with id 'searchView' not found in the parent view");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    // Define interfaces for the listeners
    public interface OnBindItemListener<T> {
        void onBindItem(View itemView, T item);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T item);
    }

    public interface OnSearchListener<T> {
        List<T> onSearch(String query);
    }

    // Methods to set the listeners and data
    public void setOnLoadEntries(List<T> itemList) {
        this.itemList = itemList;
        this.filteredList = new ArrayList<>(itemList);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        checkAndInitializeAdapter();
    }

    public void setItemTemplate(int itemLayoutResId) {
        this.itemLayoutResId = itemLayoutResId;
        checkAndInitializeAdapter();
    }

    public void onBindItem(OnBindItemListener<T> listener) {
        this.onBindItemListener = listener;
        checkAndInitializeAdapter();
    }

    public void onClickItem(OnItemClickListener<T> listener) {
        this.onItemClickListener = listener;
    }

    public void onSearch(OnSearchListener<T> listener) {
        this.onSearchListener = listener;
    }

    private void checkAndInitializeAdapter() {
        if (itemLayoutResId != 0 && onBindItemListener != null && itemList != null && recyclerView != null) {
            if (filteredList == null) {
                filteredList = new ArrayList<>(itemList);
            }
            if (adapter == null) {
                adapter = new InnerAdapter();
                recyclerView.setAdapter(adapter);

                // Set up searchView listener
                if (searchView != null) {
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
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void filter(String query) {
        if (onSearchListener != null) {
            filteredList = onSearchListener.onSearch(query);
        } else {
            // Default filtering logic (optional)
            if (query == null || query.isEmpty()) {
                filteredList = new ArrayList<>(itemList);
            } else {
                filteredList = new ArrayList<>();
                for (T item : itemList) {
                    if (item.toString().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(itemLayoutResId, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}


I don't like this part
this.searchView = parentView.findViewById(R.id.searchView);
        this.recyclerView = parentView.findViewById(R.id.recyclerView);


Because that means I have to have something like this all the time:
<LinearLayout style="@style/Field">
            <androidx.appcompat.widget.SearchView
                android:id="@+id/sVMedicationList"
                style="@style/FieldInput"
                android:padding="5dp"
                android:hint="@string/search_placeholder"
                android:queryHint="@string/search_placeholder" />
        </LinearLayout>
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvMedicationList"
            style="@style/ListItems"
            android:layout_weight="1" />


But I want to use a custom compontent/fragment like: 
<SearchRecycler .... /> to maintain concistency in the the whole app.