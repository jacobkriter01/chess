package service;
import dataaccess.DataAccess;
import datamodel.UserData;
import datamodel.RegisterResponse;
import datamodel.AuthTokenData;
import exceptions.ServiceException;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.ServerException;
import java.util.UUID;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public RegisterResponse register(UserData user) throws ServiceException {
        if(user == null || user.username() == null || user.password() == null){
            throw new BadRequestException();
        }

        var extinguisher = dataAccess.getUser(user.username());
        if(extinguisher != null){
            throw new AlreadyTakenException();
        }

        dataAccess.addUser(user);

        var token = new AuthTokenData(UUID.randomUUID().toString(), user.username());
        dataAccess.addAuthToken(token);

        return new RegisterResponse(user.username(), token.authToken());
    }

    public AuthTokenData login(UserData user) throws ServiceException {
        var existingUser = dataAccess.getUser(user.username());
        if(existingUser == null || !existingUser.password().equals(user.password())){
            throw new UnauthorizedException();
        }

        var token = new AuthTokenData(UUID.randomUUID().toString(), user.username());
        dataAccess.addAuthToken(token);

        return token;
    }

    public void logout(String token) throws ServiceException {
        var auth = dataAccess.getAuthToken(token);
        if (auth == null){
            throw new UnauthorizedException();
        }
        dataAccess.removeAuthToken(token);
    }
}
