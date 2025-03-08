package shared;

public class Admin {
    private String id;
    private String name;
    private String type;
    private String password;
    private String facultyType;

    public Admin(String id, String name, String type, String password, String facultyType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.password = password;
        this.facultyType = facultyType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPassword() {
        return password;
    }

    public String getFacultyType() {
        return facultyType;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", facultyType='" + facultyType + '\'' +
                '}';
    }
}

