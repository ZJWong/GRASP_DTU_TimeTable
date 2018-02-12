package Solver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import Data.Basic;
import Data.Course;
import Data.ReadData;
import Data.Room;
import Solution.HardConstraints;
import Solution.ObjectiveFunction;
import Solution.ObjectiveFunction;
import Solution.Schedule;
import Solution.Solution;
import Solution.Swap;
import Solution.TimeSlot;

public class HillClimber implements Solver {
//	private ArrayList<ArrayList<TimeSlot>> coursePositions = new ArrayList<ArrayList<TimeSlot>>();
//	private ArrayList<Schedule[][]> solution = new ArrayList<Schedule[][]>();
//	private long runlength;
//	public HillClimber(ArrayList<ArrayList<TimeSlot>> coursePositions, ArrayList<Schedule[][]> solution,long runlength){
//		this.coursePositions = coursePositions;
//		this.solution = solution;
//		this.runlength = runlength;
//		
//		
//	}

//	public ArrayList<Schedule[][]> solution;
//	public ArrayList<ArrayList<TimeSlot>> coursePositions;
//	public ObjectiveFunction objFunc;
//	ArrayList<Course> unscheduledCourses;

	
	
//	public HillClimber(ArrayList<Schedule[][]> solution, 
//			ArrayList<ArrayList<TimeSlot>> coursePositions, ObjectiveFunction objFunc, ArrayList<Course> unscheduledCourses){
//		this.coursePositions=coursePositions;
//		this.solution= solution;
//		this.objFunc= objFunc;
//		this.unscheduledCourses=unscheduledCourses;
//	}
	

