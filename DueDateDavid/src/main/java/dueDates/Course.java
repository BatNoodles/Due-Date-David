package dueDates;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonCreator
    public Course(
            @JsonProperty("name")
            String name,
            @JsonProperty("memberIds")
            @JsonAlias("members")
            HashSet<Long> memberIds){
        this.name = name;
        if (memberIds == null) this.memberIds = new HashSet<>();
        else this.memberIds = new HashSet<>(memberIds);
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
     * Removes a user and returns whether the userId was in the set of memberIds.
     * @param userId Long - the userId to be removed
     * @return true if the userId was present in the memberIds
     */
    public boolean removeUser(Long userId){return memberIds.remove(userId);}
    /**
     * Adds a userId to the course
     * @param userId Long - userId
     */
    public void addUserId(Long userId){memberIds.add(userId);}
    @Override
    public String toString(){return name;}

    public boolean equals(Object o){
        if (o == null) return false;
        if (!(o instanceof  Course other)) return false;
        return other.name.equals(name) && other.memberIds.equals(memberIds);
    }
}
