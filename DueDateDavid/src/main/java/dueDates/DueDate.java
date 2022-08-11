package dueDates;

import java.util.Date;


/**
 * A data structure to represent a due date. Has a name, subject and date/time that it is due.
 */


//TODO: Decide whether the date should be inputted as a string (and then checked) or keep it as a number. same with time?

public class DueDate {
    private String name;
    private Subject subject;
    private Date date;

    public DueDate(String name, String subject, int day, int month, int hour) {
        this.name = name;
    }
}
