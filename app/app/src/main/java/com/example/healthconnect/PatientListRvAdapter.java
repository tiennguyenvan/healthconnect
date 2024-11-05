package com.example.healthconnect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PatientListRvAdapter extends RecyclerView.Adapter<PatientListRvAdapter.PatientViewHolder> {

    private List<Patient> patientList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Patient patient);
    }

    public PatientListRvAdapter(List<Patient> patientList, OnItemClickListener listener) {
        this.patientList = patientList;
        this.listener = listener;
    }

    public void updateList(List<Patient> newList) {
        patientList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_patient_item, parent, false);
        return new PatientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.bind(patient, listener);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
        }

        public void bind(final Patient patient, final OnItemClickListener listener) {
            tvPatientName.setText(patient.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(patient));
        }
    }
}
