package edu.isu.cs2263.hw03;

/**
 * The class that contains information about each course
 */
public class Course {
    private String department;
    private String courseNum;
    private String name;
    private String credit;

    public static final String[] departments = {
            "CS", "MATH", "CHEM", "PHYS", "BIOL", "EE"
    };

    public static final String[] departmentsFullName ={
            "Computer Science", "Mathematics", "Chemistry", "Physics", "Biology", "Electrical Engineering"
    };

    /**
     * @param dept department name
     * @param courseNum course number
     * @param name course name
     * @param credit number of credits
     */
    public Course(String dept, String courseNum, String name, String credit){
        this.department = dept;
        this.courseNum = courseNum;
        this.name = name;
        this.credit = credit;

    }

    /**
     * @param name Department full name
     * @return Department abbreviation
     */
    public static String getDeptName(String name){
        for(int i = 0; i < departments.length; i++){
            if(departmentsFullName[i] == name){
                return departments[i];
            }
        }
        return null;
    }

    /**
     * @return return department name
     */
    public String getDepartment(){return this.department;}

    /**
     * @return return course number
     */
    public String getCourseNum(){return this.courseNum;}

    /**
     * @return return course name
     */
    public String getName(){return this.name;}

    /**
     * @return return course's number of credit
     */
    public String getCredit(){return this.credit;}

}
