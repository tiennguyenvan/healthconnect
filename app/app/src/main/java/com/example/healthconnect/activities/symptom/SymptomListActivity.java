package com.example.healthconnect.activities.symptom;

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
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.views.SearchRecyclerView;

public class SymptomListActivity extends AppCompatActivity {
    DbTable<Symptom> symptomDbTable;
    private $ inThis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_symptom_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        symptomDbTable = DbTable.getInstance(this, Symptom.class);

        inThis.onClick(R.id.btSymptomListToSymptomForm).goToScreen(SymptomFormActivity.class);
        inThis.onClick(R.id.btSymptomListToMain).goToScreen(MainActivity.class);

        SearchRecyclerView<Symptom> srvSymptom = findViewById(R.id.srvSymptom);
        srvSymptom.setItemList(symptomDbTable.getAll());
        srvSymptom.setItemLayout(R.layout.component_symptom_item);
        srvSymptom.setOnBindItem((itemView, symptom) -> {
            TextView tvSymptomName = itemView.findViewById(R.id.tvSymptomName);
            tvSymptomName.setText(symptom.getName());
        });
        srvSymptom.setOnClickItem((symptom -> {
            inThis.passToScreen(SymptomFormActivity.class, getString(R.string.key_symptom_id), symptom.getId());
        }));
        srvSymptom.setOnSearch(query -> symptomDbTable.searchBy(Symptom.columnName(), query));
    }
}