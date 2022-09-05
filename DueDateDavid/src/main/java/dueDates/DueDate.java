package dueDates;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
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

    public DueDate(String name, String course, String date, String time, Database database) {
        this.name = name;

        this.course = database.getOrAddCourse(course);
        this.date = LocalDateTime.parse(LocalDateTime.now().getYear() + date + time, dateFormat); //Kind of lazy, but it is very unlikely that anyone would add a due date in another year - at least for the NZ school schedule.
    }

    public DueDate(String name, String course, String date, String time) {
        this(name, course, date, time, Database.getInstance());
    }

    /**
     * Constructor for a deserialized DueDate
     * @param name - String name
     * @param date - LocalDateTime date
     * @param course - String name of the course
     */
    public DueDate(String name, LocalDateTime date, String course){
        this.name = name;
        this.date = date;
        this.course =  Database.getInstance().getOrAddCourse(course); //A course should never be added by this method if the serialization & deserialization is done correctly.
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
     * Gets the LocalDateTime associated with this due date.
     * @return - LocalDateTime
     */
    public LocalDateTime getTime(){return date;}

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


    /**
     * Custom Serializer for serializing DueDates. This is needed as it would be inefficient to directly serialize the contents of the courses as they are already serialized by the database.
     */
    public static class DueDateSerializer extends StdSerializer<DueDate> {
        public DueDateSerializer() {
            this(null);
        }
        public DueDateSerializer(Class<DueDate> t) {
            super(t);
        }
        @Override
        public void serialize(DueDate dueDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", dueDate.name);
            jsonGenerator.writeStringField("date",  dueDate.date.toString());
            jsonGenerator.writeStringField("course", dueDate.course.getName());
            jsonGenerator.writeEndObject();

        }
    }

    public static class DueDateDeserializer extends StdDeserializer<DueDate> {
        public DueDateDeserializer() {
            this(null);
        }
        public DueDateDeserializer(Class<DueDate> t) {
            super(t);
        }
        @Override
        public DueDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException{
            JsonNode node =  jsonParser.getCodec().readTree(jsonParser);

            JsonNode nameNode = node.get("name");
            JsonNode courseNameNode = node.get("course");
            JsonNode dateNode = node.get("date");

            return new DueDate(nameNode.asText(), LocalDateTime.parse(dateNode.asText()), courseNameNode.asText());


        }
    }


}
