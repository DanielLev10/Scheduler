package com.example.project362.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project362.R;
import com.example.project362.models.Employee;
import com.example.project362.models.Shift;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class ShiftAttendanceAdapter extends RecyclerView.Adapter<ShiftAttendanceAdapter.ShiftsViewHolder> {

    private ArrayList<Shift> shiftList;

    static class ShiftsViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView info;
        private final TextView attendance;
        private final EditText attendanceAdd;
        private final Button attendanceButton;
        private final TextView employees;

        ShiftsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.shiftTitle);
            info = itemView.findViewById(R.id.shiftInfo);

            employees = itemView.findViewById(R.id.shiftEmployees);

            attendanceAdd = itemView.findViewById(R.id.editTextShiftAttendance);
            attendance = itemView.findViewById(R.id.epmployeeAttendance);
            attendanceButton = itemView.findViewById(R.id.attendanceButton);

        }
    }

    public ShiftAttendanceAdapter(ArrayList<Shift> shifts)
    {
        shiftList = shifts;
    }

    @NonNull
    @Override
    public ShiftAttendanceAdapter.ShiftsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shift_card_admin_checkin,
                viewGroup, false);
        ShiftAttendanceAdapter.ShiftsViewHolder svh = new ShiftAttendanceAdapter.ShiftsViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ShiftAttendanceAdapter.ShiftsViewHolder shiftsViewHolder, int i) {
        final Shift currentShift = shiftList.get(i);
        shiftsViewHolder.title.setText("Shift ID: " + currentShift.getId());
        shiftsViewHolder.info.setText("Description: This shift starts on " + currentShift.getStartTime() + " and ends on "
                + currentShift.getEndTime() + ". ");

        shiftsViewHolder.employees.setText(this.formatEmployees(currentShift.getEmployees()));
        shiftsViewHolder.attendance.setText(currentShift.getNote());

        shiftsViewHolder.attendanceButton.setOnClickListener((View v) ->
        {
            // Code here executes on main thread after user presses button
            String text = shiftsViewHolder.attendanceAdd.getText().toString();
            String n = shiftsViewHolder.attendance.getText().toString();
            String note = n + "\n" + text;

            currentShift.setAttendance(note).addOnCompleteListener((Task<Void> task) ->
            {
                if (task.isSuccessful())
                    shiftsViewHolder.attendance.setText(currentShift.getAttendance());
            });

            shiftsViewHolder.attendanceAdd.setText("");
        });


    }

    String formatEmployees(ArrayList<DocumentReference> employees)
    {
        StringBuilder employeesSb = new StringBuilder();
        for (DocumentReference ref : employees)
            employeesSb.append(ref.getId()).append("\n");
        return employeesSb.toString();
    }

    @Override
    public int getItemCount() {
        return shiftList.size();
    }

}