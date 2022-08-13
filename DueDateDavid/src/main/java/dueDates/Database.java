package dueDates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Singleton database.
 */
public class Database {
    private final List<DueDate> dueDates;
    private final List<Course> courses;

    private static final Database DATABASE = new Database();

    public static Database getInstance(){
        return DATABASE;
    }

    private Database(){
        dueDates = new ArrayList<>();
        courses = new ArrayList<>();
    }

    public List<DueDate> getDueDates(){return Collections.unmodifiableList(dueDates);}

    public List<Course> getCourses(){return Collections.unmodifiableList(courses);}

    /**
     * Gets all due dates based on a predicate.
     * @param predicate - Due date predicate
     * @return List of the courses matching the predicate
     */
    public List<DueDate> filterDueDates(Predicate<? super DueDate> predicate){return dueDates.stream().filter(predicate).toList();}

    public void addCourse(String courseName){
        courses.add(new Course(courseName));}

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


}
