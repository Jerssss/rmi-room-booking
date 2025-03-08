package shared;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {
    @Expose
    @SerializedName("Student_ID")
    private String id;

    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("Password")
    private String password;

    @Expose
    @SerializedName("CourseYear")
    private String courseYear;

    public Student(String id, String name, String password, String courseYear) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.courseYear = courseYear;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getCourseYear() { return courseYear; }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", courseYear='" + courseYear + '\'' +
                '}';
    }
}
