package dueDates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import reactor.core.publisher.*;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton database.
 */
public class Database {

    private static class DueDateFluxSink implements Consumer<FluxSink<DueDate>>{
        private FluxSink<DueDate> fluxSink;

        @Override
        public void accept(FluxSink<DueDate> fluxSink){
            this.fluxSink = fluxSink;}

        public void publishDueDate(DueDate date, Duration delay){
            if (fluxSink == null) return;
            Mono.just(date).delayElement(delay).subscribe(d -> this.fluxSink.next(d));
        }

    }
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    private final List<DueDate> dueDates;
    private final List<Course> courses;



    @JsonIgnore
    private final Flux<DueDate> reminderFlux;
    @JsonIgnore
    private final DueDateFluxSink reminderSink;
    @JsonIgnore
    private final Flux<DueDate> removalFlux;
    @JsonIgnore
    private final DueDateFluxSink removalSink;

    /**
     * Id for the channel that reminders should be sent to. This is an optional as the channel may not be set, or may be removed.
     */
    private Long channel;

    private static final String FILENAME = "database.json";

    private static final Database DATABASE = new Database();


    public static Database getInstance(){
        return DATABASE;
    }

    protected Database(){
        dueDates = new ArrayList<>();
        courses = new ArrayList<>();
        channel = null;
        reminderSink = new DueDateFluxSink();
        reminderFlux = Flux.create(reminderSink).share();
        removalSink = new DueDateFluxSink();
        removalFlux = Flux.create(removalSink).share();

    }

    /**
     * Emits all the due dates in the DueDates list to both the reminder sink (one hour before it is due) and the removal sink (the moment it is due)
     */
    private void emitAllDueDates(){
        dueDates.forEach(this::emitDueDate);
    }

    /**
     * Emits a single DueDate
     * @param d - The DueDate to be emitted.
     */
    private void emitDueDate(DueDate d){
        Duration timeToRemind = d.getTimeUntil().minusHours(1);
        Duration timeToRemove = d.getTimeUntil();
        reminderSink.publishDueDate(d, timeToRemind.isNegative() ? Duration.ZERO : timeToRemind);
        removalSink.publishDueDate(d, timeToRemove.isNegative() ? Duration.ZERO : timeToRemove);
    }

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
        channel = database.channel;
        emitAllDueDates();
    }


    public Flux<DueDate> getReminderFlux(){return reminderFlux;}
    public Flux<DueDate> getRemovalFlux(){return removalFlux;}

    /**
     * Gets all due dates based on a predicate.
     * @param predicate - Due date predicate
     * @return List of the courses matching the predicate
     */
    public List<DueDate> filterDueDates(Predicate<? super DueDate> predicate){return dueDates.stream().filter(predicate).toList();}

    @JsonProperty("channel")
    public void setChannel(Long channelId){
        channel = channelId;
    }
    @JsonIgnore
    public Optional<Long> getChannel(){return Optional.ofNullable(channel);}

    @JsonProperty("channel")
    public Long getChannelNullable(){return channel;}

    public List<DueDate> getDueDates(){return Collections.unmodifiableList(dueDates);}

    public List<Course> getCourses(){return Collections.unmodifiableList(courses);}

    /**
     * Removes a due date and returns it.
     * @param index - Index of the due date to be removed.
     * @return DueDate that was removed.
     */
    public DueDate removeDueDate(int index){return dueDates.remove(index);}

    public void removeDueDate(DueDate dueDate){dueDates.remove(dueDate);}

    public void addCourse(String courseName){
        courses.add(new Course(courseName));
    }

    /**
     * Adds a DueDate, and also sets it to be emitted to the reminder and removal fluxes
     * @param dueDate - DueDate to be added.
     */
    public void addDueDate(DueDate dueDate){
        dueDates.add(dueDate);
        emitDueDate(dueDate);
    }

    /**
     * Returns an optional of a course based on the course name.
     * @param courseName Name of the course to be gotten
     * @return Optional containing course if it exists, empty if it does not
     */
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

    /**
     * Returns whether a user is in a course.
     * @param course Name of the course.
     * @param userId Id of the user.
     * @return True if the user is in the course, false if the user is not in the course or the course does not exist.
     */
    public boolean userInCourse(String course, Long userId){
        return getCourse(course).map(value -> value.containsUser(userId)).orElse(false);
    }


    /**
     * Removes a course from the list of courses
     * @param course Course to be removed
     */

    public void removeCourse(Course course){
        courses.remove(course);
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
            logger.log(Level.INFO, e + ": Creating empty database");
            return new Database();
        }
    }

    public static void save() throws IOException{
        DATABASE.save(FILENAME);
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
        Database database =  objectMapper.readValue(stream, Database.class);
        database.getDueDates().forEach(d -> d.setCourse(database));
        return database;
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

    @Override
    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof Database other)) return false;
        return other.dueDates.equals(dueDates) && other.courses.equals(courses);
    }
    @Override
    public String toString(){
        return courses + "\n" + dueDates;
    }

}
