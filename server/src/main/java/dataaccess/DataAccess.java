package dataaccess;

import datamodel.AuthTokenData;
import datamodel.UserData;

public interface DataAccess {
    void clear();
    void addUser(UserData user);
    UserData getUser(String username);

    void addAuthToken(AuthTokenData authToken);
    AuthTokenData getAuthToken(String token);
    void removeAuthToken(String token);
}
