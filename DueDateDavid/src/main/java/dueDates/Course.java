package dueDates;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Data structure to represent a subject. Has a subject name and a list of discord users "enrolled" in that class.
 */
public class Course {
    private final String name;
    private final Set<Long> memberIds;

    public Course(String name){
        this.name = name.toUpperCase();
        memberIds = new HashSet<>();
    }

    /**
     * Gets the name of the course
     * @return String - name
     */
    public String getName(){return name;}

    public Set<Long> getMembers(){return Collections.unmodifiableSet(memberIds);}

    /**
     * Returns true if this course contains the userId
     * @param userId Long  - the userId
     * @return true if the userId is contained
     */
    public boolean containsUser(Long userId){
        return memberIds.contains(userId);
    }

    /**
     * Adds a userId to the course
     * @param userId Long - userId
     */
    public void addUserId(Long userId){memberIds.add(userId);}
    @Override
    public String toString(){return name;}
}
