package xyz.cyklo.api;

/**
 * Created by Aashish Nehete on 10-Jan-16.
 */
public class Details {
    private String name;
    private String college;
    private String number;
    private String email;

    public Details(String name, String college, String number, String email) {
        this.name = name;
        this.college = college;
        this.number = number;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
