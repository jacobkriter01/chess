package dataaccess;

import datamodel.AuthTokenData;
import datamodel.GameData;
import datamodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {

   private static MySqlDataAccess dao;

   @BeforeEach
   void clearTables(){
       dao = new MySqlDataAccess();
       dao.clear();
   }

   @Test
   @DisplayName("addUser positive")
    public void testAddGetUser() {
       var user = new UserData("Jacob", null, "pwd");
       dao.addUser(user);

       var result = dao.getUser("Jacob");
       assertNotNull(result);
       assertEquals("Jacob", result.username());

       assertNotEquals("pwd", result.password());
       assertTrue(BCrypt.checkpw("pwd", result.password()));
   }

   @Test
   @DisplayName("addUser negative")
   public void testAddUserDuplicate() {
       var user = new UserData("Jacob", null, "pwd");
       dao.addUser(user);
       var dup = new UserData("Jacob", null, "pwd3");
       assertThrows(RuntimeException.class, () -> dao.addUser(dup));

   }

   @Test
   @DisplayName("getUser does not exist")
   public void testGetUserNegative() {
       var result = dao.getUser("fake");
       assertNull(result);
   }

   @Test
   @DisplayName("addAuthToken positive test")
   public void addAuthTokenPositive() {
       var user = new UserData("user1", null, "pwd");
       dao.addUser(user);
       var token = new AuthTokenData("token123", "user1");

       dao.addAuthToken(token);

       var result = dao.getAuthToken("token123");
       assertNotNull(result);
       assertEquals("user1", result.username());
   }

   @Test
   @DisplayName("addAuthToken duplicate token")
   public void testAddAuthTokenNegative() {
       var user = new UserData("user1", null, "pwd");
       dao.addUser(user);
       var token = new AuthTokenData("token123", "user1");
       dao.addAuthToken(token);

       var dup = new AuthTokenData("token123", "user1");
       assertThrows(RuntimeException.class, () -> dao.addAuthToken(dup));
   }

    @Test
    @DisplayName("getAuthToken token not exist")
    public void testGetAuthTokenNegative() {
        var result = dao.getAuthToken("token123");
        assertNull(result);
    }

    @Test
    @DisplayName("remove authToken positive")
    public void removeAuthTokenPositive() {
       var user = new UserData("user1", null, "pwd");
       dao.addUser(user);
       var token = new AuthTokenData("token123", "user1");
       dao.addAuthToken(token);

       dao.removeAuthToken("token123");
       assertNull(dao.getAuthToken("token123"));
    }

    @Test
    @DisplayName("remove authToken not exist")
    public void removeAuthTokenNegative() {
       assertDoesNotThrow(() -> dao.removeAuthToken("token123"));
    }

    @Test
    @DisplayName("addGame positive")
    public void addGamePositive() {
        var game = dao.addGame("Chess");
        assertNotNull(game);
        assertEquals("Chess",game.getGameName());
    }

    @Test
    @DisplayName("getGame negative")
    public void getGameNegative() {
       var result = dao.getGame(123);
       assertNull(result);
    }

    @Test
    @DisplayName("joinGame positive")
    public void joinGamePositive() {
       dao.addUser(new UserData("user1", null, "pwd"));
       var game = dao.addGame("Chess");

       dao.joinGame(game.getGameID(), "user1", "WHITE");

       var update = dao.getGame(game.getGameID());
       assertEquals("user1", update.getWhiteUsername());
    }

    @Test
    @DisplayName("joinGame not exist")
    public void joinGameNegative() {
       assertThrows(RuntimeException.class, () -> dao.joinGame(123, "user1", "WHITE"));
    }

    @Test
    @DisplayName("getAllGames positive")
    public void getAllGamesPositive() {
       var g1 = dao.addGame("Test1");
       var g2 = dao.addGame("Test2");

       Collection<GameData> allGames = dao.getAllGames();
       assertEquals(2, allGames.size());
    }


}