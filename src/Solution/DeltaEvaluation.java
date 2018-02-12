package Solution;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Data.Course;
import Data.ReadData;
import Data.Relations;
import Data.Room;


public class DeltaEvaluation {
	
//	// Define objects present in the objective function
	private static final double unscheduledPenaltyFactor = 10;
	private static final double roomCapacityPenaltyFactor = 1;
	private static final double minWorkingDaysPenaltyFactor = 5;
	private static final double curriculumCompactnessPenaltyFactor = 2;
	private static final double roomStabilityPenaltyFactor = 1;
	
	private static double unscheduledPenalty;
	private static double roomCapacityPenalty;
	private static double minWorkingDaysPenalty;
	private static double curriculumCompactnessPenalty;
	private static double roomStabilityPenalty;
	private static double totalPenalty;
	
	
	public static double getTotalAfterChangeCost(ArrayList<Schedule[][]> solution, int unscheduledCoursesBeforeMove, 
			int uncheduledCoursesAfterMove, ArrayList<ArrayList<TimeSlot>> coursePositions,
			Course course, Room room, int scheduleIndex, int dayIndex, int periodIndex, int checkAdjacent) {

		unscheduledPenalty = getTotalUnscheduledPenalty(unscheduledCoursesBeforeMove, uncheduledCoursesAfterMove);
		roomCapacityPenalty = getTotalRoomCapacityPenalty(course, room);
		minWorkingDaysPenalty = getTotalMinWorkingDaysPenalty(coursePositions, course, dayIndex);
		curriculumCompactnessPenalty = getTotalCurriculumCompactnessPenalty(solution, scheduleIndex, dayIndex, periodIndex, checkAdjacent);
		roomStabilityPenalty = getTotalRoomStability(coursePositions, course);
		
		
		totalPenalty = unscheduledPenalty + roomCapacityPenalty + minWorkingDaysPenalty + 
				curriculumCompactnessPenalty + roomStabilityPenalty;
	
		return totalPenalty;
	}
	
	
	public static void printDeltaEvaluationCosts(PrintWriter outputFile) {
		
		outputFile.println("---------------- Delta Evaluation Report ----------------");
		outputFile.println("Delta eval. objective change: " + totalPenalty);
		outputFile.println("Delta unscheduled penalty = " + unscheduledPenalty);
		outputFile.println("Delta room capacity penalty = " + roomCapacityPenalty);
		outputFile.println("Delta min working days penalty = " + minWorkingDaysPenalty);
		outputFile.println("Delta curriculum compact. penalty = " + curriculumCompactnessPenalty);
		outputFile.println("Delta room stability penalty = " + roomStabilityPenalty);
		outputFile.println();
		
	}


