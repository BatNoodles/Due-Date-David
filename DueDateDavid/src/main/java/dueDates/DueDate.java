package dueDates;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;


/**
 * A data structure to represent a due date. Has a name, subject and date/time that it is due.
 */



public class DueDate {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MMHH:mm");

    private String name;
    private Subject course;
    private Date date;

    public static boolean dateTimeIsValid(String date, String time){
        try{
            dateFormat.parse(date + time);
            return true;
        } catch (DateTimeParseException e) {return false;}
    }

    public DueDate(String name, String course, String date, String time) {
        this.name = name;

    }
}
