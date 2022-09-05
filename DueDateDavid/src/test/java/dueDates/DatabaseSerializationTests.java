package dueDates;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class DatabaseSerializationTests {

    private Database withCourses(String... courseNames){
        Database db = new Database();
        for (String course : courseNames) db.addCourse(course);
        return db;
    }

    private void assertSaveEquals(Database database, String expected) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        database.serialize(stream);
        assertEquals(expected, stream.toString());

    }

    @Test
    public void testExceptionWhenFileNotFound(){
        Exception exception = assertThrows(IOException.class, ()-> Database.load("NONEXISTENTFILE"));
        assertTrue(exception.getMessage().contains("cannot find the file specified"));
    }
    @Test
    public void testSerializeEmptyDatabase() throws IOException {
        Database database = new Database();
        assertSaveEquals(database, "{\"dueDates\":[],\"courses\":[]}");
    }
    @Test
    public void testSerializeSingleEmptyCourse() throws IOException{
        Database database = withCourses("TEST_COURSE");
        assertSaveEquals(database, "{\"dueDates\":[],\"courses\":[{\"name\":\"TEST_COURSE\",\"members\":[]}]}");
    }


    @Test
    public void testSerializeWithCourse() throws IOException {
        Database database = withCourses("ONE_MEMBER");
        database.joinCourse("ONE_MEMBER", 1L);
        assertSaveEquals(database,"{\"dueDates\":[],\"courses\":[{\"name\":\"ONE_MEMBER\",\"members\":[1]}]}" );
    }

}
