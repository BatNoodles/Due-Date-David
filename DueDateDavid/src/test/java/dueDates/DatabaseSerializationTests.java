package dueDates;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DatabaseSerializationTests {

    @Test
    public void testExceptionWhenFileNotFound(){
        Exception exception = assertThrows(IOException.class, ()-> Database.load("NONEXISTENTFILE"));
        assertTrue(exception.getMessage().contains("cannot find the file specified"));
    }
    @Test
    public void testSerializeEmptyDatabase() throws IOException {
        Database database = new Database();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        database.serialize(outputStream);

        String output = outputStream.toString();

        assertEquals("{\"dueDates\":[],\"courses\":[]}", output);
    }
    @Test
    public void testSerializeSingleEmptyCourse() throws IOException{
        Database database = new Database();
        database.addCourse("TEST_COURSE");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        database.serialize(outputStream);

        String output = outputStream.toString();

        assertEquals("{\"dueDates\":[],\"courses\":[{\"name\":\"TEST_COURSE\",\"members\":[]}]}", output);
    }

}