	/**
	 * Calculates the total penalty of unscheduled courses.
	 * @param unscheduledCoursesBeforeMove Number of unscheduled courses before the move.
	 * @param uncheduledCoursesAfterMove Number of unscheduled courses after the move. 
	 * @return unscheduledPenaltyChange (Negative if improved, positive if worse, 0 if unchanged) 
	 */
	public static double getTotalUnscheduledPenalty(int unscheduledCoursesBeforeMove, int uncheduledCoursesAfterMove) {
		
		double unscheduledPenaltyChange = (uncheduledCoursesAfterMove - unscheduledCoursesBeforeMove) * unscheduledPenaltyFactor;
		
		return unscheduledPenaltyChange;
	}
	
	
	/**
	 * Calculates the total room capacity penalty.
	 * @param course Course to be inspected.
	 * @param room Room assigned for the course.
	 * @return roomCapacityPenaltyChange (Positive if capacity of room is exceeded, 0 if there is room for everyone)
	 */
	public static double getTotalRoomCapacityPenalty(Course course, Room room) {
		
		double roomCapacityPenaltyChange = 0;
		
		// Calculate whether the number of students assigned for the course exceed room capacity
//		System.out.println("Delta:  " + course.getCourseId() + " og " + room.getCapacity());
		double capacityShortage = course.getStudents() - room.getCapacity();
		if (capacityShortage > 0) {
			roomCapacityPenaltyChange = roomCapacityPenaltyFactor * capacityShortage;
		}
		
		return roomCapacityPenaltyChange;	
	}
	
	
	public static double getTotalMinWorkingDaysPenalty(ArrayList<ArrayList<TimeSlot>> coursePositions, Course course, int dayIndex) {
		
		ArrayList<Integer> storeDays=new ArrayList<Integer>();
		ArrayList<Integer> storeDaysNew=new ArrayList<Integer>();
		String courseId=course.getCourseId();
		int courseNumber= Integer.parseInt(courseId.substring(1,courseId.length()));
		
		for (int i = 0; i < coursePositions.get(courseNumber).size(); i++) {
			storeDays.add(coursePositions.get(courseNumber).get(i).day);
			storeDaysNew.add(coursePositions.get(courseNumber).get(i).day);
		}
		for (int i = 0; i < storeDays.size() ; i++) {
			if (storeDays.get(i).equals(dayIndex)) {
				storeDays.remove(i);
				break;
			}
		}
		
		
		Set<Integer> Tmp = new HashSet<Integer>(storeDays);
		storeDays.clear();
		storeDays.addAll(Tmp);
		Tmp = new HashSet<Integer>(storeDaysNew);
		storeDaysNew.clear();
		storeDaysNew.addAll(Tmp);
		
		
		int afterChangeDays=storeDaysNew.size();
		int beforeChangeDays=storeDays.size();
		int dayExpected= course.getWorkingDays();
		if (beforeChangeDays>=dayExpected) {
			return 0;
		}
		
		
		if(afterChangeDays==beforeChangeDays){
			return 0;
		}
		
		return -minWorkingDaysPenaltyFactor;

	}
	
	
	/**
	 * Calculates the total curriculum compactness penalty change for the current timeSlot being changes as well as the timeSlot above and below the current one.
	 * @param solution Current solution containing all schedules.
	 * @param course Course added/removed at/from the specified timeSlot (scheduleIndex, periodIndex, dayIndex)
	 * @param scheduleIndex
	 * @param dayIndex
	 * @param periodIndex
	 * @return
	 */
	public static double getTotalCurriculumCompactnessPenalty(ArrayList<Schedule[][]> solution, int scheduleIndex, int dayIndex, int periodIndex, int checkAdjacent) {
		
		double totalAfterChangePenalty = 0;
		int scheduleIndexCopy = scheduleIndex;
		int solutionSize = solution.size();
		
		// If change occurs at first period
		if (periodIndex == 0) {
			if(checkAdjacent != -1 && checkAdjacent != 1){
				totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex);
			}
//			outputFile.println("calling curr. compactness case 1");
			// Only check current course and the course after it
			//System.out.println("INDEN: " + totalAfterChangePenalty);
			
			//System.out.println("IMELLEM: " + totalAfterChangePenalty);
			for(int s = 0; s < solutionSize; s++){
				scheduleIndex = s;
				totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex + 1);
			}
			//System.out.println("EFTER: " + totalAfterChangePenalty);
		}
		// If change occurs at last period
		else if (periodIndex == ReadData.basicData.getPeriods()-1) {
			if(checkAdjacent != -1 && checkAdjacent != 1){
				totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex);
			}
//			outputFile.println("calling curr. compactness case 2");
			// Only check current course and the previous one
			for(int s = 0; s < solutionSize; s++){
				scheduleIndex = s;
				totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex - 1);
			}
		}
		// If none of the two cases above are correct -> check current course and the one above & below
		else if (periodIndex != 0 && periodIndex != ReadData.basicData.getPeriods()-1) {
			
			//if(checkAdjacent != 1){
				for(int s = 0; s < solutionSize; s++){
					scheduleIndex = s;
					totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex - 1);
				}
			//}
			//if(checkAdjacent != -1){
				for(int s = 0; s < solutionSize; s++){
					scheduleIndex = s;
					totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex + 1);
				}
			//}
