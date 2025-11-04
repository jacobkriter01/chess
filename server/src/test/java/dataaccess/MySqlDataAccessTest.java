package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {

   private MySqlDataAccess dao;

   @BeforeEach
   public void setUp() {
       dao = new MySqlDataAccess();
       dao.clear();
   }

   @Test
    public void testAddGetUser() {
       var user = new UserData("Jacob", null, "pwd");
       dao.addUser(user);

       var result = dao.getUser("Jacob");
       assertNotNull(result);
       assertEquals("Jacob", result.username());

       assertNotEquals("pwd", result.password());
       assertTrue(BCrypt.checkpw("pwd", result.password()));
   }

}