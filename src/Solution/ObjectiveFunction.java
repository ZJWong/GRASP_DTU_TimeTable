package Solution;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import Data.Basic;
import Data.Course;
import Data.ReadData;


public class ObjectiveFunction {
	
	// Define objects present in the objective function
	private final double unscheduledPenaltyFactor = 10;
	private final double roomCapacityPenaltyFactor = 1;
	private final double minWorkingDaysPenaltyFactor = 5;
	private final double curriculumCompactnessPenaltyFactor = 2;
	private final double roomStabilityPenaltyFactor = 1;
	
	private static double totalPenalty;
	private static double unscheduledPenalty;
	private static double roomCapacityPenalty;
	private static double minWorkingDaysPenalty;
	private static double curriculumCompactnessPenalty;
	private static double roomStabilityPenalty;

	
	
	 public double getTotalCost(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions) {
		unscheduledPenalty = getTotalUnscheduledPenalty(solution);
		roomCapacityPenalty = getTotalRoomCapacityPenalty(solution);
		minWorkingDaysPenalty = getTotalMinWorkingDaysPenalty(solution, coursePositions);
		curriculumCompactnessPenalty = getTotalCurriculumCompactnessPenalty(solution);
		roomStabilityPenalty = getTotalRoomStability(solution, coursePositions);
		
		totalPenalty = unscheduledPenalty + roomCapacityPenalty + minWorkingDaysPenalty + 
				curriculumCompactnessPenalty + roomStabilityPenalty;
			
		return totalPenalty;
	}
	
	 
	 public static void printObjectiveFunctionCostsToConsole() {
			
			System.out.println();
			System.out.println("------ Objective Function Report ------");
			System.out.println("OBJECTIVE: \t\t\t" + totalPenalty);
			System.out.println("UNSCHEDULED: \t\t\t" + unscheduledPenalty);
			System.out.println("ROOM CAPACITY: \t\t\t" + roomCapacityPenalty);
			System.out.println("MIN WORKING DAYS: \t\t" + minWorkingDaysPenalty);
			System.out.println("CURRICULUM COMPACTNESS: \t" + curriculumCompactnessPenalty);
			System.out.println("ROOM STABILITY: \t\t" + roomStabilityPenalty);
			System.out.println();
			
	}
		
		
	public static void printObjectiveFunctionCostsToFile(PrintWriter outputFile) {
			
		outputFile.println("------ Objective Function Report ------");
		outputFile.println("OBJECTIVE:                 " + totalPenalty);
		outputFile.println("UNSCHEDULED:               " + unscheduledPenalty);
		outputFile.println("ROOM CAPACITY:             " + roomCapacityPenalty);
		outputFile.println("MIN WORKING DAYS:          " + minWorkingDaysPenalty);
		outputFile.println("CURRICULUM COMPACTNESS:    " + curriculumCompactnessPenalty);
		outputFile.println("ROOM STABILITY:            " + roomStabilityPenalty);
		outputFile.println();
			
	}
	
	
	/**
	 * Used for parameter tuning.
	 * @param outputFile CSV file.
	 */
	public static void printObjectiveFunctionValueOnlyToFile(PrintWriter outputFile) {
		
		outputFile.print(totalPenalty);

	}
	 
	
	/**
	 * @author Christoph
	 * Calculates the total penalty associated with unscheduled courses (lectures)
	 * @param solution Current solution.
	 * @return penalty Penalty of unscheduled courses. 
	 */
	private double getTotalUnscheduledPenalty(ArrayList<Schedule[][]> solution) {
		
		unscheduledPenalty = 0;
		int numberViolations = 0;
		
		int numberOfCourses = ReadData.getBasicData().getCourses();
		int days = ReadData.getBasicData().getDays();
		int period = ReadData.getBasicData().getPeriods();
		int size = solution.size();
		
		
		for(int count=0; count<numberOfCourses; count++){
			String tmpCourseId = ReadData.getCourses().get(count).getCourseId(); //Stores the CourseId for a Course - specified by the counter
			Course courses = ReadData.getCourseByIdentifier(tmpCourseId); // Number of Lectures a Course has to be taught
			int numberLectureIdeal = courses.getLectures();
			int numberLectureReal = 0;
			label2:
			for(int s = 0; s < size; s++){
				Schedule[][] schedule1 = solution.get(s);
				for(int d = 0; d < days; d++){

					for(int p = 0; p < period; p++){
						if(schedule1[p][d].getCourseId().isEmpty()){ 
							continue;
						}
						if(schedule1[p][d].getCourseId().equals(tmpCourseId) ){
							numberLectureReal++;
						}
						if(numberLectureReal == numberLectureIdeal){
							break label2;
						}
					}
				}		
			}
			if(numberLectureReal != numberLectureIdeal){
				numberViolations += numberLectureIdeal - numberLectureReal;
			}
		}

		unscheduledPenalty = unscheduledPenaltyFactor * numberViolations;
		
		return unscheduledPenalty;
	}
	
	
	/**
	 * @author Christoph
	 * Calculates the total room capacity penalty.
	 * @param solution Current solution.
	 * @return penalty Room capacity penalty
	 */
	private double getTotalRoomCapacityPenalty(ArrayList<Schedule[][]> solution) {
		
		roomCapacityPenalty = 0;
		int numberViolations = 0;

		int days = ReadData.getBasicData().getDays();
		int period = ReadData.getBasicData().getPeriods();
		int numberOfRooms = ReadData.getBasicData().getRooms();
		int size = solution.size(); // Number of schedules 
		
		for(int s = 0; s < size; s++){ //First loop goes through the schedules
			Schedule[][] schedule1 = solution.get(s);
			for(int d = 0; d < days; d++){ // Second loop goes through days
				for(int p = 0; p < period; p++){ // Third loop goes through periods
					if(schedule1[p][d].getCourseId().isEmpty()){ //the right way to do this?
						continue;						
					}
					String tmpCourseId = schedule1[p][d].getCourseId();
					Course courses = ReadData.getCourseByIdentifier(tmpCourseId);
					int students = courses.getStudents();
					String tmpRoomId = schedule1[p][d].getRoomId();
					for(int r = 0; r < numberOfRooms; r++){
						String roomLookUpId = ReadData.getRooms().get(r).getRoomId();
						if(tmpRoomId.equals(roomLookUpId)){
							int roomCapacity = ReadData.getRooms().get(r).getCapacity();
							if(students > roomCapacity){
								numberViolations += (students-roomCapacity);
							}
						}		
					}
				}	
			}
		}

		roomCapacityPenalty = roomCapacityPenaltyFactor * numberViolations;
		
		return roomCapacityPenalty;
	}
	
	

