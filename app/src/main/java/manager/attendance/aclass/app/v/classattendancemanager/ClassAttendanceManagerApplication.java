package manager.attendance.aclass.app.v.classattendancemanager;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application class for setting up Realm!
 */

public class ClassAttendanceManagerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        RealmConfiguration config = new
//                RealmConfiguration.Builder(getApplicationContext()).build();
//        Realm.setDefaultConfiguration(config);

//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .schemaVersion(0) // Must be bumped when the schema changes
//                .migration(new MyMigration()) // Migration to run instead of throwing an exception
//                .build();

        Realm.init(this);

        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("class_attendance_database.realm").schemaVersion(0).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        // https://medium.com/@budioktaviyans/android-realm-migration-schema-4fcef6c61e82#.7jyspkp29

//        // After Update
//        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("database.realm").schemaVersion(1).migration(new MyMigration()).build();
//        Realm.setDefaultConfiguration(configuration);
//        Realm.getInstance(configuration);

    }



    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }


}
