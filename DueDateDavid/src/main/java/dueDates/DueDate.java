package dueDates;

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




    @Override
    public String toString(){
        return String.format("(%s) %s at %s on %d:%d", name, course, date.toLocalDate().toString(), date.getHour(), date.getMinute());
    }


}
