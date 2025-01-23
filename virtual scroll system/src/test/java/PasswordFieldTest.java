import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordFieldTest {

    @Test
    public void testReadPassword() {
        // Create an input stream with the desired password
        String inputPassword = "mySecretPassword123";
        InputStream inputStream = new ByteArrayInputStream(inputPassword.getBytes());
        System.setIn(inputStream);

        // Set the prompt for the password field
        String prompt = "Enter your password: ";

        // Redirect System.out to capture the output
        InputStream originalSystemIn = System.in;
        System.setIn(inputStream);

        // Invoke the readPassword method
        String result = PasswordField.readPassword(prompt);

        // Restore original System.in
        System.setIn(originalSystemIn);

        // Verify the result
        assertEquals(inputPassword, result, "Passwords should match");
    }
}
