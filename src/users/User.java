package users;

public abstract class User {

    private String userID;
    private String name;
    private String username;
    private String password;

    public User(String userID, String name, String username, String password) {

        this.userID = userID;
        this.name = name;
        this.username = username;
        this.password = password;

    }

    public abstract String getRole();

    public String getUserID() {

        return userID;

    }

    public String getName() {

        return name;

    }

    public String getUsername() {

        return username;

    }

    public void setUsername(String username) {

        this.username = username;

    }

    public String getPassword() {

        return password;

    }

    public void setPassword(String password) {

        this.password = password;

    }

    public boolean checkPassword(String password) {

        return this.password.equals(password);

    }

    public boolean checkLogin(String username, String password) {

        return this.username.equals(username) && checkPassword(password);

    }

    public void displayDetails() {

        System.out.println("User ID: " + userID);
        System.out.println("Name: " + name);
        System.out.println("Role: " + getRole());

    }

}
