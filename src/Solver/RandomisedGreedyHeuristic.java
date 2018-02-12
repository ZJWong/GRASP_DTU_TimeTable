package Solver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Data.Basic;
import Data.Course;
import Data.ReadData;
import Data.Room;
import Solution.DeltaEvaluationGreedyHeuristic;
import Solution.HardConstraints;
import Solution.ObjectiveFunction;
import Solution.Schedule;
import Solution.Solution;
import Solution.Swap;
import Solution.TimeSlot;

public class RandomisedGreedyHeuristic implements Solver {
	
	// Decide whether to print intermediate steps
	private static boolean printIntermediateSteps = false;

	public static boolean isPrintIntermediateSteps() {
		return printIntermediateSteps;
	}

	public static void setPrintIntermediateSteps(boolean printIntermediateSteps) {
		RandomisedGreedyHeuristic.printIntermediateSteps = printIntermediateSteps;
	}
		
	
	// Define remaining data
	ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
	ArrayList<Room> rooms = new ArrayList<Room>();
	Basic basicData = ReadData.getBasicData();
	
	private static HashMap<String, ArrayList<TimeSlot>> timeSlotByIdentifier;

	public static ArrayList<TimeSlot> getTimeSlotByIdentifier(String courseId) {
		return timeSlotByIdentifier.get(courseId);
	}	
	
	ArrayList<RestrictedCandidateList> restrictedCandidateList = new ArrayList<RestrictedCandidateList>();

	
	
