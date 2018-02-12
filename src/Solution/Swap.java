package Solution;

import java.util.ArrayList;

import Data.Course;
import Data.ReadData;
import Data.Room;
import Solver.HillClimber;
import Solution.TimeSlot;

public class Swap {
	
	// For only swapping rooms
	private ArrayList<Schedule[][]> solutionCopyOneOnly = new ArrayList<Schedule[][]>();
//	private ArrayList<ArrayList<TimeSlot>> coursePositionsCopyRoomId = new ArrayList<ArrayList<TimeSlot>>();
	ArrayList<ArrayList<TimeSlot>> coursePositionsOneOnly = HillClimber.setCoursePositions(ReadData.getCourses().size());
	private double DeltaOneOnly;
	
	
	// For only swapping courses
	private ArrayList<Schedule[][]> solutionCopyCourseId = new ArrayList<Schedule[][]>();
//	private ArrayList<ArrayList<TimeSlot>> coursePositionsCopyCourseId = new ArrayList<ArrayList<TimeSlot>>();
	private ArrayList<ArrayList<TimeSlot>> coursePositionsCopyCourseId = HillClimber.setCoursePositions(ReadData.getCourses().size());
	private double DeltaCourse;
	
	// For swapping both rooms and courses
	private ArrayList<Schedule[][]> solutionCopyAddOnly = new ArrayList<Schedule[][]>();
//	private ArrayList<ArrayList<TimeSlot>> coursePositionsCopyBoth = new ArrayList<ArrayList<TimeSlot>>();
	ArrayList<ArrayList<TimeSlot>> coursePositionsAddOnly = HillClimber.setCoursePositions(ReadData.getCourses().size());
	private double DeltaAddOnly;
	
	