	@Override
	public void solve(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions, 
			ArrayList<Course> unscheduledCourses, 
			ArrayList<Room> rooms, Basic basicData, PrintWriter outputFile) throws FileNotFoundException{
		
		ArrayList<Schedule[][]> solutionCopy = new ArrayList<Schedule[][]>();
		ArrayList<ArrayList<TimeSlot>> coursePositionsCopy = setCoursePositions(ReadData.getCourses().size());		//		 	Set the time limitation
		ArrayList<Schedule[][]> solutionCopyRemove = new ArrayList<Schedule[][]>();
		ArrayList<ArrayList<TimeSlot>> coursePositionsCopyRemove = setCoursePositions(ReadData.getCourses().size());		//		 	Set the time limitation
		ArrayList<Schedule[][]> solutionNew = new ArrayList<Schedule[][]>();
		ArrayList<ArrayList<TimeSlot>> coursePositionsNew = setCoursePositions(ReadData.getCourses().size());		//		 	Set the time limitation
	


		
//		Date date=  new Date();
//			boolean done = false;
//		    long startTime = date.getTime();
//		    long max_sec=startTime+1000;

		    
//		    get the size of the loops	

		    
		    int days=ReadData.basicData.getDays();
		    int periods= ReadData.basicData.getPeriods();

		    
		 // Apply NaiveSolver
//			Solver solver = new NaiveSolver();
//			solver.solve(solution, coursePositions, unscheduledCourses, rooms, basicData, objFunc);
		    
			reArrange(solutionNew,coursePositionsNew,solution);
//			Solution.printSolutionToConsole(solutionNew);
			
			deepCopyForSchedule(solutionCopy,solutionNew);
			deepCopyForTimeSlot(coursePositionsCopy,coursePositionsNew);
			deepCopyForSchedule(solutionCopyRemove,solutionNew);
			deepCopyForTimeSlot(coursePositionsCopyRemove,coursePositionsNew);
			//TimeSlot.printCoursePositions(coursePositionsCopyRemove);
//		    get the current 
			ObjectiveFunction obj1 = new ObjectiveFunction();
		    double Solutionlvalue = obj1.getTotalCost(solution, coursePositions);
		    //objFunc.printObjectiveFunctionCostsToConsole();
//		    System.out.println("Objective value BEFORE : " + Solutionlvalue);
		    System.out.println();
		    		    
		    int schedules=solutionCopyRemove.size();
		    double Delta = 0;
		    Boolean test = true;
		    int count = 0;
		    int count1 = 0;
		    int iterations = 0;
		    
		    loop:
		    while(Main.Solve_UTT.runningTimeTracker(Main.Solve_UTT.getEndTime()) || test == false) //Runs while there is time
		    {
//		    	date=new Date();
		    	iterations++;

//		    	System.out.println(date.getTime());
		    	
		    	for (int course1Schedule = 0; course1Schedule < schedules; course1Schedule ++) {
		    		
					for (int course1Day = 0; course1Day < days; course1Day++) {
						
						label:
						for (int course1Period = 0; course1Period < periods; course1Period++) {
							String couseId1=solutionCopyRemove.get(course1Schedule)[course1Period][course1Day].getCourseId();
							
//							get the first course 
							if (couseId1.equals("")) {
//								if (couseId1.equals("")) {
//								continue label;
//								}
								for (int unschduledCourseNumber = 0; unschduledCourseNumber < unscheduledCourses.size(); unschduledCourseNumber++) {
									Course courseToBePut = ReadData.getCourseByIdentifier(unscheduledCourses.get(unschduledCourseNumber).getCourseId());
									String courseId=courseToBePut.getCourseId();
									Schedule[][] schedule=solutionCopyRemove.get(course1Schedule);
									String room1 = ReadData.getRooms().get(course1Schedule).getRoomId();
									HardConstraints callHardCon1 = new HardConstraints(courseId,room1 , course1Day, course1Period, coursePositionsCopyRemove, solutionCopyRemove);
									Boolean checkanswer1 = callHardCon1.hardConstraintsViolated(1);	
									
									if (checkanswer1) {
											continue label;
									}
									
									int courseNumber1 = Integer.parseInt(courseId.substring(1, courseId.length()));
									TimeSlot.addPosition(coursePositionsCopyRemove, courseToBePut , ReadData.getRooms().get(course1Schedule), course1Schedule, course1Day, course1Period);
									Schedule.addCourseOnly(schedule,courseToBePut, course1Period, course1Day);
									Swap swap2 = new Swap();
									swap2.addOnlySwap(course1Schedule, course1Day, course1Period, coursePositionsCopyRemove, solutionCopyRemove, unscheduledCourses.size(), unscheduledCourses.size()-1);
									double delta = swap2.getDeltaAddOnly();
									//double delta = 10;
									//System.out.println(delta);
									if (delta <= 0) {
										
										unscheduledCourses.remove(unschduledCourseNumber);
										Solutionlvalue = Solutionlvalue + delta;
								
										continue label;
									}
									Schedule.removeOnlyCourse(schedule, course1Period, course1Day);
									
									TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
									
									
								}	
								
								
								
								
								continue label;

							}
							
//							only when it is not null the swap can make sense
							for (int course2Schedule = 0; course2Schedule < schedules; course2Schedule++) {
																
								for (int course2Day = 0; course2Day < days; course2Day++) {
									label2:
									for (int course2Period = 0; course2Period < periods; course2Period++) {
//									get the second course
										
										if (course1Schedule!=course2Schedule || course1Day!=course2Day || course1Period!=course2Period) {
//											make sure the two courses are not the same one
											
											String couseId2=solutionCopyRemove.get(course2Schedule)[course2Period][course2Day].getCourseId();
							
											
									
											
											if(couseId2.equals("")){
												
												// Check for HardConstraints
												// First remove both courses from our solution and coursePosition deepCopy list
												Schedule[][] schedule1 = solutionCopyRemove.get(course1Schedule);
												Schedule[][] schedule2 = solutionCopyRemove.get(course2Schedule);
												String courseId1 = schedule1[course1Period][course1Day].getCourseId();
												String courseId2 = schedule2[course2Period][course2Day].getCourseId();
												String roomId1 = schedule1[course1Period][course1Day].getRoomId();
												String roomId2 = schedule2[course2Period][course2Day].getRoomId();
					
												Schedule.removeOnlyCourse(schedule1, course1Period, course1Day);
												//Schedule.removeCourse(schedule2, course2Period, course2Day);
											
												Course courses1 = ReadData.getCourseByIdentifier(courseId1);
												//Course courses2 = ReadData.getCourseByIdentifier(courseId2);
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
													
												// Update solutionCopy with the new schedule
												
												//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
												//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
												
												int courseNumber1 = Integer.parseInt(courseId1.substring(1, courseId1.length()));
												//int courseNumber2 = Integer.parseInt(courseId2.substring(1, courseId2.length()));
												TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
												//TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
												
												HardConstraints callHardCon1 = new HardConstraints(courseId1, roomId2, course2Day, course2Period, coursePositionsCopyRemove, solutionCopyRemove);
												Boolean checkanswer1 = callHardCon1.hardConstraintsViolated(1);	
												
												
												if(checkanswer1 == true){
													Delta = 10;
													
												}
//												HardConstraints callHardCon2 = new HardConstraints(courseId2, roomId1, course1Day, course1Period, coursePositionsCopyRemove, solutionCopyRemove);
//												Boolean checkanswer2 = callHardCon2.checkHardConstraints();											
//												if(checkanswer2 == false){
//													Delta = 10;
//												}
												
												TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room1, course1Schedule, course1Day, course1Period);
												//TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room2, course2Schedule, course2Day, course2Period);
												Schedule.addCourseOnly(schedule1, courses1, course1Period, course1Day);
												//Schedule.addCourseSetRoom(schedule2, courses2, room2, course2Period, course2Day);
												
												if(checkanswer1 == false){
													
													
													//TimeSlot.printCoursePositions(coursePositionsCopyRemove);
													
													Swap swap1 = new Swap();
													swap1.oneOnlySwap(course1Schedule, course2Schedule, course1Day, course2Day, course1Period, course2Period, coursePositionsCopyRemove, solutionCopyRemove, 5, 5);
													Delta = swap1.getDeltaOneOnly();
													
													//System.out.println("VI RPINTER DELTA: " + Delta);
//													the delta is a positive number or a negitive number
													}
//													System.out.println(Delta + " for " + courseId1 + " og " + courseId2 + " day1 " + course1Day
//															+ " day2 " + course2Day + " periode1 " + course1Period + " periode2 " + course2Period
//															+ " index1 " + course1Schedule + " index2 " + course2Schedule);
													//System.out.println("VI PRINTER DELTA: "+ Delta);
													if (Delta < 0) {
													
													
													Solutionlvalue = Solutionlvalue + Delta;
													
//													System.out.println("Delta = " + Delta + ": Course, room " + courseId1 + ", " + roomId1 + ", " 
//															+ course1Day + ", " + course1Period + ", " + course1Schedule + " swapped with " 
//															+ courseId2 + ", " + roomId2 + ", " + course2Day + ", " + course2Period + ", " + course2Schedule);
//															
															
													//coursePositions = swap.getCoursePositionsCopyCourseId();
													//solution = swap.getSolutionCopyCourseId();
													Schedule.removeOnlyCourse(schedule1, course1Period, course1Day);
													//Schedule.removeCourse(schedule2, course2Period, course2Day);
													TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
													//TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
													TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room2, course2Schedule, course2Day, course2Period);
													//TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room1, course1Schedule, course1Day, course1Period);
													Schedule.addCourseOnly(schedule2, courses1, course2Period, course2Day);
													//Schedule.addCourseSetRoom(schedule1, courses2, room1, course1Period, course1Day);
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
													//count++;
													if(count == 10){
														test = false;
														break loop;
													}
													
													continue label;
						
													
													
												}
						
												else if (Delta >= 0){
//													Schedule.removeCourse(schedule1, course1Period, course1Day);
//													Schedule.removeCourse(schedule2, course2Period, course2Day);
//													TimeSlot.removePosition(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
//													TimeSlot.removePosition(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
//													TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room1, course1Schedule, course1Day, course1Period);
//													TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room2, course2Schedule, course2Day, course2Period);
//													Schedule.addCourseSetRoom(schedule1, courses1, room1, course1Period, course1Day);
//													Schedule.addCourseSetRoom(schedule2, courses2, room2, course2Period, course2Day);
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
												}
												//solutionCopy = solutionCopyRemove;
												//coursePositionsCopy = coursePositionsCopyRemove;

											}	
											
												
											
											
											
											
											else if(!(couseId2.equals(""))){
												if(couseId1.equals(couseId2)){
													continue label2;
												}
												// Check for HardConstraints
												// First remove both courses from our solution and coursePosition deepCopy list
												Schedule[][] schedule1 = solutionCopyRemove.get(course1Schedule);
												Schedule[][] schedule2 = solutionCopyRemove.get(course2Schedule);
												String courseId1 = schedule1[course1Period][course1Day].getCourseId();
												String courseId2 = schedule2[course2Period][course2Day].getCourseId();
												String roomId1 = schedule1[course1Period][course1Day].getRoomId();
												String roomId2 = schedule2[course2Period][course2Day].getRoomId();
				
												Schedule.removeOnlyCourse(schedule1, course1Period, course1Day);
												Schedule.removeOnlyCourse(schedule2, course2Period, course2Day);
										
												Course courses1 = ReadData.getCourseByIdentifier(courseId1);
												Course courses2 = ReadData.getCourseByIdentifier(courseId2);
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
												
												// Update solutionCopy with the new schedule
											
												//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
												//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
											
												int courseNumber1 = Integer.parseInt(courseId1.substring(1, courseId1.length()));
												int courseNumber2 = Integer.parseInt(courseId2.substring(1, courseId2.length()));
												TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
												TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
												HardConstraints callHardCon1 = new HardConstraints(courseId1, roomId2, course2Day, course2Period, coursePositionsCopyRemove, solutionCopyRemove);
												Boolean checkanswer1 = callHardCon1.hardConstraintsViolated(1);											
												if(checkanswer1 == true){
													Delta = 10;
												
												}
												HardConstraints callHardCon2 = new HardConstraints(courseId2, roomId1, course1Day, course1Period, coursePositionsCopyRemove, solutionCopyRemove);
												Boolean checkanswer2 = callHardCon2.hardConstraintsViolated(1);											
												if(checkanswer2 == true){
													Delta = 10;
												}
											
												TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room1, course1Schedule, course1Day, course1Period);
												TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room2, course2Schedule, course2Day, course2Period);
												Schedule.addCourseOnly(schedule1, courses1, course1Period, course1Day);
												Schedule.addCourseOnly(schedule2, courses2, course2Period, course2Day);
											
												if(checkanswer2 == false && checkanswer1 == false){
												
												
													//TimeSlot.printCoursePositions(coursePositionsCopyRemove);
													Swap swap = new Swap();
													swap.courseSwap(course1Schedule, course2Schedule, course1Day, course2Day, course1Period, course2Period, coursePositionsCopyRemove, solutionCopyRemove, 5, 5);
													Delta = swap.getDeltaCourse();
												
//													the delta is a positive number or a negitive number
												}
//													System.out.println(Delta + " for " + courseId1 + " og " + courseId2 + " day1 " + course1Day
//															+ " day2 " + course2Day + " periode1 " + course1Period + " periode2 " + course2Period
//															+ " index1 " + course1Schedule + " index2 " + course2Schedule);
//											
												if (Delta < 0) {
												
												
													Solutionlvalue = Solutionlvalue + Delta;
												
//													System.out.println("Delta = " + Delta + ": Course, room " + courseId1 + ", " + roomId1 + ", " 
//															+ course1Day + ", " + course1Period + ", " + course1Schedule + " swapped with " 
//															+ courseId2 + ", " + roomId2 + ", " + course2Day + ", " + course2Period + ", " + course2Schedule);
//														
														
													//coursePositions = swap.getCoursePositionsCopyCourseId();
													//solution = swap.getSolutionCopyCourseId();
													Schedule.removeOnlyCourse(schedule1, course1Period, course1Day);
													Schedule.removeOnlyCourse(schedule2, course2Period, course2Day);
													TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
													TimeSlot.removePositionHillClimber(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
													TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room2, course2Schedule, course2Day, course2Period);
													TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room1, course1Schedule, course1Day, course1Period);
													Schedule.addCourseOnly(schedule2, courses1, course2Period, course2Day);
													Schedule.addCourseOnly(schedule1, courses2, course1Period, course1Day);
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
													//count1++;
													if(count1 == 60){
														test = false;
														break loop;
													}
												
													continue label;
					
												
												
												}
					
												else if (Delta >= 0){
//													Schedule.removeCourse(schedule1, course1Period, course1Day);
//													Schedule.removeCourse(schedule2, course2Period, course2Day);
//													TimeSlot.removePosition(coursePositionsCopyRemove, courseNumber1, course1Schedule, course1Day, course1Period);
//													TimeSlot.removePosition(coursePositionsCopyRemove, courseNumber2, course2Schedule, course2Day, course2Period);
//													TimeSlot.addPosition(coursePositionsCopyRemove, courses1, room1, course1Schedule, course1Day, course1Period);
//													TimeSlot.addPosition(coursePositionsCopyRemove, courses2, room2, course2Schedule, course2Day, course2Period);
//													Schedule.addCourseSetRoom(schedule1, courses1, room1, course1Period, course1Day);
//													Schedule.addCourseSetRoom(schedule2, courses2, room2, course2Period, course2Day);
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule1, course1Schedule); 
													//Solution.replaceScheduleInSolution(solutionCopyRemove, schedule2, course2Schedule);
												}
												//solutionCopy = solutionCopyRemove;
												//coursePositionsCopy = coursePositionsCopyRemove;
											}	
//											count++;
//											if(count == 15){
//												test = false;
//												break loop;
//											}
										}
									}
								}
							}
						}
					}
				}
		    } 
