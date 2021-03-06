package com.example.project362.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project362.R;
import com.example.project362.models.Employee;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
	EditText editTextEmail, editTextPassword, editTextName;
	private FirebaseAuth mAuth;
	String email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		editTextEmail = findViewById(R.id.editTextEmail);
		editTextPassword = findViewById(R.id.editTextPassword);
		editTextName = findViewById(R.id.editTextName);

		mAuth = FirebaseAuth.getInstance();

		findViewById(R.id.buttonSignUp).setOnClickListener(this);
	}

	private void registerUser()
	{
		email = editTextEmail.getText().toString();
		password = editTextPassword.getText().toString();
		// if email is empty, notify user
		if (email.isEmpty())
		{
			editTextEmail.setError("Email is required");
			editTextEmail.requestFocus();
			return;
		}
		// if email does not match email form, notify user
		if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
		{
			editTextEmail.setError("please enter valid email");
			editTextEmail.requestFocus();
			return;
		}
		// if password is empty, notify user
		if (password.isEmpty())
		{
			editTextPassword.setError("password is required");
			editTextPassword.requestFocus();
			return;
		}
		// if password length is less than 6, notify user
		if (password.length() < 6)
		{
			editTextPassword.setError("Enter password that is atleast 6 characters long");
			editTextPassword.requestFocus();
			return;
		}

		// create the user in the auth database
		mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Task<AuthResult> task) -> {
			if (task.isSuccessful())
			{
				String name = editTextName.getText().toString();
				// make an employee for the new user
				Employee e = new Employee(mAuth.getUid(), email, name, "employee");
				// create employee in database
				e.create();

				Toast.makeText(SignUpActivity.this, "User Register Succsesful",
						Toast.LENGTH_SHORT).show();

				// go to home activity to navigate to rest of app
				Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(getApplicationContext(), task.getException().getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
			case R.id.buttonSignUp:
				registerUser();
				break;
			case R.id.textViewLogin:
				finish();
				// go to the main activity (login page)
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
				break;
		}
	}
}
