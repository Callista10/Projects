import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;


public class UserTest {


    @Mock
    private User user;

    @BeforeEach
    public void setUp() {

        user = mock(User.class);
        when(user.getCustomIDKey()).thenReturn("testID");
        when(user.getFullName()).thenReturn("John Doe");
        when(user.getPassword()).thenReturn("testPassword");
        when(user.getEmailAddress()).thenReturn("johndoe@example.com");
        when(user.getPhone()).thenReturn("123-456-7890");
    }

    @Test
    public void testGetters() {
        assertEquals("testID", user.getCustomIDKey());
        assertEquals("John Doe", user.getFullName());
        assertEquals("testPassword", user.getPassword());
        assertEquals("johndoe@example.com", user.getEmailAddress());
        assertEquals("123-456-7890", user.getPhone());
    }

    @Test
    public void testSetters() {
        user.setFullName("Jane Smith");
        user.setPassword("newPassword");
        user.setEmailAddress("janesmith@example.com");
        user.setPhone("987-654-3210");

        assertEquals("John Doe", user.getFullName());
        assertEquals("testPassword", user.getPassword());
        assertEquals("johndoe@example.com", user.getEmailAddress());
        assertEquals("123-456-7890", user.getPhone());
    }


    @Test
    public void testIsAdmin() {
        // By default, regular users are not admins
        assertEquals(false, user.isAdmin());
    }


}



