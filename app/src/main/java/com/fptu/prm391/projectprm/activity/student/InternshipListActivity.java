package com.fptu.prm391.projectprm.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.activity.auth.LoginActivity;
import com.fptu.prm391.projectprm.adapter.InternshipAdapter;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;
import com.fptu.prm391.projectprm.util.SharedPrefManager;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InternshipListActivity extends AppCompatActivity implements InternshipAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private Spinner spinnerField, spinnerSort;
    private InternshipAdapter adapter;
    private List<Internship> internshipList;
    private List<Internship> filteredList;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        recyclerView = findViewById(R.id.recycler_internships);
        spinnerField = findViewById(R.id.spinner_filter_field);
        spinnerSort = findViewById(R.id.spinner_sort);

        InternshipDAO dao = new InternshipDAO(new DatabaseHelper(this).getReadableDatabase());
        internshipList = dao.getAllInternships(); // SQLite or Firebase
        filteredList = new ArrayList<>(internshipList);

        adapter = new InternshipAdapter(filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupSpinners();

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void setupSpinners() {
        // Field filter (ngành)
        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                getFieldList()
        );
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerField.setAdapter(fieldAdapter);

        spinnerField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                filterAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Sort options
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Mới nhất", "Cũ nhất"}
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                filterAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void filterAndSort() {
        String selectedField = spinnerField.getSelectedItem().toString();
        String selectedSort = spinnerSort.getSelectedItem().toString();

        filteredList.clear();

        for (Internship item : internshipList) {
            if (selectedField.equals("Tất cả") || item.getField().equalsIgnoreCase(selectedField)) {
                filteredList.add(item);
            }
        }

        // Sort by createdAt
        Collections.sort(filteredList, new Comparator<Internship>() {
            @Override
            public int compare(Internship a, Internship b) {
                try {
                    Date dateA = sdf.parse(a.getCreatedAt());
                    Date dateB = sdf.parse(b.getCreatedAt());
                    return selectedSort.equals("Mới nhất")
                            ? dateB.compareTo(dateA)
                            : dateA.compareTo(dateB);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    private List<String> getFieldList() {
        List<String> fields = new ArrayList<>();
        fields.add("Tất cả");
        for (Internship i : internshipList) {
            if (!fields.contains(i.getField())) {
                fields.add(i.getField());
            }
        }
        return fields;
    }

    @Override
    public void onItemClick(Internship internship) {
        Intent intent = new Intent(this, InternshipDetailActivity.class);
        intent.putExtra("INTERNSHIP_ID", internship.getId());
        startActivity(intent);
    }


}
