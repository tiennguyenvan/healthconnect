package com.example.healthconnect.activities.diagnose;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.MainActivity;
import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.views.SearchRecyclerView;

public class DiagnoseListActivity extends AppCompatActivity {
    private DbTable<Diagnose> diagnoseDbTable;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diagnose_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        diagnoseDbTable = DbTable.getInstance(this, Diagnose.class);

        // Navigate to DiagnoseFormActivity or MainActivity
        inThis.onClick(R.id.btDiagnoseListToDiagnoseForm).goToScreen(DiagnoseFormActivity.class);
        inThis.onClick(R.id.btDiagnoseListToMain).goToScreen(MainActivity.class);

        // Setup the SearchRecyclerView
        SearchRecyclerView<Diagnose> srvDiagnose = findViewById(R.id.srvDiagnose);
        srvDiagnose.setItemList(diagnoseDbTable.getAll());
        srvDiagnose.setItemLayout(R.layout.component_diagnose_item);
        srvDiagnose.setOnBindItem((itemView, diagnose) -> {
            TextView tvDiagnoseName = itemView.findViewById(R.id.tvDiagnoseName);
            tvDiagnoseName.setText(diagnose.getName());
        });
        srvDiagnose.setOnClickItem(diagnose -> {
            inThis.passToScreen(DiagnoseFormActivity.class, getString(R.string.key_diagnose_id), diagnose.getId());
        });
        srvDiagnose.setOnSearch(query -> diagnoseDbTable.searchBy(Diagnose.columnName(), query));
    }
}
