package com.example.project362.models;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings({"unchecked", "WeakerAccess"})
public class Shift
{
	private static final String TAG = "com-s-362-shift-project";

	private static final String START_TIME = "startTime";
	private static final String END_TIME = "endTime";
	private static final String EMPLOYEES = "employees";
	private static final String CHECKEDIN = "checkedIn";
	private static final String NOTE = "note";
	private static final String LOCK = "lock";
	private static final String ATTENDANCE = "attendance";

	private static final String NAME="Name";
	private static final String COLLECTION = "Shifts";


	private int lock;

	// used for locking shifts
	public enum LockStatus {
		LOCKED("LOCKED", 0), UNLOCKED("UNLOCKED", 1);

		private final int value;
		private final String desc;

		LockStatus(String desc, int value)
		{
			this.desc = desc;
			this.value = value;
		}

		public int getValue()
		{
			return this.value;
		}

		public String toString()
		{
			return this.desc;
		}

		public static LockStatus getStatus(int i)
		{
			return LockStatus.values()[i];
		}
	}

	// toggles this shifts status
	public Task<Void> toggleStatus() {
		this.lock = (this.lock == 1) ? 0 : 1;
		return this.update(LOCK, this.lock);
	}

	public int getStatus()
	{
		return lock;
	}

	private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
	private	String name;
	private String id;
	private Date startTime;
	private Date endTime;
	private ArrayList<DocumentReference> employees;
	private ArrayList<DocumentReference> checkedIn;
	private String note;
	private String attendance; //TODO do we still use this?


	// make from a document snapshot from the db
	public Shift(DocumentSnapshot docSnap)
	{
		this.copyFromDocumentSnapshot(docSnap);
	}

	public Shift(String name, Date startTime, Date endTime)
	{
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.employees = new ArrayList<>();
		this.checkedIn = new ArrayList<>();
		this.note = "";
		this.attendance = "";
		this.lock = LockStatus.UNLOCKED.getValue();
	}

	// set the start time and update in db
	public Task<Void> setStartTime(final Date date)
	{
		return this.update(START_TIME, date).addOnCompleteListener((Task<Void> task) ->
		{
			if (task.isSuccessful()) Shift.this.startTime = date;
			else
			{
				if (task.getException() != null)
					Log.e(TAG, task.getException().toString());
				throw new Error("Operation unsuccessful");
			}
		});
	}

	public Task<Void> setEndTime(final Date date)
	{
		return this.update(END_TIME, date).addOnCompleteListener((Task<Void> task) ->
		{
			if (task.isSuccessful()) Shift.this.endTime = date;
			else
			{
				if (task.getException() != null)
					Log.e(TAG, task.getException().toString());
				throw new Error("Operation unsuccessful");
			}
		});
	}

	public Task<Void> setEmployees(final ArrayList<DocumentReference> employees)
	{
		return this.update(EMPLOYEES, employees).addOnCompleteListener((Task<Void> t) ->
		{
			if (t.isSuccessful()) Shift.this.employees = employees;
			else
			{
				if (t.getException() != null)
					Log.e(TAG, t.getException().toString());
			}
		});
	}

	public Task<Void> addEmployee(final DocumentReference employee)
	{
		if (this.lock == LockStatus.LOCKED.getValue()) return Tasks.forException(new Exception("It's locked yo"));
		final ArrayList<DocumentReference> temp = new ArrayList<>(employees);

		boolean contained = false;
		for (int i = 0; i < temp.size(); i++)
		{
			if (temp.get(i).getId().equals(employee.getId()))
			{
				contained = true;
				break;
			}
		}

		if (contained)
			return Tasks.forException(new Exception("This employee is already to the selected" +
					" " +
					"shift"));

		temp.add(employee);

		return this.update(EMPLOYEES, temp).addOnCompleteListener((Task<Void> t) ->
		{
			if (t.isSuccessful()) Shift.this.employees = temp;
			else
			{
				if (t.getException() != null)
					Log.e(TAG, t.getException().toString());
			}
		});
	}

	public Task<Void> checkInEmployee(final DocumentReference employee)
	{
		if (this.lock == LockStatus.LOCKED.getValue()) return Tasks.forException(new Exception("It's locked yo"));
		final ArrayList<DocumentReference> temp = new ArrayList<>(checkedIn);
		final ArrayList<DocumentReference> emps = new ArrayList<>(employees);

		boolean contained = false;
		for (int i = 0; i < emps.size(); i++)
		{
			if (emps.get(i).getId().equals(employee.getId()))
			{
				contained = true;
				break;
			}
		}

		if (contained){
			temp.add(employee);
		}

		return this.update(CHECKEDIN, temp).addOnCompleteListener((Task<Void> t) ->
		{
			if (t.isSuccessful()) Shift.this.checkedIn = temp;

		});



	}

	public Task<Void> removeEmployee(DocumentReference employee)
	{
		if (this.lock == LockStatus.LOCKED.getValue()) return Tasks.forException(new Exception("It's locked yo"));
		final ArrayList<DocumentReference> temp = new ArrayList<>(employees);

		boolean contained = false;
		for (int i = 0; i < temp.size(); i++)
		{
			if (temp.get(i).getId().equals(employee.getId()))
			{
				temp.remove(i);
				contained = true;
				Log.d(TAG, "found");
				break;
			}
		}

		if (!contained)
			return Tasks.forException(new Exception("That employee is not assigned to the " +
					"selected shift"));

		return this.update(EMPLOYEES, temp).addOnCompleteListener((Task<Void> task) ->
		{
			if (task.isSuccessful()) Shift.this.employees = temp;
			else if (task.getException() != null) Log.e(TAG, task.getException().toString());
		});
	}