//		    Solution.printSolutionInTableToConsole(solutionCopyRemove);
//		    TimeSlot.printCoursePositions(coursePositionsCopyRemove);
		    
		    // Print schedules to file
			Solution.printSolutionInTableToFile(solution, outputFile);
		    
		    ObjectiveFunction obj2 = new ObjectiveFunction();
		    double Solutionlvalue2 = obj2.getTotalCost(solutionCopyRemove, coursePositionsCopyRemove);
		    
		    // Print objective function report to file
		    obj2.printObjectiveFunctionCostsToFile(outputFile);
		    obj2.printObjectiveFunctionCostsToConsole();

		    ObjectiveFunction.printObjectiveFunctionValueOnlyToFile(Main.Solve_UTT.getFinalTestRunFile());
		    Main.Solve_UTT.getFinalTestRunFile().print(", " + iterations + "\n");
		   
		    
		    if (Main.Solve_UTT.isRunParameterTuning()) {
		    	ObjectiveFunction.printObjectiveFunctionValueOnlyToFile(Main.Solve_UTT.getParameterTuningFile());
		    }
		    
		    // Print unscheduled courses to file
			outputFile.println("# of unscheduled courses: " + unscheduledCourses.size());
			for (int u = 0; u < unscheduledCourses.size(); u++) {
				outputFile.print(unscheduledCourses.get(u).getCourseId() + ", ");
			}
			outputFile.println("\n\n");
			
			outputFile.println("Total iterations #: " + iterations);
			
			
		    
