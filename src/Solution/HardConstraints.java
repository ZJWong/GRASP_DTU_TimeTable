package Solution;

import java.util.ArrayList;

import Data.Course;
import Data.ReadData;
import Data.UnavailabilityByCourse;

public class HardConstraints {
	
	private String courseId;
	private String roomId;
	private int dayIndex;
	private int periodIndex;
	private ArrayList<ArrayList<TimeSlot>> coursePositions = new ArrayList<ArrayList<TimeSlot>>();
	private ArrayList<Schedule[][]> solution = new ArrayList<Schedule[][]>();

	public HardConstraints(String courseId, String roomId, int dayIndex, int periodIndex, ArrayList<ArrayList<TimeSlot>> coursePositions, ArrayList<Schedule[][]> solution){	
		this.courseId = courseId;
		this.roomId = roomId;
		this.dayIndex = dayIndex;
		this.periodIndex = periodIndex;
		this.coursePositions = coursePositions;
		this.solution = solution;
	}
	
	
	
	public Boolean hardConstraintsViolated(int hillClimber){
		
//		TimeSlot timeslot = coursePositions.get(0).get(0);
		
		Boolean hardConstraintsViolated = false;
		
		Boolean distinctTimeSlots = checkDistinctTimeSlots();
		Boolean roomOccupancy = checkRoomOccupancy();
		Boolean conflicts = checkConflicts();
		Boolean availabilities = checkAvailabilities();
		
		if (hillClimber == 1) {
			roomOccupancy = false;
		}

		hardConstraintsViolated = (distinctTimeSlots || roomOccupancy || conflicts || availabilities); // &&Boolean2&&Boolean3&&Boolean4
//		System.out.println("hardConstraintsViolated: "+ hardConstraintsViolated);
		return hardConstraintsViolated;
		
	}


	/**
	 * Checks whether each lecture of a course is scheduled in distinct timeSlots. 
	 * @return true If hard constraint is violated, false otherwise.
	 */
	private Boolean checkDistinctTimeSlots() {
		
		// Check whether other lecture belonging to the same course is already placed at the timeSlot
		for (int i = 0; i < solution.size(); i++) {
			Schedule[][] schedule = solution.get(i);
			if(schedule[periodIndex][dayIndex].getCourseId().equals(courseId)){
				return true;
			}
		}
		
		// Checks whether all lectures of a course are scheduled. If so return false indicating that no further lectures of that specific course can be inserted.
		int numberCourses = coursePositions.size();
		for (int count = 0; count < numberCourses; count++){
			// Ignore courses which have not been scheduled at all yet
			if(coursePositions.get(count).size() == 0){
				continue;
			}
			TimeSlot currentTimeSlot = coursePositions.get(count).get(0);
			
			if (currentTimeSlot.getCourseId() != null) continue;
			
			String tmpCourseId = currentTimeSlot.getCourseId();
			System.out.println("tmpCourseId "+ tmpCourseId);
			Course courses = ReadData.getCourseByIdentifier(tmpCourseId); // Number of Lectures a Course has to be taught
			System.out.println(courses.getCourseId());
			int numberLectures = courses.getLectures();
			if(tmpCourseId.equals(courseId)){
				int countCol = coursePositions.get(count).size();
				if (countCol >= numberLectures) {
					return true;
				}
			}
		}
		return false;
	}

	
	/**
	 * Checks whether two lectures are scheduled in the same room in the same timeSlot. 
	 * @return true If hard constraint is violated, false otherwise.
	 */
	private Boolean checkRoomOccupancy() {

		for (int i = 0; i < solution.size(); i++) {
			Schedule[][] schedule = solution.get(i);
			if(schedule[periodIndex][dayIndex].getRoomId().equals(roomId)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether lectures of courses in the same curriculum or taught by the same lecturer
	 * are scheduled in different timeSlots.
	 * @return true If hard constraint is violated, false otherwise.
	 */
	private Boolean checkConflicts() {
		
		// Specified course (input)
		Course course1 = ReadData.getCourseByIdentifier(courseId);
		String lecturerCourse1 = course1.getLecturer();
		
		for (int i = 0; i < solution.size(); i++) {
			Schedule[][] schedule = solution.get(i);
			String courseFromTimeSlot = schedule[periodIndex][dayIndex].getCourseId();
			
			// Ignore empty timeSlots 
			if (courseFromTimeSlot.equals("")) {
				continue;
			}
			
			Course course2 = ReadData.getCourseByIdentifier(courseFromTimeSlot);
			if ((course2.getLecturer().equals(lecturerCourse1) || compareTheCurrciculum(courseId, courseFromTimeSlot))) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Compares curricular of two courses, returns true if they are identical.
	 * @param courseId1 Index of course 1.
	 * @param courseId2 Index of course 2.
	 * @return true If curricular of both courses are identical.
	 */
	Boolean compareTheCurrciculum(String courseId1, String courseId2) {
		
		int courseNumber1 = Integer.parseInt(courseId1.substring(1, courseId1.length()));
		int courseNumber2 = Integer.parseInt(courseId2.substring(1, courseId2.length()));
		for (int i = 0; i < ReadData.getRelationsArrayListByCourses().get(courseNumber1).size(); i++) {
			for (int j = 0; j < ReadData.getRelationsArrayListByCourses().get(courseNumber2).size() ; j++) {
				if (ReadData.getRelationsArrayListByCourses().get(courseNumber1).get(i).getCurriculumId().equals(
						ReadData.getRelationsArrayListByCourses().get(courseNumber2).get(j).getCurriculumId())) {
					return true;
				}
			}
		}
		
		return false;
	}

	
	/**
	 * Checks whether the availabilities are not violated.
	 * @return true If hard constraint is violated, false otherwise.
	 */
	private Boolean checkAvailabilities() {
		ArrayList<ArrayList<UnavailabilityByCourse>> unavailability = ReadData.getUnavailabilitiesBycourse();
		int courseNumber=Integer.parseInt(courseId.substring(1));
		for (int i = 0; i < unavailability.get(courseNumber).size(); i++) {
			if (dayIndex == unavailability.get(courseNumber).get(i).getDay() && periodIndex == unavailability.get(courseNumber).get(i).getPeriod()) {
				return true;
			}
		}
		return false;
	}

}