	public Task<Void> removeEmployee(int i)
	{
		if (i >= this.employees.size())
			return Tasks.forException(new Exception("That employee does not exist"));

		final ArrayList<DocumentReference> temp = new ArrayList<>(this.employees);

		return this.update(EMPLOYEES, temp).addOnCompleteListener((Task<Void> t) -> {
			if (t.isSuccessful()) Shift.this.employees = temp;
			else if (t.getException() != null)
				Log.e(TAG, t.getException().toString());
		});
	}

	public Task<Void> removeEmployee(String id)
	{
		for (int i = 0; i < this.employees.size(); i++)
			if (this.employees.get(i).getId().equals(id))
				return this.removeEmployee(i);

		return Tasks.forException(new Exception("Employee not found"));
	}

	public Task<Void> setNote(final String note)
	{
		return this.update(NOTE, note).addOnCompleteListener((Task<Void> t) ->
		{
			if (t.isSuccessful()) Shift.this.note = note;
			else
			{
				if (t.getException() != null)
					Log.e(TAG, t.getException().toString());
			}
		});
	}

	public Task<Void> setAttendance(final String attendance)
	{
		return this.update(ATTENDANCE, attendance).addOnCompleteListener((Task<Void> t) ->
		{
			if (t.isSuccessful()) Shift.this.attendance = attendance;
			else
			{
				if (t.getException() != null)
					Log.e(TAG, t.getException().toString());
			}
		});
	}

	public String setId(String id)
	{
		return this.id = id;
	}

	public Date getStartTime()
	{
		return this.startTime;
	}

	public Date getEndTime()
	{
		return this.endTime;
	}

	public ArrayList<DocumentReference> getEmployees()
	{
		return this.employees;
	}

	public ArrayList<DocumentReference> getCheckedIn()
	{
		return this.checkedIn;
	}

	public String getNote()
	{
		return this.note;
	}

	public String getAttendance()
	{
		return this.attendance;
	}

	public String getId()
	{
		return this.id;
	}

	public DocumentReference getReference()
	{
		return db.collection(COLLECTION).document(this.id);
	}

	public String getName()
	{
		return this.name;
	}

	/**
	 * Performs a <strong>SHALLOW</strong> copy on the attributes of the given
	 * Shift into the attributes of this shit
	 *
	 * @param src - Shift to copy attributes from
	 */
	public void copyFromDocumentSnapshot(DocumentSnapshot src)
	{
		this.name = (String) src.get(NAME);
		this.id = src.getId();
		this.startTime = ((Timestamp) src.get(START_TIME)).toDate();
		this.endTime = ((Timestamp) src.get(END_TIME)).toDate();
		this.note = (String) src.get(NOTE);
		this.attendance = (String) src.get(ATTENDANCE);
		this.employees = (ArrayList<DocumentReference>) src.get(EMPLOYEES);
		this.checkedIn = (ArrayList<DocumentReference>) src.get(CHECKEDIN);
		this.lock = (int) (long) src.get(LOCK);
	}

	// DATABASE LOGIC
	public static Task<DocumentSnapshot> getShiftByKey(String key)
	{
		return Shift.getShiftReferenceByKey(key).get();
	}

	public static DocumentReference getShiftReferenceByKey(String key)
	{
		return db.collection(COLLECTION).document(key);
	}

	public static Task<QuerySnapshot> getShifts()
	{
		return db.collection(COLLECTION).get();
	}

	private Task<Void> update(String field, final Object datum)
	{
		Map<String, Object> data = new HashMap<>();
		data.put(field, datum);
		return db.collection(COLLECTION).document(this.id).update(data);
	}

	public static Task<Void> delete(String id)
	{
		return db.collection(COLLECTION).document(id).delete();
	}

	public static Task<DocumentSnapshot> swapEmployees(DocumentReference shiftRef, DocumentReference empFromRef, DocumentReference empToRef)
	{
		return shiftRef.get().addOnCompleteListener((Task<DocumentSnapshot> t) -> {
			if (t.isSuccessful() && t.getResult() != null)
			{
				Shift s = new Shift(t.getResult());
				// add the to employee and remove the from employee
				s.removeEmployee(empFromRef).addOnCompleteListener(
						(Task<Void> task) -> s.addEmployee(empToRef));
			}
		});
	}

	public static Task<QuerySnapshot> getExpiredShiftsContainingEmployee(DocumentReference employee)
	{
		return db.collection(COLLECTION).whereArrayContains(EMPLOYEES, employee)
				.whereLessThan(END_TIME, Timestamp.now()).get();
	}

	public Task<DocumentReference> create()
	{
		HashMap<String, Object> h = new HashMap<>();
		h.put(NAME, this.name);
		h.put(START_TIME, this.startTime);
		h.put(END_TIME, this.endTime);
		h.put(NOTE, this.note);
		h.put(ATTENDANCE, this.attendance);
		h.put(EMPLOYEES, this.employees);
		h.put(CHECKEDIN, this.checkedIn);
		h.put(LOCK, this.lock);

		return db.collection(COLLECTION).add(h)
				.addOnCompleteListener(t -> {
					if (t.isSuccessful() && t.getResult() != null)
						this.id = t.getResult().getId();
				});
	}
}
