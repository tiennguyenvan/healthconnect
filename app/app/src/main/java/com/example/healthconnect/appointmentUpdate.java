package com.example.healthconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class appointmentUpdate extends AppCompatActivity {
    private EditText etStartDate, etDuration;
    private Button btnUpdateAppointment;
    private DatabaseHelper databaseHelper;
    private long appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etStartDate = findViewById(R.id.etStartDate);
        etDuration = findViewById(R.id.etDuration);
        btnUpdateAppointment = findViewById(R.id.btnUpdateAppointment);

        databaseHelper = DatabaseHelper.getInstance(this);
        appointmentId = getIntent().getLongExtra("appointmentId", -1);

        if (appointmentId == -1) {
            Toast.makeText(this, "Invalid appointment ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        btnUpdateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = etStartDate.getText().toString().trim();
                String durationStr = etDuration.getText().toString().trim();

                if (startDate.isEmpty() || durationStr.isEmpty()) {
                    Toast.makeText(appointmentUpdate.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    double duration = Double.parseDouble(durationStr);
                    boolean isUpdated = databaseHelper.appointmentUpdate(appointmentId, startDate, duration);

                    if (isUpdated) {
                        Toast.makeText(appointmentUpdate.this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(appointmentUpdate.this, "Failed to update appointment", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(appointmentUpdate.this, "Invalid duration format", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