//		   // objFunc.printObjectiveFunctionCostsToConsole();
//		    System.out.println("Objective value AFTERObj : " + Solutionlvalue2);
//		    System.out.println();
//		    System.out.println("Objective value AFTERDelta : " + Solutionlvalue);
//		    System.out.println();
//		    System.out.println("Number succesfull iterations oneOnly: " + count);
//		    System.out.println("Number succesfull iterations courseSwap: " + count1);

		    

	}


	public static ArrayList<ArrayList<TimeSlot>> setCoursePositions(int numberOfCourses) {
		
		ArrayList<ArrayList<TimeSlot>> coursePositions = new ArrayList<ArrayList<TimeSlot>>();
		
		for (int c = 0; c < numberOfCourses; c++) {
			ArrayList<TimeSlot> courseArrayList = new ArrayList<TimeSlot>();
			coursePositions.add(courseArrayList);
		}
		
		return coursePositions;
	}
	

	public static void deepCopyForTimeSlot(ArrayList<ArrayList<TimeSlot>> coursePositionsCopy,
			ArrayList<ArrayList<TimeSlot>> coursePositionsIn) {
		
		for (int i = 0; i < coursePositionsIn.size(); i++) {
			label99:
			for (int j = 0; j < coursePositionsIn.get(i).size(); j++) {
				TimeSlot TimeSlotTmp = new TimeSlot();
				String courseId = coursePositionsIn.get(i).get(j).getCourseId();
				if(courseId == ""){
					continue label99;
				}
				int courseNumber = Integer.parseInt(courseId.substring(1, courseId.length()));
				TimeSlotTmp.setCourseId(coursePositionsIn.get(i).get(j).getCourseId());
				TimeSlotTmp.setRoomId(coursePositionsIn.get(i).get(j).getRoomId());
				TimeSlotTmp.setScheduleId(coursePositionsIn.get(i).get(j).getScheduleId());
				TimeSlotTmp.setPeriod(coursePositionsIn.get(i).get(j).getPeriod());
				TimeSlotTmp.setDay(coursePositionsIn.get(i).get(j).getDay());
				coursePositionsCopy.get(courseNumber).add(TimeSlotTmp);
			
				
				
//				coursePositionsCopy.get(i).get(j).setDay(coursePositionsIn.get(i).get(j).getDay());
//				coursePositionsCopy.get(i).get(j).setPeriod(coursePositionsIn.get(i).get(j).getPeriod());
//				coursePositionsCopy.get(i).get(j).setRoomId(coursePositionsIn.get(i).get(j).getRoomId());
//				coursePositionsCopy.get(i).get(j).setScheduleId(coursePositionsIn.get(i).get(j).getScheduleId());
			}
		}
		
	}


	public static void deepCopyForSchedule(ArrayList<Schedule[][]> solutionCopy, ArrayList<Schedule[][]> solutionIn) {
		for (int i = 0; i < solutionIn.size(); i++) {
			Schedule[][] scheduletemp=Schedule.makeNewSchedule
					(ReadData.basicData.getPeriods(), ReadData.basicData.getDays());
			
			for (int j = 0; j < ReadData.basicData.getDays(); j++) {
				for (int j2 = 0; j2 < ReadData.basicData.getPeriods(); j2++) {
					scheduletemp[j2][j].setCourseId(solutionIn.get(i)[j2][j].getCourseId());
					scheduletemp[j2][j].setDayIndex(solutionIn.get(i)[j2][j].getDayIndex());
					scheduletemp[j2][j].setPeriodIndex(solutionIn.get(i)[j2][j].getPeriodIndex());
					scheduletemp[j2][j].setRoomId(solutionIn.get(i)[j2][j].getRoomId());
					
				}
			}
			solutionCopy.add(scheduletemp);
		}
		
	}
	
	private void reArrange(
			ArrayList<Schedule[][]> solutionNew, ArrayList<ArrayList<TimeSlot>> coursePositionsNew, ArrayList<Schedule[][]> solution2) {
		ArrayList<Room> rooms = Data.ReadData.getRooms();
		Course course= new Course();
		course.setCourseId("");
		for (int roomNumber = 0; roomNumber < ReadData.getBasicData().getRooms(); roomNumber++) {
			Schedule[][] scheduletmp=Schedule.makeNewSchedule
					(ReadData.basicData.getPeriods(), ReadData.basicData.getDays());
			Room room = rooms.get(roomNumber);
			for (int day = 0; day < ReadData.basicData.getDays(); day++) {
				for (int period= 0; period < scheduletmp.length; period++) {
					scheduletmp = Schedule.addCourseSetRoom(scheduletmp, course, room, period, day);
				}
			}
			solutionNew.add(scheduletmp);
		}
		
		for (int scheduleNumber = 0; scheduleNumber <solution2.size() ; scheduleNumber++) {
			Schedule[][] schedule=solution2.get(scheduleNumber);
			for (int day = 0; day < ReadData.getBasicData().getDays(); day++) {
				for (int period = 0; period < ReadData.basicData.getPeriods(); period++) {
					String courseId=schedule[period][day].getCourseId();
					if (courseId!="") {
						
						course= ReadData.getCourseByIdentifier(schedule[period][day].getCourseId()); 
						String roomId = schedule[period][day].getRoomId();
						int roomNumber=Integer.parseInt(roomId.substring(1));
						Room room=rooms.get(roomNumber);
						solutionNew.get(roomNumber)[period][day].setCourseId(courseId);
						TimeSlot.addPosition(coursePositionsNew, course, room,roomNumber, day, period);
						}
					
					
				}
				}
			}	
	
	}
	
	@Override
	public ArrayList<Course> getUnscheduledCourses() {
		return getUnscheduledCourses();
	}
}