	/**
	 * @author Zejun
	 * Calculates the total minimum working days penalty. 
	 * @param solution Current solution.
	 * @param coursePositions ArrayList with courses and their timeSlots and rooms assigned.
	 * @return penalty Total penaly.
	 */
	private double getTotalMinWorkingDaysPenalty(ArrayList<Schedule[][]> solution,ArrayList<ArrayList<TimeSlot>> coursePositions) {
		
		minWorkingDaysPenalty=0;
		for (int i = 0; i < ReadData.getBasicData().getCourses(); i++) {
			int daysexpected = ReadData.getCourses().get(i).getWorkingDays();
	
			ArrayList<Integer> day = new ArrayList<Integer>();
		if (coursePositions.get(i).size()==0) {
			minWorkingDaysPenalty+=daysexpected*minWorkingDaysPenaltyFactor;
			continue;
		}	
			for (int j = 0; j <coursePositions.get(i).size(); j++){
				day.add(coursePositions.get(i).get(j).getDay());
				}
			Set<Integer> Tmp = new HashSet<Integer>(day);
			day.clear();
			day.addAll(Tmp);
			if (day.size()>=daysexpected) {
				continue;
			}
			minWorkingDaysPenalty=minWorkingDaysPenalty+(daysexpected-day.size())*minWorkingDaysPenaltyFactor;
		}		

		return minWorkingDaysPenalty;

		
	}
	
	
	/**
	 * @author Zejun
	 * Calculates the total curriculum compactness penalty.
	 * @param solution Current solution
	 * @return penalty Total penalty.
	 */
	private double getTotalCurriculumCompactnessPenalty(ArrayList<Schedule[][]> solution) {
		//scan every course appear in the schedule and if there is a course near it that has the same curruculum there will be no penalty
				
		curriculumCompactnessPenalty=0;
				Basic basic = ReadData.getBasicData();
//				for (int i = 0; i <basic.getCourses() ; i++) {
//					penalty += curriculumCompactnessPenalty*ReadData.getCourses().get(i).getLectures();
//				}
				int numOfsolution= solution.size();
				for (int j = 0; j < basic.getDays(); j++) {
					for (int j2 = 0; j2 < basic.getPeriods(); j2++) {					

						for (int k = 0; k < numOfsolution; k++) {
//							
							Schedule[][] schedule = solution.get(k);
							String courseId=schedule[j2][j].getCourseId();
							if (courseId =="") {
								continue;
							}
							int coursenumber= Integer.parseInt(courseId.substring(1));
							ArrayList<String> allCurriculumId=getAllCurriculumId(solution,courseId,j,j2);
							ArrayList<String> targetCurriculumId= new ArrayList<String>();
							for (int i = 0; i <ReadData.getRelationsArrayListByCourses().get(coursenumber).size() ; i++) {
								targetCurriculumId.add(ReadData.getRelationsArrayListByCourses().get(coursenumber).get(i).getCurriculumId());
							}
							label2:
							for (int i = 0; i < targetCurriculumId.size(); i++) {
								for (int l = 0; l < allCurriculumId.size(); l++) {
									if (targetCurriculumId.get(i).equals(allCurriculumId.get(l))) {
										continue label2;
									}
									
									
								}
								curriculumCompactnessPenalty++;
//								System.out.println(courseId+" "+j+" "+j2+" "+k);
//								System.out.println(allCurriculumId);
//								System.out.println(targetCurriculumId);
							}
						
						}
					
					}
					}
				
				curriculumCompactnessPenalty=curriculumCompactnessPenalty*curriculumCompactnessPenaltyFactor;		
				
				return curriculumCompactnessPenalty;

			}


	
	/**
	 * get all the curriculum id of one course, returns array list contains all the id
	 * @param solution current solution 
	 * @param courseId target course id
	 * @param day day of the target course
	 * @param period period of the target course
	 * @return Array list contains all the curriculum id
	 */

