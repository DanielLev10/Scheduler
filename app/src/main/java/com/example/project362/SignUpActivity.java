package com.example.project362;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextEmail, editTextPassword, editTextName;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword=  findViewById(R.id.editTextPassword);
        editTextName=  findViewById(R.id.editTextName);

        mAuth = FirebaseAuth.getInstance();



        findViewById(R.id.buttonSignUp).setOnClickListener(this);
       // findViewById(R.id.button_send).setOnClickListener(this);
    }

        private void registerUser(){
            String email= editTextEmail.getText().toString();
            String password= editTextPassword.getText().toString();
            if(email.isEmpty())
            {
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("please enter valid email");
                editTextEmail.requestFocus();
                return;
            }
            if (password.isEmpty())
            {
                editTextPassword.setError("password is required");
                editTextPassword.requestFocus();
                return;
            }
            if(password.length() < 6){
                editTextPassword.setError("Enter password that is atleast 6 characters long");
                editTextPassword.requestFocus();
                return;
            }



        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference employeeRef = db.collection("Employees");

                    Map<String, Object> newEmployeeData = new HashMap<>();
                    newEmployeeData.put("Email", mAuth.getCurrentUser().getEmail());
                    newEmployeeData.put("Status", "employee");
                    newEmployeeData.put("EmpID", mAuth.getCurrentUser().getUid());
                    newEmployeeData.put("Name", editTextName.getText().toString());

                    employeeRef.add(newEmployeeData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("SignUp", "Success");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Error - Sign Up", e);
                                }
                            });


                    //Toast.makeText(getApplicationContext(),"User Register Succsesful"), Toast.LENGTH_SHORT);
                    Toast.makeText(SignUpActivity.this, "User Register Succsesful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, profileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else{

                }
            }
        });
        }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.buttonSignUp:
                registerUser();
                break;
            case R.id.textViewLogin:
                finish();
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);


                break;
        }
    }
}