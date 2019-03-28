package com.example.project362.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.project362.R;
import com.example.project362.adapters.PendingShiftSwapsAdapter;

public class HomeActivity extends AppCompatActivity
{
	private Button allShiftsButton;
	private Button pendingReceivedSwapRequestsButton;

	@Override
	public void onCreate(Bundle savedInstanceBundle)
	{
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.activity_home);

		allShiftsButton = findViewById(R.id.allShiftsButton);
		pendingReceivedSwapRequestsButton = findViewById(R.id.pendingReceivedSwapRequestsButton);
	}

	public void onStart()
	{
		super.onStart();
		this.allShiftsButton.setOnClickListener((View v) -> {
			Intent i = new Intent(this, ViewAllShiftsActivity.class);
			startActivity(i);
		});

		this.pendingReceivedSwapRequestsButton.setOnClickListener((View v) -> {
			Intent i = new Intent(this, ViewPendingRequestsToUserActivity.class);
			startActivity(i);
		});

		// ADD LISTENERS HERE
	}
}
