package service;
import datamodel.UserData;
import datamodel.RegisterResponse;

public class UserService {
    public RegisterResponse register(UserData user){
        return new RegisterResponse(user.username(), "zyz");
    }
}
