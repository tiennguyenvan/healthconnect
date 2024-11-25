package com.example.healthconnect;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.activities.appointment.AppointmentListActivity;
import com.example.healthconnect.activities.diagnose.DiagnoseListActivity;
import com.example.healthconnect.activities.medication.MedicationListActivity;
import com.example.healthconnect.activities.patient.PatientListActivity;
import com.example.healthconnect.activities.symptom.SymptomListActivity;
import com.example.healthconnect.activities.treatment.TreatmentListActivity;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.models.Treatment;

import java.util.List;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    private final String NOTIFIER_APPOINTMENT_CHANNEL_ID = "NOTIFIER_APPOINTMENT_CHANNEL_ID";
    private final CharSequence NOTIFIER_APPOINTMENT_CHANNEL_NAME = "HealthConnect Appointment";
    private final String NOTIFIER_APPOINTMENT_CHANNEL_DESCRIPTION = "Upcoming appointments booked in the HealthConnect app";
    private final int NOTIFIER_APPOINTMENT_NOTIFICATION_ID = 1;


    private NotificationManager notificationManager;

    private DbTable<Patient> patientTable;
    private DbTable<Medication> medicationTable;
    private DbTable<Appointment> appointmentTable;
    private DbTable<Symptom> symptomTable;
    private DbTable<Treatment> treatmentTable;
    private DbTable<Diagnose> diagnoseTable;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        patientTable = DbTable.getInstance(this, Patient.class);
        medicationTable = DbTable.getInstance(this, Medication.class);
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        symptomTable = DbTable.getInstance(this, Symptom.class);
        treatmentTable = DbTable.getInstance(this, Treatment.class);
        diagnoseTable = DbTable.getInstance(this, Diagnose.class);


        inThis.on(R.id.tvPatientCount).setText(String.valueOf(patientTable.size()));
        inThis.on(R.id.tvMedicationCount).setText(String.valueOf(medicationTable.size()));
        inThis.on(R.id.tvAppointmentCount).setText(String.valueOf(appointmentTable.size()));
        inThis.on(R.id.tvSymptomCount).setText(String.valueOf(symptomTable.size()));
        inThis.on(R.id.tvTreatmentCount).setText(String.valueOf(treatmentTable.size()));
        inThis.on(R.id.tvDiagnoseCount).setText(String.valueOf(diagnoseTable.size()));

        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        inThis.onClick(R.id.btAppointments).goToScreen(AppointmentListActivity.class);
        inThis.onClick(R.id.btMedications).goToScreen(MedicationListActivity.class);
        inThis.onClick(R.id.btSymptoms).goToScreen(SymptomListActivity.class);
        inThis.onClick(R.id.btTreatments).goToScreen(TreatmentListActivity.class);
        inThis.onClick(R.id.btDiagnoses).goToScreen(DiagnoseListActivity.class);


        updateLatestAppointment();
        updateMedicationClosestToEmpty();


        // notification
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        if (notificationManager == null) {
//            System.out.println("Notification Manager is null");
//        } else {
//            System.out.println("Notification Manager initialized");
//        }
        if (!areNotificationsEnabled()) {
            promptEnableNotifications();
        }
        createNotificationChannel();
        showNotification();
        startHeartbeatAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patientTable.closeDatabase();
        appointmentTable.closeDatabase();
    }

    private void updateLatestAppointment() {
        List<Appointment> appointments = appointmentTable.getAll();
        TextView latest = findViewById(R.id.tvAppointmentLatest);
        if (appointments.isEmpty()) {
            inThis.on(R.id.tvAppointmentLatest).setVisibility(View.GONE);
            return;
        }

        // Sort appointments by startDateTime closest to NOW
        appointments.sort(Appointment::sortByStartDateTime);
        Appointment latestAppointment = appointments.get(0);
        Patient patient = patientTable.getById(latestAppointment.getPatient_id());
        String latestAppointmentText = String.format(
                "%s  %s",
                patient != null ? patient.getName() : "Unknown",
                latestAppointment.getFormatStartDateTime()
        );

        inThis.on(R.id.tvAppointmentLatest).setText(latestAppointmentText);
        inThis.on(R.id.tvAppointmentLatest).setVisibility(View.VISIBLE);
    }

    private void updateMedicationClosestToEmpty() {
        List<Medication> medications = medicationTable.getAll();
        if (medications.isEmpty()) {
            inThis.on(R.id.tvMedicationEmpty).setVisibility(View.GONE);
            return;
        }

        // Find medication with lowest stock percentage using Comparator.comparingDouble
        Medication lowestStockMedication = Medication.getClosetToEmpty(medications);

        // Construct and display the closest-to-empty medication string
        String medicationEmptyText = String.format(
                getString(R.string.medication_main_page_status),
                lowestStockMedication.getMedicationName(),
                lowestStockMedication.getStock(),
                lowestStockMedication.getMaxStock()
        );

        inThis.on(R.id.tvMedicationEmpty).setText(medicationEmptyText);
        inThis.on(R.id.tvMedicationEmpty).setVisibility(View.VISIBLE);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // NOTIFICATION
    private void createNotificationChannel() {
//        Log.d("Build.VERSION.SDK_INT", "createNotificationChannel: " + Build.VERSION.SDK_INT);
//        Log.d("Build.VERSION_CODES.O", "createNotificationChannel: " + Build.VERSION_CODES.O);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFIER_APPOINTMENT_CHANNEL_ID,
                    NOTIFIER_APPOINTMENT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(NOTIFIER_APPOINTMENT_CHANNEL_DESCRIPTION);

            // Register the channel with the NotificationManager
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
//                System.out.println("Notification channel created");
            } else {
//                System.out.println("Notification Manager is null");
            }
        }
    }


    public void showNotification() {
        // Get the soonest upcoming appointment in the next 30 minutes
        Appointment soonestAppointment = getSoonestUpcomingAppointment();

        if (soonestAppointment == null) {
//            System.out.println("No appointments within the next 30 minutes");
            return;
        }

        // Fetch patient details (if needed)
        Patient patient = patientTable.getById(soonestAppointment.getPatient_id());
        String patientName = (patient != null) ? patient.getName() : "Unknown";

        // Format the notification content
        String title = "Upcoming Appointment";
        String content = String.format(
                "Appointment with %s at %s",
                patientName,
                soonestAppointment.getFormatStartDateTime() // Format time (e.g., "10:00 AM - Nov 24")
        );

        // Create PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        Notification.Builder builder = new Notification.Builder(this, NOTIFIER_APPOINTMENT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // Replace with your icon
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }

    public void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8.0 and above
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else { // For older Android versions
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }


    public void promptEnableNotifications() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("HealthConnect uses notifications to remind you of upcoming appointments and updates. Please enable notifications in your app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> openNotificationSettings())
                .setNegativeButton("Cancel", null)
                .show();
    }

    public boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private Appointment getSoonestUpcomingAppointment() {
        List<Appointment> appointments = appointmentTable.getAll(); // Fetch all appointments
        LocalDateTime now = LocalDateTime.now(); // Current time
        LocalDateTime thirtyMinutesFromNow = now.plusMinutes(30);

        // Filter for appointments that start within the next 30 minutes
        return appointments.stream()
                .filter(a -> {
                    LocalDateTime startDateTime = LocalDateTime.parse(a.getStartDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    return startDateTime.isAfter(now) && startDateTime.isBefore(thirtyMinutesFromNow);
                })
                .min((a1, a2) -> { // Find the soonest one
                    LocalDateTime start1 = LocalDateTime.parse(a1.getStartDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    LocalDateTime start2 = LocalDateTime.parse(a2.getStartDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    return start1.compareTo(start2);
                })
                .orElse(null); // Return null if no appointment found
    }

    ///////////
    // Animation
    private void startHeartbeatAnimation() {
        ImageView logo = findViewById(R.id.ivHeathConnectLogo);

        // Fade in (heartbeat effect)
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0.6f, 1f);
        fadeIn.setDuration(100); // Sudden glow
//        fadeIn.setInterpolator(new AccelerateInterpolator());

        // Fade out to rest
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0.6f);
        fadeOut.setDuration(300); // Smoothly return to rest
//        fadeOut.setInterpolator(new DecelerateInterpolator());

        // Rest phase (hold still with no opacity change)
        ObjectAnimator restPause = ObjectAnimator.ofFloat(logo, "alpha", 0.6f, 0.6f);
        restPause.setDuration(900); // Pause for 500ms

        // Combine animations into a sequence
        AnimatorSet heartbeatAnimator = new AnimatorSet();
        heartbeatAnimator.playSequentially(fadeIn, fadeOut, restPause);

        // Loop the animation indefinitely
        heartbeatAnimator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                heartbeatAnimator.start(); // Restart animation
            }

            @Override
            public void onAnimationStart(android.animation.Animator animation) {}
            @Override
            public void onAnimationCancel(android.animation.Animator animation) {}
            @Override
            public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        // Start the animation
        heartbeatAnimator.start();
    }

}