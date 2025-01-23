import org.junit.jupiter.api.Test;

public class EraserThreadTest {
    @Test
    public void testEraserThread() {
        // Create an EraserThread instance and pass a prompt
        EraserThread eraserThread = new EraserThread("Enter a password: ");

        // Create a new thread for the EraserThread
        Thread thread = new Thread(eraserThread);

        // Start the EraserThread
        thread.start();

        // Sleep for a few seconds to simulate user input
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the EraserThread
        eraserThread.stopMasking();

        // Wait for the thread to finish
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
