package adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import manager.attendance.aclass.app.v.classattendancemanager.EditSubject;
import manager.attendance.aclass.app.v.classattendancemanager.R;
import model.Subject;

/**
 * Adapter class to manage list of subjects!
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.RecyclerViewHolder> {

    private List<Subject> data;
    private Context mContext;
    Realm realm;
    int minimumAttendance;
    NumberFormat numberFormat;

    public SubjectAdapter(Context context, ArrayList<Subject> data, int minimumAttendance) {
        this.mContext = context;
        this.data = data;
        this.minimumAttendance = minimumAttendance;
        realm = Realm.getDefaultInstance();
        // For percentage!
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        setHasStableIds(true);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.subject_list_item, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {

        final Subject tempSubject = data.get(viewHolder.getAdapterPosition());

        viewHolder.mSubjectName.setText(capitalizeFirstLetter(tempSubject.getName()));

        int totalLecturesAttended = tempSubject.getTotalLecturesAttended();
        int totalLectures = tempSubject.getTotalLectures();

        if(totalLectures == 0) {
            viewHolder.mSubjectAttendancePercentage.setText("0%");
        } else {
            double percentage = ((double) totalLecturesAttended / (double) totalLectures) * 100;
            viewHolder.mSubjectAttendancePercentage.setText(numberFormat.format(percentage) + "%");
        }

        viewHolder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to Edit Screen!
                Intent intent = new Intent(mContext, EditSubject.class);
                intent.putExtra("id", tempSubject.getId());
                mContext.startActivity(intent);


            }
        });

        viewHolder.mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show a popup showing details about this subject.

                String message1 = mContext.getString(R.string.total_classes) + "  -  " + tempSubject.getTotalLectures();
                String message2 = mContext.getString(R.string.classes_attended) + "  -  " + tempSubject.getTotalLecturesAttended();
                String message3 = mContext.getString(R.string.classes_skipped) + "  -  " + tempSubject.getTotalLecturesSkipped();

                String finalMessage = message1 + " \n" + message2 + " \n" + message3;

                new AlertDialog.Builder(mContext, R.style.MyDialogTheme)
                        .setTitle(capitalizeFirstLetter(tempSubject.getName()))
                        .setMessage(finalMessage)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

        viewHolder.mAttendLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Update on Screen!

                int totalLectures = tempSubject.getTotalLectures();
                int lecturesAttended = tempSubject.getTotalLecturesAttended();

                totalLectures++;
                lecturesAttended++;

                double percentage = ((double) lecturesAttended / (double) totalLectures) * 100;
                viewHolder.mSubjectAttendancePercentage.setText(numberFormat.format(percentage) + "%");

                tempSubject.setTotalLectures(totalLectures);
                tempSubject.setTotalLecturesAttended(lecturesAttended);

                // Update on Realm

                Subject toEdit = realm.where(Subject.class).equalTo("id", tempSubject.getId()).findFirst();
                realm.beginTransaction();
                toEdit.setTotalLectures(totalLectures);
                toEdit.setTotalLecturesAttended(lecturesAttended);
                realm.commitTransaction();

            }
        });

        viewHolder.mSkipLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Update on Screen!

                int totalLectures = tempSubject.getTotalLectures();
                int lecturesSkipped = tempSubject.getTotalLecturesSkipped();
                int lecturesAttended = tempSubject.getTotalLecturesAttended();

                totalLectures++;
                lecturesSkipped++;

                double percentage = ((double) lecturesAttended / (double) totalLectures) * 100;
                viewHolder.mSubjectAttendancePercentage.setText(numberFormat.format(percentage) + "%");

                tempSubject.setTotalLectures(totalLectures);
                tempSubject.setTotalLecturesSkipped(lecturesSkipped);

                // Update on Realm


                Subject toEdit = realm.where(Subject.class).equalTo("id", tempSubject.getId()).findFirst();
                realm.beginTransaction();
                toEdit.setTotalLectures(totalLectures);
                toEdit.setTotalLecturesSkipped(lecturesSkipped);
                realm.commitTransaction();


            }
        });


    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout placeHolder;
        public LinearLayout mLinearLayout;
        protected TextView mSubjectName, mSubjectAttendancePercentage;
        protected ImageButton mEdit, mInfo;
        protected Button mAttendLecture, mSkipLecture;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.lin);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            mSubjectName = (TextView) itemView.findViewById(R.id.subject_name);
            mSubjectAttendancePercentage = (TextView) itemView.findViewById(R.id.subject_attendance_percentage);
            mEdit = (ImageButton) itemView.findViewById(R.id.edit);
            mInfo = (ImageButton) itemView.findViewById(R.id.info);
            mAttendLecture = (Button) itemView.findViewById(R.id.attend_lecture);
            mSkipLecture = (Button) itemView.findViewById(R.id.skip_lecture);

        }

    }


}
