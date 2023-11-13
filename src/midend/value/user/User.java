package midend.value.user;

import midend.value.Value;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        users.add(user);
    }
}
