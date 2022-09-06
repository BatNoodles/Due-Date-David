package dueDates;


import org.junit.Test;

import java.io.ByteArrayInputStream;
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

    private void assertLoadEquals(String json, Database expected) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes());
        Database deserialized = Database.deserialize(input);
        assertEquals(expected, deserialized);
    }

    @Test
    public void testExceptionWhenFileNotFound(){
        Exception exception = assertThrows(IOException.class, ()-> Database.load("NONEXISTENTFILE"));
        assertTrue(exception.getMessage().contains("cannot find the file specified"));
    }
    @Test
    public void testSerializeEmptyDatabase() throws IOException {
        Database database = new Database();
        assertSaveEquals(database, "{\"dueDates\":[],\"courses\":[],\"channel\":null}");
    }
    @Test
    public void testSerializeSingleEmptyCourse() throws IOException{
        Database database = withCourses("TEST_COURSE");
        assertSaveEquals(database, "{\"dueDates\":[],\"courses\":[{\"name\":\"TEST_COURSE\",\"members\":[]}],\"channel\":null}");
    }
    @Test
    public void testSerializeWithCourse() throws IOException {
        Database database = withCourses("ONE_MEMBER");
        database.joinCourse("ONE_MEMBER", 1L);
        assertSaveEquals(database,"{\"dueDates\":[],\"courses\":[{\"name\":\"ONE_MEMBER\",\"members\":[1]}],\"channel\":null}" );
    }
    @Test
    public void testSerializeWithCourses() throws IOException {
        Database database = withCourses("ONE_MEMBER", "TWO_MEMBERS");
        database.joinCourse("ONE_MEMBER", 1L);
        database.joinCourse("TWO_MEMBERS", 2L);
        database.joinCourse("TWO_MEMBERS", 3L);
        assertSaveEquals(database,"{\"dueDates\":[],\"courses\":[{\"name\":\"ONE_MEMBER\",\"members\":[1]},{\"name\":\"TWO_MEMBERS\",\"members\":[2,3]}],\"channel\":null}" );
    }
    @Test
    public void testSerializeWithDueDates() throws IOException {
        Database database = new Database();
        DueDate date = new DueDate("test", "TEST", "01/01", "12:00", database);
        database.addDueDate(date);
        assertSaveEquals(database,"{\"dueDates\":[{\"name\":\"test\",\"date\":\"" + date.getTime()+ "\",\"course\":\"TEST\"}],\"courses\":[{\"name\":\"TEST\",\"members\":[]}],\"channel\":null}" );
    }
    @Test
    public void testSerializeFullDatabase() throws IOException {
        Database database = new Database();
        DueDate date = new DueDate("test", "TEST", "01/01", "12:00", database);
        database.joinCourse("ONE_MEMBER", 1L);
        database.joinCourse("TWO_MEMBERS", 2L);
        database.joinCourse("TWO_MEMBERS", 3L);
        database.addDueDate(date);

        assertSaveEquals(database,"{\"dueDates\":[{\"name\":\"test\",\"date\":\"" + date.getTime()+ "\",\"course\":\"TEST\"}],\"courses\":[{\"name\":\"TEST\",\"members\":[]},{\"name\":\"ONE_MEMBER\",\"members\":[1]},{\"name\":\"TWO_MEMBERS\",\"members\":[2,3]}],\"channel\":null}" );
    }


    @Test
    public void testDeserializeDatabase() throws IOException {
        Database database = new Database();
        DueDate date = new DueDate("test", "TEST", "01/01", "12:00", database);
        database.joinCourse("ONE_MEMBER", 1L);
        database.joinCourse("TWO_MEMBERS", 2L);
        database.joinCourse("TWO_MEMBERS", 3L);
        database.addDueDate(date);

        assertLoadEquals("{\"dueDates\":[{\"name\":\"test\",\"date\":\"" + date.getTime()+ "\",\"course\":\"TEST\"}],\"courses\":[{\"name\":\"TEST\",\"members\":[]},{\"name\":\"ONE_MEMBER\",\"members\":[1]},{\"name\":\"TWO_MEMBERS\",\"members\":[2,3]}],\"channel\":null}", database);

    }

    @Test
    public void testSerializedDatabaseIsEqual() throws IOException {
        Database database = new Database();
        DueDate date = new DueDate("test", "TEST", "01/01", "12:00", database);
        database.joinCourse("ONE_MEMBER", 1L);
        database.joinCourse("TWO_MEMBERS", 2L);
        database.joinCourse("TWO_MEMBERS", 3L);
        database.addDueDate(date);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        database.serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        assertEquals(database, Database.deserialize(inputStream));


    }

}
