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
    private final List<Subject> subjects;

    private static final Database DATABASE = new Database();

    public static Database getInstance(){
        return DATABASE;
    }

    private Database(){
        dueDates = new ArrayList<>();
        subjects = new ArrayList<>();
    }

    public List<DueDate> getDueDates(){return Collections.unmodifiableList(dueDates);}

    public List<Subject> getSubjects(){return Collections.unmodifiableList(subjects);}

    /**
     * Gets all due dates based on a predicate.
     * @param predicate - Due date predicate
     * @return List of the subjects matching the predicate
     */
    public List<DueDate> filterDueDates(Predicate<? super DueDate> predicate){return dueDates.stream().filter(predicate).toList();}

    public void addSubject(String subjectName){subjects.add(new Subject(subjectName));}

    public Optional<Subject> getSubject(String subjectName){return subjects.stream().filter(s->s.getName().equalsIgnoreCase(subjectName)).findFirst();}

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
                    Subject s = new Subject(subjectName);
                    s.addUserId(userId);
                    subjects.add(s);
                }
        );
    }


}
