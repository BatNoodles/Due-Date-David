package dueDates;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database.
 */
public class Database {

    private static final Logger logger = Logger.getLogger(Database.class.getName());

    private final List<DueDate> dueDates;
    private final List<Course> courses;

    private static final String FILENAME = "resources/database.json";

    private static final Database DATABASE = new Database();


    public static Database getInstance(){
        return DATABASE;
    }

    protected Database(){
        dueDates = new ArrayList<>();
        courses = new ArrayList<>();
    }




    public List<DueDate> getDueDates(){return Collections.unmodifiableList(dueDates);}

    public List<Course> getCourses(){return Collections.unmodifiableList(courses);}

    /**
     * Sets the instance to the database saved in FILENAME.
     */
    public static void loadInstance(){
        Database temp = loadOrNew(FILENAME);
        DATABASE.set(temp);
    }

    /**
     * Sets the dueDates and courses of this database to that of the other database
     * @param database Database to be copied from.
     */
    private void set(Database database){
        dueDates.clear();
        dueDates.addAll(database.dueDates);
        courses.clear();
        courses.addAll(database.courses);
    }

    /**
     * Gets all due dates based on a predicate.
     * @param predicate - Due date predicate
     * @return List of the courses matching the predicate
     */
    public List<DueDate> filterDueDates(Predicate<? super DueDate> predicate){return dueDates.stream().filter(predicate).toList();}

    public void addCourse(String courseName){
        courses.add(new Course(courseName));
    }

    public void addDueDate(DueDate dueDate){dueDates.add(dueDate);}

    public Optional<Course> getCourse(String courseName){return courses.stream().filter(s->s.getName().equalsIgnoreCase(courseName)).findFirst();}

    /**
     * Gets the course with the specified name, creates it first if it does not exist.
     * @param courseName - Name of the course
     * @return course with the specified name
     */
    public Course getOrAddCourse(String courseName){
        Optional<Course> course = getCourse(courseName);
        if (course.isEmpty()){
            Course s = new Course(courseName);
            courses.add(s);
            return s;
        }
        return course.get();
    }

    /**
     * Adds a discord user ID to a course; i.e. Allows a user to join a course so that they can be notified of any due dates in that class.
     * If the course does not exist it is created.
     * @param courseName - Name of the course, case-insensitive
     * @param userId - Discord user id
     */
    public void joinCourse(String courseName, Long userId){
        getCourse(courseName).ifPresentOrElse(
                c->c.addUserId(userId),
                ()-> {
                    Course s = new Course(courseName);
                    s.addUserId(userId);
                    courses.add(s);
                }
        );
    }

    public boolean userInCourse(String course, Long userId){
        return getCourse(course).map(value -> value.containsUser(userId)).orElse(false);
    }

    /**
     * Saves the Database to a json file.
     * @param filename - Filename to be saved to.
     * @throws IOException - Error was encountered saving the file.
     */
    public void save(String filename) throws IOException {
        serialize(new FileOutputStream(filename));
    }

    /**
     * Attempts to load a Database from a json file. If there is an error a new, empty database is created.
     * @param filename - Filename of the json file.
     * @return Database - the new Database loaded.
     */
    public static Database loadOrNew(String filename){
        try {
            return load(filename);
        } catch (IOException e) {
            logger.log(Level.FINE, e + ": Creating empty database");
            return new Database();
        }
    }

    /**
     * Loads a database from a json file.
     * @param filename - Name of the json file.
     * @return Database - the database loaded.
     * @throws IOException - Error is encountered opening the file.
     */
    public static Database load(String filename) throws IOException{
        return deserialize(new FileInputStream(filename));
    }

    /**
     * Deserializes a database from an InputStream.
     * @param stream InputStream of database data.
     * @return Database object.
     * @throws IOException Exception thrown when reading from the stream.
     */
    public static Database deserialize(InputStream stream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("DueDateDeserializer", new Version(1,0,0,null,null,null));
        module.addDeserializer(DueDate.class, new DueDate.DueDateDeserializer());

        objectMapper.registerModule(module);

        return objectMapper.readValue(stream, Database.class);
    }

    /**
     * Serializes the database to an output stream.
     * @param stream Stream for the serialized Database to be written to.
     * @throws IOException Exception thrown when writing to the stream.
     */
    public void serialize(OutputStream stream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("DueDateSerializer", new Version(1,0,0,null,null,null));
        module.addSerializer(DueDate.class, new DueDate.DueDateSerializer());

        objectMapper.registerModule(module);

        objectMapper.writeValue(stream, this);
    }


}