	public void oneOnlySwap(int scheduleIndex1, int scheduleIndex2, int dayIndex1, int dayIndex2,
			int periodIndex1, int periodIndex2, 
			ArrayList<ArrayList<TimeSlot>> coursePositions, ArrayList<Schedule[][]> solution,
			int unscheduledCourses, int remainingUnscheduled) {
		/* 1 indicates the first timeslot in the solution we want to swap, where number 2 indicates 
		the last timeslot in the solution. */
		
		// Make a copy of both solution and coursePositions
//		ArrayList<ArrayList<TimeSlot>> coursePositionsCopyRoomId = coursePositions;
//		ArrayList<Schedule[][]> solutionCopyRoomId = solution;
		
		HillClimber.deepCopyForSchedule(solutionCopyOneOnly, solution);
		HillClimber.deepCopyForTimeSlot(coursePositionsOneOnly, coursePositions);
		
		// Get the schedule for room1 and room2
		Schedule[][] schedule1 = solutionCopyOneOnly.get(scheduleIndex1);
		Schedule[][] schedule2 = solutionCopyOneOnly.get(scheduleIndex2);
		
		//Get roomId for both rooms
		String courseId1 = schedule1[periodIndex1][dayIndex1].getCourseId();
//		String courseId2 = schedule2[periodIndex2][dayIndex2].getCourseId();
		String roomId1 = schedule1[periodIndex1][dayIndex1].getRoomId();
		String roomId2 = schedule2[periodIndex2][dayIndex2].getRoomId();
		
		// Replace the roomId for the two timeslots
		schedule1[periodIndex1][dayIndex1].setCourseId("");
		schedule2[periodIndex2][dayIndex2].setCourseId(courseId1);
	
		// Update solutionCopy with the new schedule
		this.solutionCopyOneOnly = Solution.replaceScheduleInSolution(solutionCopyOneOnly, schedule1, scheduleIndex1); 
		this.solutionCopyOneOnly = Solution.replaceScheduleInSolution(solutionCopyOneOnly, schedule2, scheduleIndex2);  
		
		
		
		//Method to update the coursePosition ArrayArrayList
		
		//First get courseId
		//String courseId1 = schedule1[periodIndex1][dayIndex1].getCourseId();
		//String courseId2 = schedule2[periodIndex2][dayIndex2].getCourseId();
		
		// Go through all coursePositions in order to find courseId1 and 2
		int numberCourses = coursePositionsOneOnly.size();
		label12:
		for(int count = 0; count < numberCourses; count++){
			int numberCol = coursePositionsOneOnly.get(count).size();
			if(numberCol == 0){
				continue label12;
			}
			TimeSlot courseLookUp = coursePositionsOneOnly.get(count).get(0);
			// Replace first roomId
			if(courseId1.equals(courseLookUp.getCourseId())){
				for(int n1 = 0; n1 < numberCol; n1++){
					TimeSlot dayPeriodLookUp1 = coursePositionsOneOnly.get(count).get(n1);
					if(dayIndex1 == dayPeriodLookUp1.getDay() && periodIndex1 == dayPeriodLookUp1.getPeriod()){
						dayPeriodLookUp1.setRoomId(roomId2);
						dayPeriodLookUp1.setDay(dayIndex2);
						dayPeriodLookUp1.setPeriod(periodIndex2);
						dayPeriodLookUp1.setScheduleId(scheduleIndex2);
					}
				}
			}
			// Replace second roomId
//			if(courseId2.equals(courseLookUp.getCourseId())){
//				for(int n2 = 0; n2 < numberCol; n2++){
//					TimeSlot dayPeriodLookUp2 = coursePositionsOneOnly.get(count).get(n2);
//					if(dayIndex2 == dayPeriodLookUp2.getDay() && periodIndex2 == dayPeriodLookUp2.getPeriod()){
//						dayPeriodLookUp2.setRoomId(roomId1);
//					}
//				}
//			}
		}
		// for loop for swap coursePositions roomId finish
		
//		this.coursePositionsOneOnly = coursePositionsOneOnly;	
		
		// Define course and room no pass to DeltaEvaluationHillClimber
		Course course1 = ReadData.getCourseByIdentifier(courseId1);
		//Course course2 = ReadData.getCourseByIdentifier(courseId2);
		Room room1 = new Room();
		Room room2 = new Room();
		// Get roomIndex
		int numberOfRooms = ReadData.getBasicData().getRooms();
		for(int roomIndex = 0; roomIndex < numberOfRooms; roomIndex++){
			String tmpRoomId = ReadData.getRooms().get(roomIndex).getRoomId();
			if(roomId1.equals(tmpRoomId)){
				room1.setCapacity(ReadData.getRooms().get(roomIndex).getCapacity());
				room1.setRoomId(tmpRoomId); //or room1.setRoomId(roomId1)
			}
			if(roomId2.equals(tmpRoomId)){
				room2.setCapacity(ReadData.getRooms().get(roomIndex).getCapacity());
				room2.setRoomId(tmpRoomId); //or room2.setRoomId(roomId2)
			}
		}
		
		int checkAdjacent = 0;
		if(dayIndex1 == dayIndex2 && (periodIndex1 - periodIndex2) == -1){
			checkAdjacent = -1;
		}
		else if(dayIndex1 == dayIndex2 && (periodIndex1 - periodIndex2) == 1){
			checkAdjacent = 1;
		}
		
		/* In order to calculate the delta difference we have to calculate the difference before the 
		change and after the change for each course/room. This is done below. */
		double course1Before = DeltaEvaluation.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses, coursePositions,
				course1, room1, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
//		double course2Before = DeltaEvaluationHillClimber.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses, coursePositions,
//				course2, room2, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent);
		double course1After = DeltaEvaluation.getTotalAfterChangeCost(solutionCopyOneOnly, remainingUnscheduled, unscheduledCourses, coursePositionsOneOnly,
				course1, room2, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent);
//		double course2After = DeltaEvaluationHillClimber.getTotalAfterChangeCost(solutionCopyOneOnly, remainingUnscheduled, unscheduledCourses, coursePositionsOneOnly,
//				course2, room1, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
		double comp2Before = DeltaEvaluation.getTotalCurriculumCompactnessPenalty(solution, scheduleIndex2, dayIndex2, periodIndex2, 1);
		double comp2After = DeltaEvaluation.getTotalCurriculumCompactnessPenalty(solutionCopyOneOnly, scheduleIndex1, dayIndex1, periodIndex1, 1);
		this.DeltaOneOnly = course1After - course1Before + comp2After - comp2Before;
		
		
//		System.out.println("");
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course1, room1));
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course1, room2));
//
//		
//		System.out.println("");
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositions, course1, dayIndex1));
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositionsOneOnly, course1, dayIndex2));
//		
//		System.out.println("");
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solution, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent));
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solutionCopyOneOnly, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent));
//		System.out.println("");
//		
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositions, course1));
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositionsOneOnly, course1));
//		System.out.println("");
		
		
		
	}
	
	

	
	
	// Get the new updated rooms swap for solution
	public ArrayList<Schedule[][]> getSolutionCopyOneOnly() {
		return this.solutionCopyOneOnly;
	}
	
	// Get the new updated rooms swap for coursePosition
	public ArrayList<ArrayList<TimeSlot>> getCoursePositionsCopyOneOnly() {
		return this.coursePositionsOneOnly;
	}
	
	//Overall delta for Room
	public double getDeltaOneOnly() {
		return this.DeltaOneOnly;
	}
	
	
	/* Method for courseSwap
	 * -----------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------
	 */
	
	
	public void courseSwap(int scheduleIndex1, int scheduleIndex2, int dayIndex1, int dayIndex2,
			int periodIndex1, int periodIndex2, 
			ArrayList<ArrayList<TimeSlot>> coursePositions, ArrayList<Schedule[][]> solution,
			int unscheduledCourses, int remainingUnscheduled) {
		

		/* 1 indicates the first timeslot in the solution we want to swap, where number 2 indicates 
		the last timeslot in the solution. */
		
		// Make a copy of both solution and coursePositions
//		ArrayList<ArrayList<TimeSlot>> coursePositionsCopyCourseId = coursePositions;
//		ArrayList<Schedule[][]> solutionCopyCourseId = solution;
		HillClimber.deepCopyForSchedule(solutionCopyCourseId, solution);
		HillClimber.deepCopyForTimeSlot(coursePositionsCopyCourseId, coursePositions);
		
		
		
		
		// Get the schedule for course1 and course2
		Schedule[][] schedule1 = solutionCopyCourseId.get(scheduleIndex1);
		Schedule[][] schedule2 = solutionCopyCourseId.get(scheduleIndex2);
		
		//Get courseId (and roomId for later) for both courses
		String courseId1 = schedule1[periodIndex1][dayIndex1].getCourseId();
		String courseId2 = schedule2[periodIndex2][dayIndex2].getCourseId();
		String roomId1 = schedule1[periodIndex1][dayIndex1].getRoomId();
		String roomId2 = schedule2[periodIndex2][dayIndex2].getRoomId();
		
		
		// Replace the courseId for the two timeslots
		schedule1[periodIndex1][dayIndex1].setCourseId(courseId2);
		schedule2[periodIndex2][dayIndex2].setCourseId(courseId1);
	
		// Update solutionCopy with the new schedule
		this.solutionCopyCourseId = Solution.replaceScheduleInSolution(solutionCopyCourseId, schedule1, scheduleIndex1); 
		this.solutionCopyCourseId = Solution.replaceScheduleInSolution(solutionCopyCourseId, schedule2, scheduleIndex2);
		
		
		//Method to update the coursePosition ArrayArrayList
				
		// Go through all coursePositions in order to find courseId1 and 2
		int numberCourses = coursePositionsCopyCourseId.size();
		label11:
		for(int count = 0; count < numberCourses; count++){
			int numberCol = coursePositionsCopyCourseId.get(count).size();
			if(numberCol == 0){
				continue label11;
			}
			TimeSlot courseLookUp = coursePositionsCopyCourseId.get(count).get(0);
			// Replace first courseId
			if(courseId1.equals(courseLookUp.getCourseId())){
				for(int n1 = 0; n1 < numberCol; n1++){
					TimeSlot dayPeriodLookUp1 = coursePositionsCopyCourseId.get(count).get(n1);
					if(dayIndex1 == dayPeriodLookUp1.getDay() && periodIndex1 == dayPeriodLookUp1.getPeriod()){
						dayPeriodLookUp1.setScheduleId(scheduleIndex2);
						dayPeriodLookUp1.setDay(dayIndex2);
						dayPeriodLookUp1.setPeriod(periodIndex2);
						dayPeriodLookUp1.setRoomId(roomId2);	
					}
				}
			}
			// Replace second courseId
			if(courseId2.equals(courseLookUp.getCourseId())){
				for(int n2 = 0; n2 < numberCol; n2++){
					TimeSlot dayPeriodLookUp2 = coursePositionsCopyCourseId.get(count).get(n2);
					if(dayIndex2 == dayPeriodLookUp2.getDay() && periodIndex2 == dayPeriodLookUp2.getPeriod()){
						dayPeriodLookUp2.setScheduleId(scheduleIndex1);
						dayPeriodLookUp2.setDay(dayIndex1);
						dayPeriodLookUp2.setPeriod(periodIndex1);
						dayPeriodLookUp2.setRoomId(roomId1);
						break;
					}
				}
			}
		}
		// for loop for swap coursePositions roomId finish

		//this.coursePositionsCopyCourseId = coursePositionsCopyCourseId;	
		
		// Define course and room no pass to DeltaEvaluationHillClimber
		
		Course course1 = ReadData.getCourseByIdentifier(courseId1);
		Course course2 = ReadData.getCourseByIdentifier(courseId2);
		Room room1 = new Room();
		Room room2 = new Room();
		// Get roomIndex
		int numberOfRooms = ReadData.getBasicData().getRooms();
		for(int roomIndex = 0; roomIndex < numberOfRooms; roomIndex++){
			String tmpRoomId = ReadData.getRooms().get(roomIndex).getRoomId();
			if(roomId1.equals(tmpRoomId)){
				room1.setCapacity(ReadData.getRooms().get(roomIndex).getCapacity());
				room1.setRoomId(tmpRoomId); //or room1.setRoomId(roomId1)
			}
			if(roomId2.equals(tmpRoomId)){
				room2.setCapacity(ReadData.getRooms().get(roomIndex).getCapacity());
				room2.setRoomId(tmpRoomId); //or room2.setRoomId(roomId2)
			}
		}
		
		int checkAdjacent = 0;
		if(dayIndex1 == dayIndex2 && (periodIndex1 - periodIndex2) == -1){
			checkAdjacent = -1;
		}
		else if(dayIndex1 == dayIndex2 && (periodIndex1 - periodIndex2) == 1){
			checkAdjacent = 1;
		}
		
		/* In order to calculate the delta difference we have to calculate the difference before the 
		change and after the change for each course/room. This is done below. */
		//TimeSlot.printCoursePositions(coursePositions);
		//TimeSlot.printCoursePositions(coursePositionsCopyCourseId);
		//Solution.printSolutionToConsole(solution);
		//Solution.printSolutionToConsole(solutionCopyCourseId);
		double course1Before = DeltaEvaluation.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses, coursePositions,
				course1, room1, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
		double course2Before = DeltaEvaluation.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses, coursePositions,
				course2, room2, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent);
		double course1After = DeltaEvaluation.getTotalAfterChangeCost(solutionCopyCourseId, remainingUnscheduled, unscheduledCourses, coursePositionsCopyCourseId,
				course1, room2, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent);
		double course2After = DeltaEvaluation.getTotalAfterChangeCost(solutionCopyCourseId, remainingUnscheduled, unscheduledCourses, coursePositionsCopyCourseId,
				course2, room1, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
		this.DeltaCourse = course1After + course2After - course1Before  - course2Before;
		
//		System.out.println("");
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course1, room1));
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course1, room2));
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course2, room2));
//		System.out.println("Room: " + DeltaEvaluationHillClimber.getTotalRoomCapacityPenalty(course2, room1));
//		
//		System.out.println("");
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositions, course1, dayIndex1));
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositionsCopyCourseId, course1, dayIndex2));
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositions, course2, dayIndex2));
//		System.out.println("MIN: " + DeltaEvaluationHillClimber.getTotalMinWorkingDaysPenalty(coursePositionsCopyCourseId, course2, dayIndex1));
//	
//		System.out.println("");
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solution, course1, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent));
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solutionCopyCourseId, course1, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent));
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solution, course2, scheduleIndex2, dayIndex2, periodIndex2, checkAdjacent));
//		System.out.println("Comp: " + DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solutionCopyCourseId, course2, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent));
//		System.out.println("");
//		
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositions, course1));
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositionsCopyCourseId, course1));
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositions, course2));
//		System.out.println("Stab: " + DeltaEvaluationHillClimber.getTotalRoomStability(coursePositionsCopyCourseId, course2));
//		System.out.println("");
	
	}
	
	

	// Get the new updated rooms swap for solution
	public ArrayList<Schedule[][]> getSolutionCopyCourseId() {
		return this.solutionCopyCourseId;
	}
	
	// Get the new updated rooms swap for coursePosition
	public ArrayList<ArrayList<TimeSlot>> getCoursePositionsCopyCourseId() {
		return this.coursePositionsCopyCourseId;
	}
	
	//Overall delta for Room
	public double getDeltaCourse() {
		return this.DeltaCourse;
	}
	
	
	
	
	/* Method for bothSwap
	 * -----------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------
	 */

	
	// Function for only swapping rooms
	public void addOnlySwap(int scheduleIndex1, int dayIndex1, int periodIndex1,
			ArrayList<ArrayList<TimeSlot>> coursePositions, ArrayList<Schedule[][]> solution,
			int unscheduledCourses, int remainingUnscheduled) {
		/* 1 indicates the first timeslot in the solution we want to swap, where number 2 indicates 
		the last timeslot in the solution. */
		Schedule[][] schedule1 = solution.get(scheduleIndex1);
		//Schedule[][] scheduleEmpty = solutionCopyAddOnly.get(scheduleIndex1);
		
		//Get roomId for both rooms
		String courseId1 = schedule1[periodIndex1][dayIndex1].getCourseId();
		String roomId = schedule1[periodIndex1][dayIndex1].getRoomId();
		
		Course course1 = ReadData.getCourseByIdentifier(courseId1);
		//Course course2 = ReadData.getCourseByIdentifier(courseId2);
		Room room1 = new Room();
		// Get roomIndex
		int numberOfRooms = ReadData.getBasicData().getRooms();
		for(int roomIndex = 0; roomIndex < numberOfRooms; roomIndex++){
			String tmpRoomId = ReadData.getRooms().get(roomIndex).getRoomId();
			if(roomId.equals(tmpRoomId)){
				room1.setCapacity(ReadData.getRooms().get(roomIndex).getCapacity());
				room1.setRoomId(tmpRoomId); //or room1.setRoomId(roomId1)
			}
		}
		
		int checkAdjacent = 0;
		
		double addOnlyUnscheduled = DeltaEvaluation.getTotalUnscheduledPenalty(unscheduledCourses, remainingUnscheduled);
		double addOnlyCapacity = DeltaEvaluation.getTotalRoomCapacityPenalty(course1, room1);
		double addOnlyMinimum = DeltaEvaluation.getTotalMinWorkingDaysPenalty(coursePositions, course1, dayIndex1);
		double addOnlyStability = DeltaEvaluation.getTotalRoomStability(coursePositions, course1);
		double addOnlyEmptyAfter = DeltaEvaluation.getTotalCurriculumCompactnessPenalty(solution, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
		
		schedule1[periodIndex1][dayIndex1].setCourseId("");
		

		schedule1[periodIndex1][dayIndex1].setCourseId("");
		double addOnlyEmptyBefore = DeltaEvaluation.getTotalCurriculumCompactnessPenalty(solution, scheduleIndex1, dayIndex1, periodIndex1, 1);
		//double comp2After = DeltaEvaluationHillClimber.getTotalCurriculumCompactnessPenalty(solutionCopyAddOnly, scheduleIndex1, dayIndex1, periodIndex1, checkAdjacent);
		schedule1[periodIndex1][dayIndex1].setCourseId(courseId1);
		this.DeltaAddOnly = addOnlyUnscheduled + addOnlyCapacity + addOnlyMinimum + addOnlyStability + addOnlyEmptyAfter - addOnlyEmptyBefore;
		
		
		
	}
	
	
	// Get the new updated rooms swap for solution
	public ArrayList<Schedule[][]> getSolutionCopyAddOnly() {
		return this.solutionCopyAddOnly;
	}
	
	// Get the new updated rooms swap for coursePosition
	public ArrayList<ArrayList<TimeSlot>> getCoursePositionsCopyAddOnly() {
		return this.coursePositionsAddOnly;
	}
	
	//Overall delta for Room
	public double getDeltaAddOnly() {
		return this.DeltaAddOnly;
	}
	
	
	/*
	 * 	// To test this code insert the follwing in e.g. the class NaiveSolcer and it
	 * will makke a random change of timeslot 6,5,0 and 6,5,1
		Swap course = new Swap();
		course.courseSwap(6, 6, 0, 1, 5, 5, coursePositions, solution, unscheduledCourses);
		coursePositions = course.getCoursePositionsCopyCourseId();
		solution = course.getSolutionCopyCourseId();
		double Delta = course.getDeltaRoom();

//		Solution.printSolutionToConsole(solution);
//		TimeSlot.printCoursePositions(coursePositions);

		
		System.out.println("Detla: " + Delta);
	 */

}