//			outputFile.println("calling curr. compactness case 3");
			//System.out.println("Inden: " + totalAfterChangePenalty);
			
			//System.out.println("Imellem1: " + totalAfterChangePenalty);
			if(checkAdjacent != -1 && checkAdjacent != 1){
				scheduleIndex = scheduleIndexCopy;
				totalAfterChangePenalty += curriculumCompactnessOfCourseAtPosition(solution, scheduleIndex, dayIndex, periodIndex);
			}
			//System.out.println("Imellem2: " + totalAfterChangePenalty);
			
			//System.out.println("EFTER: " + totalAfterChangePenalty);
		}
		
		
		return totalAfterChangePenalty;
		
	}
	
	
	
	public static double curriculumCompactnessOfCourseAtPosition(ArrayList<Schedule[][]> solution, int scheduleIndex, int dayIndex, int periodIndex) {
		
		double penalty = 0;
		Boolean below = true;
		Boolean above = true;
		
		// Import RelationsArrayListByCourses & RelationsArrayListByCurricula 		
		ArrayList<ArrayList<Relations>> relationsByCourse = ReadData.getRelationsArrayListByCourses();
//		ArrayList<ArrayList<Relations>> relationsByCurricula = ReadData.getRelationsArrayListByCurricula();
		Schedule[][] scheduleIfEmpty = solution.get(scheduleIndex);
		
		// Identify curriculum of course
		// If the current timeslot is empty return penalty=0, since no need to check for penalty
		if(scheduleIfEmpty[periodIndex][dayIndex].getCourseId().equals("")){
			return 0;
		}
		String courseIdCurrent = scheduleIfEmpty[periodIndex][dayIndex].getCourseId();
		int courseNumber = Integer.parseInt(courseIdCurrent.substring(1, courseIdCurrent.length()));
		int courseCurrentCurriculumSize = relationsByCourse.get(courseNumber).size(); 
		
	
		
// The first main loop is to check for the position in timeslot periodIndex = 0	
		// periodIndex == 0 || 
		if(periodIndex == 0){
			int newPeriodIndexAbove = periodIndex+1;
			
			label4:
			for(int nns = 0; nns < courseCurrentCurriculumSize; nns++){
				String curriculumIdCurrent = relationsByCourse.get(courseNumber).get(nns).getCurriculumId();
					
				for (int s = 0; s < solution.size(); s++){ // Checks for each schedule
					Schedule[][] schedule = solution.get(s);
							
					// If above timeslot is empty --> gives penalty
					if(schedule[newPeriodIndexAbove][dayIndex].getCourseId().equals("")){ // If the above timeslot is epmty, return false
						above = false;
					}
					// Checks if the course above has same curriculum as the current course
					else if(!(schedule[newPeriodIndexAbove][dayIndex].getCourseId().equals(""))){
						//System.out.println("VI PRITER IGE: " + newPeriodIndexAbove + " og day " + dayIndex);
						String courseIdAbove = schedule[newPeriodIndexAbove][dayIndex].getCourseId();
						//System.out.println("CourseId : " + courseIdAbove);
						int courseNumberAbove = Integer.parseInt(courseIdAbove.substring(1, courseIdAbove.length()));
						int courseAboveCurriculumSize = relationsByCourse.get(courseNumberAbove).size();
						//System.out.println("Current number: " + courseNumber + " above number " +  courseNumberAbove);
						for(int ns = 0; ns < courseAboveCurriculumSize; ns++){
							String curriculumIdAbove = relationsByCourse.get(courseNumberAbove).get(ns).getCurriculumId();
							//System.out.println("PRINTID Current: " + curriculumIdCurrent + " og above " + curriculumIdAbove);
							if(curriculumIdCurrent.equals(curriculumIdAbove)){
							
							above = true;
							continue label4;
							}
							else if (!(curriculumIdCurrent.equals(curriculumIdAbove))){
							above = false;
							}
						}
					}
				}
				if(above == false){
					penalty++;
				}
			}
		}
		
		
		
		
// The second main llop check for the position in timeslot periodIndex = lastPosition
		// periodIndex == (ReadData.getBasicData().getPeriods()-1) || 
		if(periodIndex == (ReadData.getBasicData().getPeriods()-1)){
			int newPeriodIndexBelow = periodIndex-1;
			label5:
			for(int nns = 0; nns < courseCurrentCurriculumSize; nns++){
				String curriculumIdCurrent = relationsByCourse.get(courseNumber).get(nns).getCurriculumId();
					
					for (int s = 0; s < solution.size(); s++){ // Checks for each schedule
						Schedule[][] schedule = solution.get(s);
							
						// If above timeslot is empty --> gives penalty
						if(schedule[newPeriodIndexBelow][dayIndex].getCourseId().equals("")){ // If the above timeslot is epmty, return false
							below = false;
						}
						// Checks if the course above has same curriculum as the current course
						else if(!(schedule[newPeriodIndexBelow][dayIndex].getCourseId().equals(""))){
							String courseIdBelow = schedule[newPeriodIndexBelow][dayIndex].getCourseId();
							int courseNumberBelow = Integer.parseInt(courseIdBelow.substring(1, courseIdBelow.length()));
							int courseBelowCurriculumSize = relationsByCourse.get(courseNumberBelow).size();
//							System.out.println("Above Size: " + courseAboveCurriculumSize);
//							System.out.println("Current Size: " + courseCurrentCurriculumSize);
							for(int ns = 0; ns < courseBelowCurriculumSize; ns++){
								String curriculumIdBelow = relationsByCourse.get(courseNumberBelow).get(ns).getCurriculumId();
							if(curriculumIdCurrent.equals(curriculumIdBelow)){
								below = true;
								continue label5;
							}
							else if (!(curriculumIdCurrent.equals(curriculumIdBelow))){
								below = false;
							}
						}
					}
				}
				if(below == false){
				penalty++;
				
				}
			}
		}

		
		// The first main loop is to check for the position in timeslot periodIndex = 0	
				// periodIndex == 0 || 
		if(!(periodIndex == (ReadData.getBasicData().getPeriods()-1)) && !(periodIndex == 0)){
			int newPeriodIndexAbove = periodIndex+1;
			int newPeriodIndexBelow = periodIndex-1;
			
			label6:
			for(int nns = 0; nns < courseCurrentCurriculumSize; nns++){
				String curriculumIdCurrent = relationsByCourse.get(courseNumber).get(nns).getCurriculumId();
					
				for (int s = 0; s < solution.size(); s++){ // Checks for each schedule
					Schedule[][] schedule = solution.get(s);
							
					// If above timeslot is empty --> gives penalty
					if(schedule[newPeriodIndexAbove][dayIndex].getCourseId().equals("") && 
							schedule[newPeriodIndexBelow][dayIndex].getCourseId().equals("")){ // If the above timeslot is epmty, return false
						above = false;
					}
					// Checks if the course above has same curriculum as the current course
					else if(!(schedule[newPeriodIndexAbove][dayIndex].getCourseId().equals("")) || 
							!(schedule[newPeriodIndexBelow][dayIndex].getCourseId().equals(""))){
						//System.out.println("VI PRITER IGE: " + newPeriodIndexAbove + " og day " + dayIndex);
						String courseIdAbove = schedule[newPeriodIndexAbove][dayIndex].getCourseId();
						//System.out.println("CourseId : " + courseIdAbove);
						if(!(courseIdAbove == "")){
							
						
						int courseNumberAbove = Integer.parseInt(courseIdAbove.substring(1, courseIdAbove.length()));
						int courseAboveCurriculumSize = relationsByCourse.get(courseNumberAbove).size();
						//System.out.println("Current number: " + courseNumber + " above number " +  courseNumberAbove);
						for(int ns = 0; ns < courseAboveCurriculumSize; ns++){
							String curriculumIdAbove = relationsByCourse.get(courseNumberAbove).get(ns).getCurriculumId();
							//System.out.println("PRINTID Current: " + curriculumIdCurrent + " og above " + curriculumIdAbove);
							if(curriculumIdCurrent.equals(curriculumIdAbove)){
							
							above = true;
							continue label6;
							}
							else if (!(curriculumIdCurrent.equals(curriculumIdAbove))){
							above = false;
							}
						}
						}
						
						String courseIdBelow = schedule[newPeriodIndexBelow][dayIndex].getCourseId();
						if(!(courseIdBelow == "")){
							
						int courseNumberBelow = Integer.parseInt(courseIdBelow.substring(1, courseIdBelow.length()));
						int courseBelowCurriculumSize = relationsByCourse.get(courseNumberBelow).size();
//						System.out.println("Above Size: " + courseAboveCurriculumSize);
//						System.out.println("Current Size: " + courseCurrentCurriculumSize);
						for(int ns = 0; ns < courseBelowCurriculumSize; ns++){
							String curriculumIdBelow = relationsByCourse.get(courseNumberBelow).get(ns).getCurriculumId();
							if(curriculumIdCurrent.equals(curriculumIdBelow)){
								below = true;
								continue label6;
							}
							else if (!(curriculumIdCurrent.equals(curriculumIdBelow))){
								below = false;
							}
						}
						}
					}
				}
				if(above == false){
				penalty++;
				}
			}
		}		
		
//		if(above == false && below == false){
//			penalty = courseCurrentCurriculumSize * penalty;
//		}
		//System.out.println("NUI: " + penalty);

		return penalty*curriculumCompactnessPenaltyFactor;
		
	}
	
	
	
	public static double getTotalRoomStability(ArrayList<ArrayList<TimeSlot>> coursePositions, Course course) {
		
		double roomStabilityPenalty = 0;
		// Define arrayList storing all room numbers of the course
		ArrayList<String> roomsUsed = new ArrayList<String>();
		
		int courseNumber = Integer.parseInt(course.getCourseId().substring(1, course.getCourseId().length()));
		int sizeCourseNumber = coursePositions.get(courseNumber).size(); // Finds out how many courses already are scheduled
		
		// Runs through each course to check for different rooms
		label:
		for(int s = 0; s < sizeCourseNumber; s++){
			String roomNow = coursePositions.get(courseNumber).get(s).getRoomId(); // Our current room
			int sizeRoomsUsed = roomsUsed.size(); // The size of the array with already existing rooms
			
			// For the first course the arraylist is empty, so we just add the first room
			if(s == 0){
				roomsUsed.add(roomNow);
				continue label;
			}

			// Runs thourgh the arraylist with existing rooms to check if 1 of them is equal to the room we want to add
			for(int k = 0; k < (sizeRoomsUsed); k++){
				if(roomNow.equals(roomsUsed.get(k))){ // If there is a a match set check = true
					//check = true;
					continue label;
				}
			}
		// If there was not match, we can add the course to our arraylist
			roomsUsed.add(roomNow);
		}
		
		roomStabilityPenalty = roomsUsed.size()-1; //The size of the arraylist indicates the number of different rooms
		
		return roomStabilityPenaltyFactor*roomStabilityPenalty;
		
	}
	
}	
