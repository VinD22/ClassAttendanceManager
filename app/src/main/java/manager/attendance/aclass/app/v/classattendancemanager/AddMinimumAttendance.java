package manager.attendance.aclass.app.v.classattendancemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity to add / update minimum attendance!
 */

public class AddMinimumAttendance extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String HAS_ADDED_MINIMUM_ATTENDANCE = "HasAddedMinimumAttendance";
    private static final String MINIMUM_ATTENDANCE = "MinimumAttendance";

    SharedPreferences prefs;

    private Button mUpdateMinimumAttendance;
    private EditText mAttendance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_minimum_attendance);

        mUpdateMinimumAttendance = (Button) findViewById(R.id.update_minimum_attendance);
        mAttendance = (EditText) findViewById(R.id.attendance);

        // Check if already attendance is saved or not, if saved then fetch that value and set text of mAttendance to that.
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean hasAddedMinimumAttendance = prefs.getBoolean(HAS_ADDED_MINIMUM_ATTENDANCE, false);
        if(hasAddedMinimumAttendance) {
            int minimumAttendance = prefs.getInt(MINIMUM_ATTENDANCE, 0);
            mAttendance.setText(minimumAttendance + "");
        }


        mUpdateMinimumAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String attendance = mAttendance.getText().toString();

                if(attendance.isEmpty()) {
                    Toast.makeText(AddMinimumAttendance.this, R.string.no_value, Toast.LENGTH_SHORT).show();
                } else {

                    // Save / Update Minimum Attendance!
                    int min_attendance = Integer.parseInt(attendance);

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(HAS_ADDED_MINIMUM_ATTENDANCE, true);
                    editor.putInt(MINIMUM_ATTENDANCE, min_attendance);
                    editor.commit();

                    Intent intent = new Intent(AddMinimumAttendance.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }

            }
        });

    }
}