	@Override
	public void solve(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions,
			ArrayList<Course> unscheduledCourses, ArrayList<Room> rooms, Basic basicData, PrintWriter outputFile) throws FileNotFoundException {		
		

		// Initialise solution by creating an empty schedule
		Schedule[][] schedule = Schedule.makeNewSchedule(basicData.getPeriods(), basicData.getDays());
		solution.add(schedule);
		outputFile.println("Calling Construction Heuristic...\n");
		
		// Calculate initial objective function value
		double objValIntial = coursePositions.size() * 10;
		outputFile.println("Initial objective value: " + objValIntial + "\n");
		
		
		boolean solutionImprovementFound = true;
		int remainingUnscheduled = unscheduledCourses.size();
		
		
		int beta = 1; // DO NOT CHANGE THIS VALUE
		double alpha = 0.05;
		

		
		// Until no further improvements can be made
		while (solutionImprovementFound && Main.Solve_UTT.runningTimeTracker(Main.Solve_UTT.getEndTime())) {
			
			solutionImprovementFound = false;
			int scheduleIndex = -1;
			
			// Identifiers of the best move
			Course bestCourse = null;
			Room bestRoom = null;
			int bestCourseIndex = -1;
			int bestDayIndex = -1;
			int bestPeriodIndex = -1;
			
			
			/********************** DETERMINE DELTA MIN & DELTA MAX *****************************/
			double deltaMin = Double.MAX_VALUE;
			double deltaMax = Double.MIN_VALUE;
			
			
			// For all courses
			for (int c = 0; c < unscheduledCourses.size(); c++) {

				Course currentCourse = unscheduledCourses.get(c);
				unscheduledCourses.remove(c);

				// For all timeSlots
				for (int d = 0; d < basicData.getDays(); d++) {
					for (int p = 0; p < basicData.getPeriods(); p++) {
						
						// For all rooms
						for (int r = 0; r < basicData.getRooms(); r++) {
							
							Room currentRoom = rooms.get(r);

							// Add course to schedule at specified position
							Schedule[][] currentSchedule = Schedule.makeNewSchedule(basicData.getPeriods(),basicData.getDays());
							currentSchedule = Schedule.addCourseSetRoom(currentSchedule, currentCourse, currentRoom, p, d);

							// Proceed only if the tested move does not violate any hard constraints
							HardConstraints hardConstraints = new HardConstraints(currentCourse.getCourseId(), currentRoom.getRoomId(), d, p, coursePositions, solution);
							if (!hardConstraints.hardConstraintsViolated(0)) {
							    
								// Add schedule to solution 
								solution.add(currentSchedule);
								
								// Determine schedule index
								scheduleIndex = solution.size() - 1;
								
								// Update coursePositions arrayList
								TimeSlot.addPosition(coursePositions, currentCourse, currentRoom, scheduleIndex, d, p);
								
								// calculate change to objective function value
								double delta = DeltaEvaluationGreedyHeuristic.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses.size(), unscheduledCourses, coursePositions, currentCourse, currentRoom, scheduleIndex, d, p);							
//								Swap swap = new Swap();
//								swap.addOnlySwap(scheduleIndex, d, p, coursePositions, solution, unscheduledCourses.size(), unscheduledCourses.size()-1);
//								double delta = swap.getDeltaAddOnly();
								
								
								// Determine deltaMin
								if (delta < deltaMin) {
									deltaMin = delta;	
								}
								
								// Determine deltaMax
								if (delta > deltaMax) {
									deltaMax = delta;
								}
								
								
								// Remove course from position
								solution.remove(scheduleIndex);
								
								// Update coursePositions arrayList
								TimeSlot.removePosition(coursePositions, currentCourse);
								
							} // End hard constraints check

						} // End rooms
					} // End periods
				} // End days
				unscheduledCourses.add(c, currentCourse);
			} // End Courses

//			System.out.println("DELTA MIN: " + deltaMin);
//			System.out.println("DELTA MAX: " + deltaMax);
			
			
			
			/********************** GENERATE RCL *****************************/
//			System.out.println("unsch: " + unscheduledCourses.size());
//			RestrictedCandidateList restrictedCandidateListTmp = new RestrictedCandidateList();
			// For all courses
			String previousCourseId = null;
			
			for (int c = 0; c < unscheduledCourses.size(); c++) {

				// No need to examine the same course as before
				if (previousCourseId != null && unscheduledCourses.get(c).getCourseId().equals(previousCourseId)) continue;
				
				Course currentCourse = unscheduledCourses.get(c);
				previousCourseId = currentCourse.getCourseId();
				unscheduledCourses.remove(c);
				
				boolean solutionApproved = false;
				RestrictedCandidateList restrictedCandidateListTmp = new RestrictedCandidateList();
				// For all timeSlots
				for (int d = 0; d < basicData.getDays(); d++) {
					for (int p = 0; p < basicData.getPeriods(); p++) {
						
						// For all rooms
						for (int r = 0; r < basicData.getRooms(); r++) {
							
							Room currentRoom = rooms.get(r);

							// Add course to schedule at specified position
							Schedule[][] currentSchedule = Schedule.makeNewSchedule(basicData.getPeriods(),basicData.getDays());
							currentSchedule = Schedule.addCourseSetRoom(currentSchedule, currentCourse, currentRoom, p, d);

							// Proceed only if the tested move does not violate any hard constraints
							HardConstraints hardConstraints = new HardConstraints(currentCourse.getCourseId(), currentRoom.getRoomId(), d, p, coursePositions, solution);
							if (!hardConstraints.hardConstraintsViolated(0)) {
							    
								// Add schedule to solution 
								solution.add(currentSchedule);
								
								// Determine schedule index
								scheduleIndex = solution.size() - 1;
								
								// Update coursePositions arrayList
								TimeSlot.addPosition(coursePositions, currentCourse, currentRoom, scheduleIndex, d, p);
								
								// calculate altered objective function value
								double delta = DeltaEvaluationGreedyHeuristic.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses.size(), unscheduledCourses, coursePositions, currentCourse, currentRoom, scheduleIndex, d, p);							
//								Swap swap1 = new Swap();
//								swap1.addOnlySwap(scheduleIndex, d, p, coursePositions, solution, unscheduledCourses.size(), unscheduledCourses.size()-1);
//								double delta = swap1.getDeltaAddOnly();
//								
								// If solution quality improved -> save the move
								if (delta <= deltaMin + alpha * (deltaMax - deltaMin)) {
									
									solutionApproved = true;
									bestCourse = currentCourse;
									restrictedCandidateListTmp.setBestCourse(bestCourse);
									bestCourse = null;
									
									bestCourseIndex = c; //System.out.println("bestCourseIndex" + bestCourseIndex);
									restrictedCandidateListTmp.setBestCourseIndex(bestCourseIndex);
									bestCourseIndex = -1;
									
									bestDayIndex = d;
									restrictedCandidateListTmp.setBestDayIndex(bestDayIndex);

									
									bestPeriodIndex = p;
									restrictedCandidateListTmp.setBestPeriodIndex(bestPeriodIndex);
									bestPeriodIndex = -1;
									
									bestRoom = currentRoom;
									restrictedCandidateListTmp.setBestRoom(bestRoom);
									bestRoom = null;

								}
								
								
								// Remove course from position
								solution.remove(scheduleIndex);
								
								// Update coursePositions arrayList
								TimeSlot.removePosition(coursePositions, currentCourse);
								
							} // End hard constraints check

						} // End rooms
					} // End periods
				} // End days
				
				// Save only one best option for each course/lecture and do it only if such a one has been found
				if (solutionApproved) {
//					System.out.println("Course " + restrictedCandidateListTmp.getBestCourse().getCourseId() + " added to RCL" );
					
					restrictedCandidateList.add(restrictedCandidateListTmp);
					restrictedCandidateListTmp = new RestrictedCandidateList();
					
					
//					System.out.println();
//					for (int k = 0; k < restrictedCandidateList.size(); k++) {
//						System.out.print(restrictedCandidateList.get(k).getBestCourse().getCourseId() + ", ");
//					}
//					System.out.println();
					
				}
				
//				if (solutionApproved) {
//					restrictedCandidateList.add(restrictedCandidateListTmp);
//				}
				
				// Add current course back to unscheduled
				unscheduledCourses.add(c, currentCourse);
				
			} // End Courses

//			System.out.println();
//			for (int k = 0; k < restrictedCandidateList.size(); k++) {
//				System.out.print(restrictedCandidateList.get(k).getBestCourse().getCourseId() + ", ");
//			}
//			System.out.println();
			
			
			if (restrictedCandidateList.size() == 1) {
//				System.out.println("course index last = " + restrictedCandidateList.get(0).getBestCourseIndex());
			}
			
//			System.out.println("RCL size: " + restrictedCandidateList.size());
			
