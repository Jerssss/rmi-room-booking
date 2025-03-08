package shared;

public class Student {
    private String id;
    private String name;
    private String password;
    private String courseYear;

    public Student(String id, String name, String password, String courseYear) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.courseYear = courseYear;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCourseYear() {
        return courseYear;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", courseYear='" + courseYear + '\'' +
                '}';
    }
}
