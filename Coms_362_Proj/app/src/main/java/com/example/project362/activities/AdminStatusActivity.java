package com.example.project362.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project362.R;
import com.example.project362.models.Admin;
import com.example.project362.models.Employee;
import com.example.project362.models.Shift;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminStatusActivity extends AppCompatActivity implements View.OnClickListener
{
	EditText userEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_status);
		userEmail = findViewById(R.id.employeeEmail);

		findViewById(R.id.grantAdmin).setOnClickListener(AdminStatusActivity.this);
		findViewById(R.id.deleteEmployee).setOnClickListener(AdminStatusActivity.this);
	}

	private void deleteUser()
	{
		String email = userEmail.getText().toString().trim();

		Employee.delete(email).addOnCompleteListener((Task<Void> t) -> {
			if (t.isSuccessful())
			{
				Admin.delete(email).addOnCompleteListener((Task<Void> dt) -> {
					if (dt.isSuccessful())
						Toast.makeText(AdminStatusActivity.this, "Deleted employee",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(AdminStatusActivity.this, "Failed to delete employee",
								Toast.LENGTH_SHORT).show();
				});
			}
			else
				Toast.makeText(AdminStatusActivity.this, "Failed to delete employee",
						Toast.LENGTH_SHORT).show();
		});

		Shift.getShifts().addOnCompleteListener((Task<QuerySnapshot> task) -> {
			if (task.isSuccessful() && task.getResult() != null)
				for (QueryDocumentSnapshot document : task.getResult())
					new Shift(document).removeEmployee(email);
			else
				Toast.makeText(AdminStatusActivity.this, "Employee not found in the Database", Toast.LENGTH_SHORT).show();
		});
	}

	private void createAdmin()
	{
		final String email = userEmail.getText().toString().trim();

		Employee.getEmployeeByEmail(email)
				.addOnCompleteListener((Task<DocumentSnapshot> task) -> {
					if (task.isSuccessful())
					{
						Admin.create(email)
								.addOnCompleteListener((Task<Void> t) -> {
									if (t.isSuccessful())
										Toast.makeText(AdminStatusActivity.this, "Admin " +
												"created", Toast.LENGTH_SHORT).show();
									else
										Toast.makeText(AdminStatusActivity.this, "Failed " +
												"to create", Toast.LENGTH_SHORT).show();
								});
					}
					else
						Toast.makeText(AdminStatusActivity.this, "Employee not found in the " +
								"Database", Toast.LENGTH_SHORT).show();
				});

	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.deleteEmployee:
				finish();
				deleteUser();
				break;
			case R.id.grantAdmin:
				finish();
				createAdmin();
				break;
		}
	}
}