package manager.attendance.aclass.app.v.classattendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import model.Subject;

/**
 * Activity to edit a particular subject!
 */

public class EditSubject extends AppCompatActivity {

    private EditText mSubjectName, mTotalClasses, mTotalClassesAttended;
    private Toolbar mToolbar;
    private Button mUpdate;

    int subjectId = -1;

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_subject);

        realm = Realm.getDefaultInstance();
        
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mSubjectName = (EditText) findViewById(R.id.subject_name);
        mTotalClasses = (EditText) findViewById(R.id.total_classes);
        mTotalClassesAttended = (EditText) findViewById(R.id.total_classes_attended);

        mUpdate = (Button) findViewById(R.id.update_subject);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.edit_subject);
            getSupportActionBar().setTitle(R.string.edit_subject);
            // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get ID from Intents!


        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {
            subjectId = extras.getInt("id");

            Subject tempSubject = realm.where(Subject.class).equalTo("id", subjectId).findFirst();
            realm.beginTransaction();
            mSubjectName.setText(tempSubject.getName());
            mTotalClasses.setText(tempSubject.getTotalLectures() + "");
            mTotalClassesAttended.setText(tempSubject.getTotalLecturesAttended() + "");
            realm.commitTransaction();

        }

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String subjectName = mSubjectName.getText().toString();
                String totalClasses = mTotalClasses.getText().toString();
                String totalClassesAttended = mTotalClassesAttended.getText().toString();

                if(subjectName.isEmpty() || totalClasses.isEmpty() || totalClassesAttended.isEmpty()) {
                    Toast.makeText(EditSubject.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                } else {

                    int totalClassesNumber = Integer.parseInt(totalClasses);
                    int totalClassesAttendedNumber = Integer.parseInt(totalClassesAttended);

                    if(totalClassesAttendedNumber > totalClassesNumber) {
                        Toast.makeText(EditSubject.this, R.string.invalid_numbers, Toast.LENGTH_SHORT).show();
                    } else {

                        int totalClassesSkippedNumber = totalClassesNumber - totalClassesAttendedNumber;

                        realm.beginTransaction();

                        Subject editSubject = realm.where(Subject.class).equalTo("id", subjectId).findFirst();
                        editSubject.setId(subjectId);
                        editSubject.setName(subjectName);
                        editSubject.setTotalLectures(totalClassesNumber);
                        editSubject.setTotalLecturesSkipped(totalClassesSkippedNumber);
                        editSubject.setTotalLecturesAttended(totalClassesAttendedNumber);

                        realm.commitTransaction();

                        Intent intent = new Intent(EditSubject.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }


                }
            }
        });



    }
}
