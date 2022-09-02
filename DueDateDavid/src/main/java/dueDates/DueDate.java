package dueDates;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * A data structure to represent a due date. Has a name, subject and date/time that it is due.
 */
public class DueDate {
    private static final DateTimeFormatter dateFormat =DateTimeFormatter.ofPattern("yyyydd/MMHH:mm");

    private final String name;
    private final Course course;
    private final LocalDateTime date;

    /**
     * Returns if the strings for date and time are in the correct format by checking if a DateTimeParseException is raised.
     * @param date - String for the date in dd/MM format.
     * @param time - String for the time in HH:mm format.
     * @return True if the date and time are valid, false if either are not.
     */
    public static boolean dateTimeIsValid(String date, String time){
        try{
            LocalDateTime.parse(LocalDate.now().getYear() + date + time, dateFormat);
            return true;
        } catch (DateTimeParseException e) {return false;}
    }

    public DueDate(String name, String course, String date, String time) {
        this.name = name;

        this.course = Database.getInstance().getOrAddCourse(course);
        this.date = LocalDateTime.parse(LocalDateTime.now().getYear() + date + time, dateFormat); //Kind of lazy, but it is very unlikely that anyone would add a due date in another year - at least for the NZ school schedule.
    }

    /**
     * Returns the duration until the DueDate is due.
     * @return - Duration until the time of the DueDate.
     */
    public Duration getTimeUntil(){
        return Duration.between(LocalDateTime.now(), date);
    }

    /**
     * Gets the course associated with this DueDate
     * @return - Course
     */
    public Course getCourse(){return course;}

    /**
     * Returns the name of the DueDate
     * @return String - name
     */
    public String getName(){return name;}

    @Override
    public String toString(){
        return String.format("(%s) %s at %d:%d on %d/%d", course, name, date.getHour(), date.getMinute(), date.getDayOfMonth(), date.getMonthValue());
    }


    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof DueDate other)) return false;

        return other.course.equals(course) && other.date.equals(date) && other.name.equals(name);
    }


}
