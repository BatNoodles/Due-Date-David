package dueDates;

import java.util.HashSet;
import java.util.Set;

/**
 * Data structure to represent a subject. Has a subject name and a list of discord users "enrolled" in that class.
 */
public class Course {
    private final String name;
    private final Set<Long> memberIds;

    public Course(String name){
        this.name = name;
        memberIds = new HashSet<>();
    }
    public String getName(){return name;}
    public boolean containsUser(Long userId){
        return memberIds.contains(userId);
    }
    public void addUserId(Long userId){memberIds.add(userId);}
    @Override
    public String toString(){return name;}
}
