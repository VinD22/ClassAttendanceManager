package model;

import io.realm.RealmObject;

/**
 * Model class for each Subject!
 */

public class Subject extends RealmObject {

    private int id;
    private String name;
    private int totalLectures;
    private int totalLecturesAttended;
    private int totalLecturesSkipped;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalLectures() {
        return totalLectures;
    }

    public void setTotalLectures(int totalLectures) {
        this.totalLectures = totalLectures;
    }

    public int getTotalLecturesAttended() {
        return totalLecturesAttended;
    }

    public void setTotalLecturesAttended(int totalLecturesAttended) {
        this.totalLecturesAttended = totalLecturesAttended;
    }

    public int getTotalLecturesSkipped() {
        return totalLecturesSkipped;
    }

    public void setTotalLecturesSkipped(int totalLecturesSkipped) {
        this.totalLecturesSkipped = totalLecturesSkipped;
    }

}
