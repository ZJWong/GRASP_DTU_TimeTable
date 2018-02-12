package Solution;

import Data.Course;
import Data.Room;

public class Schedule {
	
	// Declaration of variables
	private int dayIndex;
	private int periodIndex;
	private String courseId;
	private String roomId;
	
	
	public Schedule(int periodIndex, int dayIndex, String courseId, String roomId) {
		
		this.periodIndex = periodIndex;
		this.dayIndex = dayIndex;
		this.courseId = courseId;
		this.roomId = roomId;
		
	}
	
	
	public Schedule() {}
	
	
	/**
	 * Creates new empty schedule with the specified number of columns (days) and rows (periods)
	 * @param periods Number of periods.
	 * @param days Number of days.
	 * @return schedule New empty schedule.
	 */
	public static Schedule[][] makeNewSchedule(int periods, int days) {
		
		Schedule[][] schedule = new Schedule[periods][days];
		
		// Add new timeslots to schedule
		for(int p = 0; p < periods; p++) {
            for(int d = 0; d < days; d++) {
            schedule[p][d] = new Schedule(p, d, "", "");         
            }  
		}
		
		return schedule;
		
	}
	
	
	/**
	 * Assigns specified room for the specified course and adds the course to schedule at specified position.
	 * @param schedule Current set of schedules (solution).
	 * @param course Course to be inserted in the schedules (solution).
	 * @param room Room assigned to the course.
	 * @param periodIndex Index of the period at which the course should be inserted at.
	 * @param dayIndex Index of the day at which the course should be inserted at.
	 * @return updatedSchedule Updated schedules (solution).
	 */
	public static Schedule[][] addCourseSetRoom(Schedule[][] schedule, Course course, Room room, int periodIndex, int dayIndex) {
		
		// Take a copy of current schedule
		Schedule[][] updatedSchedule = schedule;
			
		// Insert course in the specified time-slot if empty
		if (updatedSchedule[periodIndex][dayIndex].getCourseId().equals("")) {
			String courseId = course.getCourseId();
			updatedSchedule[periodIndex][dayIndex].setCourseId(courseId);
			updatedSchedule[periodIndex][dayIndex].setRoomId(room.getRoomId());
//			System.out.println("Course " + courseId + " added to timeslot[" + periodIndex +"][" + dayIndex + "] and assigned room " + room.getRoomId());
		}
		
		return updatedSchedule;
		
	}
	
	
	public static Schedule[][] addCourseOnly(Schedule[][] schedule, Course course, int periodIndex, int dayIndex) {
		
		// Take a copy of current schedule
		Schedule[][] updatedSchedule = schedule;
			
		// Insert course in the specified time-slot if empty
		if (updatedSchedule[periodIndex][dayIndex].getCourseId().equals("")) {
			String courseId = course.getCourseId();
			updatedSchedule[periodIndex][dayIndex].setCourseId(courseId);
			//updatedSchedule[periodIndex][dayIndex].setRoomId(room.getRoomId());
//			System.out.println("Course " + courseId + " added to timeslot[" + periodIndex +"][" + dayIndex + "] and assigned room " + room.getRoomId());
		}
		
		return updatedSchedule;
		
	}
	
	
	/**
	 * Removes course from the specified position in the schedule.
	 * @param schedule Schedule from which the specified course should be deleted from.
	 * @param periodIndex Period during which the specified course is held.
	 * @param dayIndex Day at which the specified course is held.
	 * @return updatedSchedule The new schedule (with updates).
	 */
	public static Schedule[][] removeCourse(Schedule[][] schedule, int periodIndex, int dayIndex) {
		
		// Take a copy of current schedule
		Schedule[][] updatedSchedule = schedule;
				
//		String removedCourseId = updatedSchedule[periodIndex][dayIndex].getCourseId();
//		Course removedCourse = ReadData.getCourseByIdentifier(removedCourseId);
		
		updatedSchedule[periodIndex][dayIndex].setCourseId("");
		updatedSchedule[periodIndex][dayIndex].setRoomId("");
		
		return updatedSchedule;
	}
	
	
	public static Schedule[][] removeOnlyCourse(Schedule[][] schedule, int periodIndex, int dayIndex) {
		
		// Take a copy of current schedule
		Schedule[][] updatedSchedule = schedule;
				
//		String removedCourseId = updatedSchedule[periodIndex][dayIndex].getCourseId();
//		Course removedCourse = ReadData.getCourseByIdentifier(removedCourseId);
		
		updatedSchedule[periodIndex][dayIndex].setCourseId("");
		//updatedSchedule[periodIndex][dayIndex].setRoomId("");
		
		return updatedSchedule;
	}
	
	
//	/**
//	 * Prints content of a schedule to console
//	 * @param schedule The schedule to be printed
//	 */
//	public static void printScheduleToConsole(Schedule[][] schedule) {
//
//		System.out.println("Printing schedule");
//
//		// Print day abbreviations
//		System.out.println("--------------------------------------------------");
//		String[] dayAbb = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
//
//		int days = schedule[0].length;
//
//		for (int d = 0; d < days; d++) {
//			if (d == days - 1) {
//				System.out.print("|  " + dayAbb[d] + "  || p |");
//			} else {
//				System.out.print("|  " + dayAbb[d] + "  |");
//			}
//		}
//		System.out.println("\n--------------------------------------------------");
//
//		// Print schedule content
//		for (int p = 0; p < Data.ReadData.getBasicData().getPeriods(); p++) {
//
//			// print course Id's
//			for (int c = 0; c < schedule[p].length; c++) {
//				
//				// print course id's:
//				// If last day of the week -> print period #
//				if (c == schedule[c].length - 1) {
//						
//					// Check if empty
//					if (schedule[p][c].getCourseId().equals("")) {
//						System.out.print("|       || " + (p + 1) + " |");
//					} else {
//						System.out.print("| " + schedule[p][c].getCourseId() + " || " + (p + 1) + " |");
//					}
//				} else {
//						
//					// Check if empty
//					if (schedule[p][c].getCourseId().equals("")) {
//						System.out.print("|       |");
//					} else {
//						System.out.print("| " + schedule[p][c].getCourseId() + " |");
//					}
//				}
//
//			}
//
//			System.out.println(); // print rooms in next line right beneath course Id's
//
//			// print rooms
//			for (int r = 0; r < schedule[p].length; r++) {
//					
//				// PRINT COURSE ID'S
//				// If last day of the week -> print period #
//				if (r == schedule[r].length - 1) {
//						
//					// Check if empty
//					if (schedule[p][r].getCourseId().equals("")) {
//						System.out.print("|       ||   |");
//					} else {
//						System.out.print("| " + schedule[p][r].getRoomId() + " ||   |");
//					}
//					
//				} else {
//					// Check if empty
//					if (schedule[p][r].getCourseId().equals("")) {
//						System.out.print("|       |");
//					} else {
//						System.out.print("| " + schedule[p][r].getRoomId() + " |");
//					}
//							
//				}
//	
//			}
//
//			System.out.println("\n--------------------------------------------------");
//		}
//			
//		System.out.println("\n");
//
//	}
	
	
//	/**
//	 * Prints content of a schedule to file
//	 * @param schedule The schedule to be printed
//	 */
//	public static void printScheduleToFile(Schedule[][] schedule, PrintWriter outputFile) {
//
//		outputFile.println();
//		outputFile.println("Printing schedule");
//
//		// Print day abbreviations
//		outputFile.println("--------------------------------------------------");
//		String[] dayAbb = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
//
//		int days = schedule[0].length;
//
//		for (int d = 0; d < days; d++) {
//			if (d == days - 1) {
//				outputFile.print("|  " + dayAbb[d] + "  || p |");
//			} else {
//				outputFile.print("|  " + dayAbb[d] + "  |");
//			}
//		}
//		outputFile.println();
//		outputFile.println("--------------------------------------------------");
//
//		// Print schedule content
//		for (int p = 0; p < Data.ReadData.getBasicData().getPeriods(); p++) {
//
//			// print course Id's
//			for (int c = 0; c < schedule[p].length; c++) {
//				
//				// print course id's:
//				// If last day of the week -> print period #
//				if (c == schedule[c].length - 1) {
//						
//					// Check if empty
//					if (schedule[p][c].getCourseId().equals("")) {
//						outputFile.print("|       || " + (p + 1) + " |");
//					} else {
//						outputFile.print("| " + schedule[p][c].getCourseId() + " || " + (p + 1) + " |");
//					}
//				} else {
//						
//					// Check if empty
//					if (schedule[p][c].getCourseId().equals("")) {
//						outputFile.print("|       |");
//					} else {
//						outputFile.print("| " + schedule[p][c].getCourseId() + " |");
//					}
//				}
//
//			}
//
//			outputFile.println(); // print rooms in next line right beneath course Id's
//
//			// print rooms
//			for (int r = 0; r < schedule[p].length; r++) {
//					
//				// PRINT COURSE ID'S
//				// If last day of the week -> print period #
//				if (r == schedule[r].length - 1) {
//						
//					// Check if empty
//					if (schedule[p][r].getCourseId().equals("")) {
//						outputFile.print("|       ||   |");
//					} else {
//						outputFile.print("| " + schedule[p][r].getRoomId() + " ||   |");
//					}
//					
//				} else {
//					// Check if empty
//					if (schedule[p][r].getCourseId().equals("")) {
//						outputFile.print("|       |");
//					} else {
//						outputFile.print("| " + schedule[p][r].getRoomId() + " |");
//					}
//							
//				}
//	
//			}
//
//			outputFile.println();
//			outputFile.println("--------------------------------------------------");
//		}
//			
//		outputFile.println("\n");
//
//	}
	

	// Getters & Setters
	public int getDayIndex() {
		return dayIndex;
	}


	public void setDayIndex(int dayIndex) {
		this.dayIndex = dayIndex;
	}


	public int getPeriodIndex() {
		return periodIndex;
	}


	public void setPeriodIndex(int periodIndex) {
		this.periodIndex = periodIndex;
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


}
