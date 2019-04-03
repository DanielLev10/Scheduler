package com.example.project362.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.project362.R;
import com.example.project362.adapters.ShiftAttendanceAdapter;
import com.example.project362.models.Shift;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewAttendanceActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView recyclerView;

    public ArrayList<Shift> shifts = new ArrayList<Shift>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_shifts);
        recyclerView = findViewById(R.id.shiftsList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // get onclick for floating action button
        findViewById(R.id.floatingActionButtonEdit).setOnClickListener(ViewAttendanceActivity.this);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ///Display all shift cards
        Shift.getShifts().addOnCompleteListener((Task<QuerySnapshot> task) -> {
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot shiftDoc : task.getResult())
                    shifts.add(new Shift(shiftDoc));
                recyclerView.setAdapter(new ShiftAttendanceAdapter(shifts));
            }
            else
                Toast.makeText(ViewAttendanceActivity.this, "No Shifts At This Time!",
                        Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.floatingActionButtonEdit:
                finish();
                Intent i = new Intent(this, EditInfoActivity.class);
                startActivity(i);
                break;
        }
    }

}