		private ArrayList<String> getAllCurriculumId (ArrayList<Schedule[][]> solution, String courseId,int day,int period) {
			Basic basic = ReadData.getBasicData();
			ArrayList<String> allCurriculumId= new ArrayList<String>();
			if (period!=0&&period<basic.getPeriods()-1) {
			for (int i = 0; i <solution.size() ; i++) {
				Schedule[][] schedule = solution.get(i);
				String courseId1=schedule[period-1][day].getCourseId();
				String courseId2=schedule[period+1][day].getCourseId();
				if (courseId1!="") {
				
				
				int coursenumber1= Integer.parseInt(courseId1.substring(1));
				for (int j = 0; j < ReadData.getRelationsArrayListByCourses().get(coursenumber1).size(); j++) {
					allCurriculumId.add(ReadData.getRelationsArrayListByCourses().get(coursenumber1).get(j).getCurriculumId());
					}
				}
				if (courseId2!="") {
				int coursenumber2= Integer.parseInt(courseId2.substring(1));
				for (int j = 0; j < ReadData.getRelationsArrayListByCourses().get(coursenumber2).size(); j++) {
					allCurriculumId.add(ReadData.getRelationsArrayListByCourses().get(coursenumber2).get(j).getCurriculumId());
					}
				}
			}
		} else if(period==0) {
			for (int i = 0; i <solution.size() ; i++) {
				Schedule[][] schedule = solution.get(i);
				String courseId1=schedule[period+1][day].getCourseId();
				if (courseId1!="") {
					int coursenumber1= Integer.parseInt(courseId1.substring(1));
				for (int j = 0; j < ReadData.getRelationsArrayListByCourses().get(coursenumber1).size(); j++) {
					allCurriculumId.add(ReadData.getRelationsArrayListByCourses().get(coursenumber1).get(j).getCurriculumId());
					}
				}
			}
		}
		else{
			for (int i = 0; i <solution.size() ; i++) {
				Schedule[][] schedule = solution.get(i);
				String courseId1=schedule[period-1][day].getCourseId();
				if (courseId1!="") {
				int coursenumber1= Integer.parseInt(courseId1.substring(1));
				for (int j = 0; j < ReadData.getRelationsArrayListByCourses().get(coursenumber1).size(); j++) {
					allCurriculumId.add(ReadData.getRelationsArrayListByCourses().get(coursenumber1).get(j).getCurriculumId());
					}
				}
				}
		}
		return allCurriculumId;
	}

		
	/**
	 * @author Christoph
	 * Calculates the total room instability penalty.
	 * @param solution Current solution.
	 * @param coursePositions Double arrayList of all lectures. The order of courses
	 * in the arrayLists is based on course numbers (0 indicates the first course from courseFile).
	 * @return penalty The total penalty associated with unstable room assignments.
	 */
	private double getTotalRoomStability(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions) {
		
		roomStabilityPenalty = 0;
		int numberViolations = 0;
		
		int numberCourses = coursePositions.size();
		int rooms = ReadData.getBasicData().getRooms();
		
		for(int count = 0; count < numberCourses; count++){
			int numberCol = coursePositions.get(count).size();
			int diffRooms = 0;
			label:
			for(int r = 0; r < rooms; r++){
				String roomLookUpId = ReadData.getRooms().get(r).getRoomId();
				//label:
				for(int countCol = 0; countCol < numberCol; countCol++){
					TimeSlot currentTimeSlot = coursePositions.get(count).get(countCol);
					if(roomLookUpId.equals(currentTimeSlot.getRoomId()) ){
						diffRooms++;
						continue label;
					}
				}
			}
			numberViolations += (diffRooms-1);
		}
		
		roomStabilityPenalty = roomStabilityPenaltyFactor * numberViolations;

		return roomStabilityPenalty;
	}

}
