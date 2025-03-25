package shared;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Admin implements Serializable { // Add Serializable
    private static final long serialVersionUID = 1L; // Add serialVersionUID

    @Expose
    @SerializedName("Admin_ID")
    private String id;

    @Expose
    @SerializedName("Name")
    private String name;

    @Expose
    @SerializedName("Type")
    private String type;

    @Expose
    @SerializedName("Password")
    private String password;

    @Expose
    @SerializedName("FacultyType")
    private String facultyType;

    public Admin(String id, String name, String type, String password, String facultyType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.password = password;
        this.facultyType = facultyType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getPassword() { return password; }
    public String getFacultyType() { return facultyType; }

    @Override
    public String toString() {
        return "Admin{" +
                "Admin_ID='" + id + '\'' +
                ", Name='" + name + '\'' +
                ", Type='" + type + '\'' +
                ", Password='" + password + '\'' +
                ", FacultyType='" + facultyType + '\'' +
                '}';
    }
}
