package manager.attendance.aclass.app.v.classattendancemanager;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import adapter.SubjectAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import model.Subject;

public class MainActivity extends AppCompatActivity {

    private static final String MY_PREFS_NAME = "Preferences";
    private static final String HAS_ADDED_MINIMUM_ATTENDANCE = "HasAddedMinimumAttendance";
    private static final String MINIMUM_ATTENDANCE = "MinimumAttendance";

    SharedPreferences prefs;

    private FirebaseAnalytics mFirebaseAnalytics;

    Realm realm;

    ArrayList<Subject> listOfSubjects = new ArrayList<>();

    private Toolbar mToolbar;
    private RecyclerView mRecList;
    private FloatingActionButton mAddSubjects;
    private SubjectAdapter mAdapter;

    int minimumAttendance = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getExtras() == null) {
            // Toast.makeText(this, "Error in intents! (Contact Developer)", Toast.LENGTH_SHORT).show();
        } else {

            Bundle extras = getIntent().getExtras();
            String url = extras.getString("url");
            // Toast.makeText(this, "Url : " + url, Toast.LENGTH_SHORT).show();

            if(url != null) {

                if(!url.isEmpty()) {

                    try{

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    } catch (ActivityNotFoundException e) {

                    }


                }

            }

        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecList = (RecyclerView) findViewById(R.id.subjects_list_recycler_view);
        mAddSubjects = (FloatingActionButton) findViewById(R.id.fab_add_subject);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(R.string.app_name);
            getSupportActionBar().setTitle(R.string.app_name);
        }

        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean hasAddedMinimumAttendance = prefs.getBoolean(HAS_ADDED_MINIMUM_ATTENDANCE, false);
        if (!hasAddedMinimumAttendance) {

            // Go to AddMinimumAttendance Screen

            Intent intent = new Intent(MainActivity.this, AddMinimumAttendance.class);
            startActivity(intent);

        } else {

            minimumAttendance = prefs.getInt(MINIMUM_ATTENDANCE, 0);
            // Toast.makeText(this, "Minimum Attendacne is " + minimumAttendance + "%", Toast.LENGTH_SHORT).show();
        }


        realm = Realm.getDefaultInstance();


        // Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Home screen - 1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home screen - 2");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Main Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        // linking widgets to XML

        mAddSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show a Open a Popup with Edit Text and add a subject. Save Subject to Realm with default entries!

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_add_subject);

                // set the custom dialog components - text, image and button
                final EditText mSubjectName = (EditText) dialog.findViewById(R.id.subject_name);
                Button mAddSubject = (Button) dialog.findViewById(R.id.add_subject);

                mAddSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String subjectName = mSubjectName.getText().toString();

                        if(subjectName.isEmpty()) {
                            Toast.makeText(MainActivity.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
                        } else {

                            // Save in realm

                            realm.beginTransaction();

                            Subject newSubject = realm.createObject(Subject.class);
                            int nextKey = getNextKey();
                            newSubject.setId(nextKey);
                            newSubject.setName(capitalizeFirstLetter(subjectName));
                            newSubject.setTotalLectures(0);
                            newSubject.setTotalLecturesAttended(0);
                            newSubject.setTotalLecturesSkipped(0);

                            realm.commitTransaction();

                            dialog.dismiss();

                            getSubjectsList();

                        }


                    }
                });

                dialog.setCancelable(true);
                dialog.show();

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecList.setLayoutManager(layoutManager);
        mRecList.setHasFixedSize(true);

        mRecList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mAddSubjects.hide();
                else if (dy < 0)
                    mAddSubjects.show();
            }
        });

        getSubjectsList();


    }

    private void getSubjectsList() {

        listOfSubjects.clear();
        RealmResults<Subject> subjectResults =
                realm.where(Subject.class).findAll();

        // Toast.makeText(this, "Total Subjects : " + subjectResults.size(), Toast.LENGTH_SHORT).show();

        for (Subject s : subjectResults) {
            final Subject tempSubject = new Subject();
            tempSubject.setId(s.getId());
            tempSubject.setName(s.getName());
            tempSubject.setTotalLecturesSkipped(s.getTotalLecturesSkipped());
            tempSubject.setTotalLecturesAttended(s.getTotalLecturesAttended());
            tempSubject.setTotalLectures(s.getTotalLectures());
            listOfSubjects.add(tempSubject);
        }

        mAdapter = new SubjectAdapter(MainActivity.this, listOfSubjects, minimumAttendance);
        mRecList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    public int getNextKey()
    {
        try {
            return realm.where(Subject.class).max("id").intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e)
        { return 0; }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


}
