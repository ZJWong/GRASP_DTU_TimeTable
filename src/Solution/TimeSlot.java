package Solution;

import java.io.PrintWriter;
import java.util.ArrayList;

import Data.Course;
import Data.Room;

public class TimeSlot {
	
	// Declaration of variables
	String courseId;
	String roomId;
	int scheduleId;
	int day;
	int period;
	
	
	
	public TimeSlot(String courseId, String roomId, int scheduleId, int day, int period) {
	
		this.courseId = courseId;
		this.roomId = roomId;
		this.scheduleId = scheduleId;
		this.day = day;
		this.period = period;
		
	}
	
	
	public TimeSlot() {}
	
	
	/**
	 * Adds the position (schedule index + timeSlot) of specified course into the positions-system of
	 * lectures. 
	 * @param coursePositions Double arrayList of all lectures scheduled so far. The order of courses
	 * in the arrayLists is based on course numbers (0 indicates the first course from courseFile). 
	 * @param course The course which lecture position data has to be stored.
	 * @param scheduleIndex The schedule index in which the specified lecture is scheduled.
	 * @param dayIndex The day index in which the specified lecture is taking place. 
	 * @param periodIndex The period during which the specified lecture is taking place.
	 * @return coursePositions Double arrayList of all lectures scheduled so far. The order of courses
	 * in the arrayLists is based on course numbers (0 indicates the first course from courseFile).
	 */
	public static ArrayList<ArrayList<TimeSlot>> addPosition(ArrayList<ArrayList<TimeSlot>> coursePositions,
			Course course, Room room, int scheduleIndex, int dayIndex, int periodIndex) {
		
		String courseId = course.getCourseId();
		String roomId = room.getRoomId();
		
		// Extract courseNumber used as identifier for the arrayList
		int courseNumber = Integer.parseInt(courseId.substring(1, courseId.length()));
		
		TimeSlot courseTimeSlot = new TimeSlot();
		
		// Set positions data for the course
		courseTimeSlot.setCourseId(courseId);
		courseTimeSlot.setRoomId(roomId);
		courseTimeSlot.setScheduleId(scheduleIndex);
		courseTimeSlot.setPeriod(periodIndex);
		courseTimeSlot.setDay(dayIndex);
		
		// Add positions data to arrayList
		coursePositions.get(courseNumber).add(courseTimeSlot);
		
		return coursePositions;	
	}
	

	/**
	 * Removes last added course from coursePositions.
	 * @param coursePositions ArrayList of course positions in schedule.
	 * @param course The course to be removed.
	 * @return coursePositions Updated coursePositions double arrayList.
	 */
	public static ArrayList<ArrayList<TimeSlot>> removePosition(ArrayList<ArrayList<TimeSlot>> coursePositions, Course course) {
		
		int courseNumber = Integer.parseInt(course.getCourseId().substring(1, course.getCourseId().length()));
		
		ArrayList<TimeSlot> courseArrayList = coursePositions.get(courseNumber);
		courseArrayList.remove(courseArrayList.size()-1);
		
		return coursePositions;

	}
	
	
	public static void removePositionHillClimber(ArrayList<ArrayList<TimeSlot>> coursePositions, int courseNumber,
			int scheduleIndex, int dayIndex, int periodIndex) {
		 
		// Extract array corresponding to the course to be removed
		ArrayList<TimeSlot> coursePositionsArray= coursePositions.get(courseNumber);
		
		// Find the TimeSlot with the given scheduleIndex, dayIndex, periodIndex
		for (int i = 0; i < coursePositionsArray.size(); i++) {
			
			// Find the appropriate position record matching the scheduleIndex, dayIndex, periodIndex
			if (coursePositionsArray.get(i).getScheduleId() == scheduleIndex &&
				coursePositionsArray.get(i).getDay() == dayIndex &&
				coursePositionsArray.get(i).getPeriod() == periodIndex) {
				
				// Clear courseId & roomId
				//coursePositionsArray.get(i).setCourseId("");
				//coursePositionsArray.get(i).setRoomId("");
			
				coursePositionsArray.remove(i);
			}
			
		}

	}
	
	
	/**
	 * Prints the double arrayList of lecture positions and room assignments scheduled so far.
	 * @param coursePositions Double arrayList of all lectures scheduled so far. The order of courses
	 * in the arrayLists is based on course numbers (0 indicates the first course from courseFile).
	 */
	public static void printCoursePositions(ArrayList<ArrayList<TimeSlot>> coursePositions) {
		
		System.out.println("\n\nPrinting course positions data:\n");
		
		System.out.println("Comment: Data printed in the following order: ");
		System.out.println("1. course index");
		System.out.println("2. room index");
		System.out.println("3. schedule index");
		System.out.println("4. period index");
		System.out.println("5. day index\n");
		
		System.out.println("--------------------------------------------------");
		
		// For all course arrayLists
		for (int i = 0; i < coursePositions.size(); i++) {
			
			// For all lecture positions in course arrayList
			for (int c = 0; c < coursePositions.get(i).size(); c++) {
				
				TimeSlot currentTimeSlot = coursePositions.get(i).get(c);
				System.out.print("[" + currentTimeSlot.getCourseId() + ", " 
									 + currentTimeSlot.getRoomId() + ", "
									 + currentTimeSlot.getScheduleId() + ", "
									 + currentTimeSlot.getPeriod() + ", "
									 + currentTimeSlot.getDay() + "] ");
			}
			System.out.println();
		}		
		
		System.out.println("--------------------------------------------------");
	}
	
	
	
	/**
	 * Prints the double arrayList of lecture positions and room assignments scheduled so far.
	 * @param coursePositions Double arrayList of all lectures scheduled so far. The order of courses
	 * in the arrayLists is based on course numbers (0 indicates the first course from courseFile).
	 */
	public static void printCoursePositionsToFile(ArrayList<ArrayList<TimeSlot>> coursePositions, PrintWriter outputFile) {
		
		outputFile.println("\n\nPrinting course positions data:\n");
		
		outputFile.println("Comment: Data printed in the following order: ");
		outputFile.println("1. course index");
		outputFile.println("2. room index");
		outputFile.println("3. schedule index");
		outputFile.println("4. period index");
		outputFile.println("5. day index\n");
		
		// For all course arrayLists
		for (int i = 0; i < coursePositions.size(); i++) {
			
			// For all lecture positions in course arrayList
			for (int c = 0; c < coursePositions.get(i).size(); c++) {
				
				TimeSlot currentTimeSlot = coursePositions.get(i).get(c);
				outputFile.print("[" + currentTimeSlot.getCourseId() + ", " 
									 + currentTimeSlot.getRoomId() + ", "
									 + currentTimeSlot.getScheduleId() + ", "
									 + currentTimeSlot.getPeriod() + ", "
									 + currentTimeSlot.getDay() + "] ");
			}
			outputFile.println();
		}		
		
	}
	
	
	
	public String getCourseId() {
		return courseId;
	}
	
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	
	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public int getScheduleId() {
		return scheduleId;
	}
	
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	
	public int getPeriod() {
		return period;
	}
	
	public void setPeriod(int period) {
		this.period = period;
	}

}
