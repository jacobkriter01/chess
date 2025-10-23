package service;
import dataaccess.DataAccess;
import datamodel.UserData;
import datamodel.RegisterResponse;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public RegisterResponse register(UserData user) throws Exception {
        if(user == null || user.username() == null || user.password() == null){
            throw new IllegalArgumentException("Missing Required Fields");
        }

        var extinguisher = dataAccess.getUser(user.username());
        if(extinguisher != null){
            throw new Exception("User already exists");
        }

        dataAccess.addUser(user);
        return new RegisterResponse(user.username(), "zyz");
    }

    public RegisterResponse login(UserData user) throws Exception {
        var existingUser = dataAccess.getUser(user.username());
        if(existingUser == null || !existingUser.password().equals(user.password())){
            throw new Exception("unauthorized");
        }
        return new RegisterResponse(user.username(), "zyz");
    }
}