			if (restrictedCandidateList.size() > 0) {
				solutionImprovementFound = true;
			}
			
			
			/********************** SELECT ELEMENT FROM RCL AND INSERT IN SOLUTION *****************************/
			if (solutionImprovementFound) {
//				System.out.println("unsch2: " + unscheduledCourses.size());
			
				
			// Select beta best candidates
			for (int b = 0; b < beta; b++) {
				
				int randomIndex = (int) (Math.random() * restrictedCandidateList.size() - 1);

//				System.out.println("randomIndex " + randomIndex);
				
				RestrictedCandidateList candidate = restrictedCandidateList.get(randomIndex);
				bestCourse = candidate.getBestCourse(); //System.out.println("bestCourse: " + bestCourse.getCourseId());
				bestCourseIndex = candidate.getBestCourseIndex(); //System.out.println("bestCourseIndex " + bestCourseIndex); 
				bestRoom = candidate.getBestRoom(); //System.out.println("bestRoom: " + bestRoom);
				bestPeriodIndex = candidate.getBestPeriodIndex(); //System.out.println("bestPeriodIndex: " + bestPeriodIndex);
				bestDayIndex = candidate.getBestDayIndex(); //System.out.println("bestDayIndex: " + bestDayIndex);

				
				// Add course in the best schedule at best timeSlot
				// Try to add course in an existing schedule to compress solution size				
				boolean existingScheduleFound = false;
				
				for (int s = 0; s < solution.size(); s++) {

					// If course has still not been inserted and there are still schedules to investigate 
					if (!existingScheduleFound) {
					
						// If empty desired timeSlot is found -> insert course at position
						if (solution.get(s)[bestPeriodIndex][bestDayIndex].getCourseId().equals("")) {
					
							Schedule.addCourseSetRoom(solution.get(s), bestCourse, bestRoom, bestPeriodIndex, bestDayIndex);
							existingScheduleFound = true;
							scheduleIndex = s;
							
						}
					}
				}
			
				// If empty timeSlot in an existing schedule has not been found -> make a new schedule and add the course
				if (!existingScheduleFound) {
					
					Schedule[][] newSchedule = Schedule.makeNewSchedule(basicData.getPeriods(), basicData.getDays());
					newSchedule = Schedule.addCourseSetRoom(newSchedule, bestCourse, bestRoom, bestPeriodIndex, bestDayIndex);
					solution.add(newSchedule);
					scheduleIndex = solution.size() - 1;
				
				}
				
				// Update position of the course
				TimeSlot.addPosition(coursePositions, bestCourse, bestRoom, scheduleIndex, bestDayIndex, bestPeriodIndex);

				
				// Remove course from RCL and unscheduledCourses
				unscheduledCourses.remove(bestCourseIndex);				
				restrictedCandidateList.remove(randomIndex);
				
				
				// Print removal summary
				if (printIntermediateSteps) {
					outputFile.println("Removing course " + unscheduledCourses.get(bestCourseIndex).getCourseId() + " from unscheduled courses.");
					outputFile.println("Remaining courses to be scheduled: " + unscheduledCourses.size()); 
				}

			}
			
			// Print modified solution
			if (printIntermediateSteps) {
				outputFile.println("ALTERED SOLUTION: ");
				Solution.printSolutionInTableToFile(solution, outputFile);
			}
			
			}
			
			restrictedCandidateList.clear();
			
			
		} // End while
		
		
		// Print schedules to file
		Solution.printSolutionInTableToFile(solution, outputFile);
		
		// Calculate and print objective function value
		ObjectiveFunction obj = new ObjectiveFunction();
		
		obj.getTotalCost(solution, coursePositions);
		ObjectiveFunction.printObjectiveFunctionCostsToFile(outputFile);	
		ObjectiveFunction.printObjectiveFunctionCostsToConsole();	
//		ObjectiveFunction.printObjectiveFunctionValueOnlyToFile(Main.Solve_UTT.getParameterTuningFile());
		
		// Print unscheduled courses
		outputFile.println("# of unscheduled courses: " + unscheduledCourses.size());
		for (int u = 0; u < unscheduledCourses.size(); u++) {
			outputFile.print(unscheduledCourses.get(u).getCourseId() + ", ");
		}
		outputFile.println("\n\n");
		
		}
	
	
	@Override
	public ArrayList<Course> getUnscheduledCourses() {
		return getUnscheduledCourses();
	}
	
}