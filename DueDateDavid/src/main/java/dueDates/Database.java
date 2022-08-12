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

    public List<Course> getSubjects(){return Collections.unmodifiableList(courses);}

    /**
     * Gets all due dates based on a predicate.
     * @param predicate - Due date predicate
     * @return List of the subjects matching the predicate
     */
    public List<DueDate> filterDueDates(Predicate<? super DueDate> predicate){return dueDates.stream().filter(predicate).toList();}

    public void addSubject(String subjectName){
        courses.add(new Course(subjectName));}

    public void addDueDate(DueDate dueDate){dueDates.add(dueDate);}

    public Optional<Course> getSubject(String subjectName){return courses.stream().filter(s->s.getName().equalsIgnoreCase(subjectName)).findFirst();}

    /**
     * Gets the subject with the specified name, creates it first if it does not exist.
     * @param subjectName - Name of the subject
     * @return Subject with the specified name
     */
    public Course getOrAddSubject(String subjectName){
        Optional<Course> subject = getSubject(subjectName);
        if (subject.isEmpty()){
            Course s = new Course(subjectName);
            courses.add(s);
            return s;
        }
        return subject.get();
    }

    /**
     * Adds a discord user ID to a subject; i.e. Allows a user to join a subject so that they can be notified of any due dates in that class.
     * If the subject does not exist it is created.
     * @param subjectName - Name of the subject, case-insensitive
     * @param userId - Discord user id
     */
    public void joinSubject(String subjectName, Long userId){
        getSubject(subjectName).ifPresentOrElse(
                s->s.addUserId(userId),
                ()-> {
                    Course s = new Course(subjectName);
                    s.addUserId(userId);
                    courses.add(s);
                }
        );
    }


}
