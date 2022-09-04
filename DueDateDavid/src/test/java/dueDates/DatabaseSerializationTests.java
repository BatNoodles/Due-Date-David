package dueDates;


import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class DatabaseSerializationTests {

    @Test
    public void testExceptionWhenFileNotFound(){
        Exception exception = assertThrows(IOException.class, ()-> Database.load("NONEXISTENTFILE"));
        assertTrue(exception.getMessage().contains("cannot find the file specified"));

    }

}
