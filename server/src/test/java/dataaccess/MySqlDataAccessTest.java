package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

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

}