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

public class GreedyHeuristic implements Solver {
	
	// Decide whether to print intermediate steps
	private static boolean printIntermediateSteps = false;

	public static boolean isPrintIntermediateSteps() {
		return printIntermediateSteps;
	}

	public static void setPrintIntermediateSteps(boolean printIntermediateSteps) {
		GreedyHeuristic.printIntermediateSteps = printIntermediateSteps;
	}
	

	// Set remaining data
	ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
	ArrayList<Room> rooms = new ArrayList<Room>();
	Basic basicData = ReadData.getBasicData();
	
	private static HashMap<String, ArrayList<TimeSlot>> timeSlotByIdentifier;

	public static ArrayList<TimeSlot> getTimeSlotByIdentifier(String courseId) {
		return timeSlotByIdentifier.get(courseId);
	}
		
	
	
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
		int remainingUnscheduledPrevious = remainingUnscheduled;
		int remainingUnscheduledCurrent = -1;
		
		double bestValue = objValIntial;//objValIntial;

		
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
			double bestCurrentDelta = ReadData.getBasicData().getCourses() * 100;
			
			
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
								
								// calculate altered objective function value
								double delta = DeltaEvaluationGreedyHeuristic.getTotalAfterChangeCost(solution, remainingUnscheduled, unscheduledCourses.size(), unscheduledCourses, coursePositions, currentCourse, currentRoom, scheduleIndex, d, p);							
//								Swap swap = new Swap();
//								swap.addOnlySwap(scheduleIndex, d, p, coursePositions, solution, unscheduledCourses.size(), unscheduledCourses.size()-1);
//								double delta = swap.getDeltaAddOnly();
								
								// If solution quality improved -> save the move
								if (delta < bestCurrentDelta) { // newObjVal < bestObjective, delta < 0
									solutionImprovementFound = true;
									bestCourse = currentCourse;
									bestCourseIndex = c;
									bestRoom = currentRoom;
									bestDayIndex = d;
									bestPeriodIndex = p;
									bestCurrentDelta = delta;

									remainingUnscheduledPrevious = remainingUnscheduled;
									remainingUnscheduledCurrent = unscheduledCourses.size();	
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

			
			// If solution can be improved
			if (solutionImprovementFound) {
				
				if (printIntermediateSteps) {
					// Print best move parameters
					outputFile.println();
					outputFile.println("Current objective = " + objValIntial);
					outputFile.println("Improvement possibility identified. Best move details:");
					outputFile.println("bestCourse = " + bestCourse.getCourseId());
					outputFile.println("bestCourseIndex = " + bestCourseIndex);
					outputFile.println("bestRoom = " + bestRoom.getRoomId());
					outputFile.println("bestDayIndex = " + bestDayIndex);
					outputFile.println("bestPeriodIndex = " + bestPeriodIndex);
					outputFile.println("bestObjective = " + bestValue);
					outputFile.println("# of schedules in solution: " + solution.size()); 
				
					// Print current solution
					outputFile.println();
					outputFile.println("SOLUTION PRIOR TO CHANGE:");
					Solution.printSolutionInTableToFile(solution, outputFile);		
				}
				
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
				
	
				// Print modified solution
				if (printIntermediateSteps) {
					outputFile.println("ALTERED SOLUTION: ");
					Solution.printSolutionInTableToFile(solution, outputFile);
				}
				
				
				// Update position of the course
				TimeSlot.addPosition(coursePositions, bestCourse, bestRoom, scheduleIndex, bestDayIndex, bestPeriodIndex);
//				TimeSlot.printCoursePositionsToFile(coursePositions, outputFile);
				
				
				// Remove course from unscheduledCourses
				unscheduledCourses.remove(bestCourseIndex);
					
				
				// Print # of remaining courses
				if (printIntermediateSteps) {
					outputFile.println("Removing course " + unscheduledCourses.get(bestCourseIndex).getCourseId() + " from unscheduled courses.");
					outputFile.println("Remaining courses to be scheduled: " + unscheduledCourses.size()); 
				}
			
				
				// Update objective function value
				if (bestCurrentDelta < bestValue) {
					bestValue = bestCurrentDelta;
				}
				
			} // End if improved
			
		} // End while
		
		
		// Print solution to output file
		Solution.printSolutionInTableToFile(solution, outputFile);
		
		// Calculate and print objective function value
		
		ObjectiveFunction obj = new ObjectiveFunction();
		obj.getTotalCost(solution, coursePositions);
		ObjectiveFunction.printObjectiveFunctionCostsToFile(outputFile);	
		ObjectiveFunction.printObjectiveFunctionCostsToConsole();	
		
		// Print unscheduled courses
		outputFile.println("# of unscheduled courses: " + unscheduledCourses.size());
		for (int u = 0; u < unscheduledCourses.size(); u++) {
			outputFile.print(unscheduledCourses.get(u).getCourseId() + ", ");
		}
		outputFile.println("\n\n");
		
//		TimeSlot.printCoursePositionsToFile(coursePositions, outputFile);
		
		outputFile.close();

	}
	
	
	@Override
	public ArrayList<Course> getUnscheduledCourses() {
		return getUnscheduledCourses();
	}

	public void setUnscheduledCourses(ArrayList<Course> unscheduledCourses) {
		this.unscheduledCourses = unscheduledCourses;
	}
	
}