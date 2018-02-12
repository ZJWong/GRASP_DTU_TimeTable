package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import Data.Basic;
import Data.Course;
import Data.ReadData;
import Data.Relations;
import Data.Room;
import Solution.Schedule;
import Solution.Solution;
import Solution.TimeSlot;
import Solver.RandomisedGreedyHeuristic;
import Solver.GreedyHeuristic;
import Solver.HillClimber;
import Solver.NaiveSolver;
import Solver.RandomisedGreedyHeuristic;
import Solver.RestrictedCandidateList;
import Solver.Solver;

public class Solve_UTT {
	
	private static int instanceNumber = 0;
	
	public static int getInstanceNumber() {
		return instanceNumber;
	}
	
	// Parameter tuning file
	private static final String PARAMETER_TUNING_OUTPUT_FOLDER = System.getProperty("user.dir") + "\\Parameter_Tuning";
	private static PrintWriter parameterTuningFile;
	
	public static PrintWriter getParameterTuningFile() {
		return parameterTuningFile;
	}
	
	// Final test run
	private static final String FINAL_TEST_RUN_OUTPUT_FOLDER = System.getProperty("user.dir") + "\\Final_Test_Run";
	private static PrintWriter finalTestRunFile;

	public static PrintWriter getFinalTestRunFile() {
		return finalTestRunFile;
	}

	
	public static int runningTime = 180; // [seconds]
	
	// start time
//	private static Date date=  new Date();
    private static long startTime = resetStartTime();
    
    
    // initial start time
    private static long initialStartTime;
   
    
    public static long getInitialStartTime() {
		return initialStartTime;
	}
    

	// End time
    private static long endTime;
	
	public static long getEndTime() {
		return endTime;
	}
	
	// Run parameter tuning?
	private static boolean runParameterTuning = false;

	public static boolean isRunParameterTuning() {
		return runParameterTuning;
	}


	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		
		/** Choose solver: Specify which solver should be applied:
		 *  1 if NaiveSolver
		 *  2 if GreedyHeuristic
		 *  3 if RandomisedGreedyHeuristic
		 */
		int solverNumber = 3;
		String solverName = "";
		
		if (solverNumber == 1) {
			solverName = "NaiveSolver";
		}
		else if (solverNumber == 2) {
			solverName = "GreedyHeuristic";
		}
		else if (solverNumber == 3) {
			solverName = "RandomisedGreedyHeuristic";
		}
		else {
			System.err.println("ERROR: Solver does not exist!!!");
		}
		
		
		
		/**************************** PARAMETER TUNING ****************************/
		
		if (runParameterTuning) {
			
			// Create output folder if it does not exist
			File folder = new File(PARAMETER_TUNING_OUTPUT_FOLDER);
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			// Define output file
			parameterTuningFile = new PrintWriter(PARAMETER_TUNING_OUTPUT_FOLDER + "\\ParameterTuning.csv");
			parameterTuningFile.print("TestInstance, alpha, Current Iteration, Final Objective\n");	

			
			
			// Test parameters
			double[] alpha = {0.0, 0.05, 0.1};
			int[] instanceNumbers = {5, 8, 11}; 
			int iterationsPerInstance = 3;
			
			
			// For all test instances
			for (int i = 0; i < instanceNumbers.length; i++) {
				// For all alpha values
				for (int a = 0; a < alpha.length; a++) {
					// For all iterations
					for (int k = 0; k < iterationsPerInstance; k++) {

						startTime = resetStartTime();
						endTime = startTime + runningTime * 1000;
						parameterTuningFile.print(instanceNumbers[i] + ", " + alpha[a] + ", " + (k+1) + ", ");
						solveInstance(solverName, instanceNumbers[i], k, alpha[a]);

					}
				}
			}
			
			parameterTuningFile.close();

		}
		
		
		/**************************** NORMAL TEST RUN ****************************/
		
		if (!runParameterTuning) {
			
			// Default alpha
			double alpha = 0.05;
		
			/** Specify whether you want to run all instances:
			 *  if all instances -> set boolean value to true
			 *  if one specific instance -> set boolean value to & specify the number of instance to run
			 */
			// Run all instances?
			boolean solveAllInstances = true;
			int loopUpperLimit = 0;
		
			if (solveAllInstances) {
				
				// Create output folder if it does not exist
				File folder = new File(FINAL_TEST_RUN_OUTPUT_FOLDER);
				if (!folder.exists()) {
					folder.mkdir();
				}
				
				// Define output file
				finalTestRunFile = new PrintWriter(FINAL_TEST_RUN_OUTPUT_FOLDER + "\\Finall_Test_Run.csv");
				finalTestRunFile.print("TestInstance, alpha, Current Iteration, Final Objective, #Iterations \n");	
				
			
				loopUpperLimit = 13;
				for (int i = 1; i <= loopUpperLimit; i++) {
					startTime = resetStartTime();
					endTime = startTime + runningTime * 1000;
					for (int k = 1; k <= 3; k++) {
						
						startTime = resetStartTime();
						endTime = startTime + runningTime * 1000;
						finalTestRunFile.print(i + ", " + alpha + ", " + k + ", ");
						solveInstance(solverName, i, k, alpha);
						
					}
				}
				
				finalTestRunFile.close();
			
			} else {
				// Specify the instance to be solved below
				loopUpperLimit = 1; 				
				
				for (int i = loopUpperLimit; i <= loopUpperLimit; i++) {
					startTime = resetStartTime();
					endTime = startTime + runningTime * 1000;
					solveInstance(solverName, i, 0, alpha);
				}
			
			}
		}
	}
	
	
	/**
	 * Method called every time an instance is to be solved,
	 * @param currentTestInstance Number of data instance to be solved.
	 * @throws FileNotFoundException
	 */
	public static void solveInstance(String solverName, int currentTestInstance, int iteration, double alpha) throws FileNotFoundException {
		
//	    while (runningTimeTracker(endTime)) {
		Date date = new Date();
		long initialStartTime = (date.getTime() / 1000);
		
		instanceNumber = currentTestInstance;
		System.out.println("Solving instance #" + instanceNumber + " ...");
		
		// Print data content?
		ReadData.setPrintDataContent(false);
		Relations.setPrintDataContent(false);
	
		// Read data
		ReadData.readAllData(instanceNumber);
	
		// Get access to basicData
		Basic basicData = ReadData.getBasicData();
	
		// Retrieve and initialise data
		ArrayList<Schedule[][]> solution = new ArrayList<Schedule[][]>();
		ArrayList<ArrayList<TimeSlot>> coursePositions = setCoursePositions(ReadData.getCourses().size());
		ArrayList<Course> unscheduledCourses = setUnscheduledCourses();
		ArrayList<Course> unscheduledLectures = setUnscheduledLectures();
		ArrayList<Room> rooms = Data.ReadData.getRooms();
		ArrayList<RestrictedCandidateList> restrictedCandidateList = new ArrayList<RestrictedCandidateList>();	
		
		// Prepare Solution Report File Folder
		final String SOLUTION_REPORT_OUTPUT_FOLDER = System.getProperty("user.dir") + "\\Solution_Reports";
						
		// Create output folder if it does not exist
		File folder = new File(SOLUTION_REPORT_OUTPUT_FOLDER);
		if (!folder.exists()) {
			folder.mkdir();
		}
								
		// Output file
		PrintWriter outputFile = new PrintWriter(SOLUTION_REPORT_OUTPUT_FOLDER + "\\Instance_" + instanceNumber + "_it_" + iteration + "_alpha_" + alpha + ".txt");
		
		
		// Call selected construction heuristic
		if (solverName.equals("NaiveSolver")) {
			// Apply NaiveSolver
			System.out.println("\nCalling NaiveSolver...");
			Solver solver1 = new NaiveSolver();
//			solver1.solve(solution, coursePositions, unscheduledCourses, rooms, basicData, outputFile);
		}
		
		else if (solverName.equals("GreedyHeuristic")) {
			// Apply GreedyHeuristic
			System.out.println("\nCalling GreedyHeuristic...");
			Solver solver2 = new GreedyHeuristic();
//			solver2.solve(solution, coursePositions, unscheduledLectures, rooms, basicData, outputFile);	
		}
		
		else if (solverName.equals("RandomisedGreedyHeuristic")) {
			// Apply Randomised Greedy Heuristic
			System.out.println("\nCalling RandomisedGreedyHeuristic...");
			Solver solver3 = new RandomisedGreedyHeuristic();
			solver3.solve(solution, coursePositions, unscheduledLectures, rooms, basicData, outputFile);
		}
		
		else {
			System.err.println("ERROR: Solver not found!!!");
		}
		

		// Apply HillClimber
		System.out.println("\nCalling Hill Climber...");
		Solver hillClimber = new HillClimber();
		hillClimber.solve(solution, coursePositions, unscheduledLectures, rooms, basicData, outputFile);
		
		// Close outputFile
		outputFile.close();
		
		// PRINT FINAL SCHEDULE TO CONSOLE
		Solution.printSolutionInDesiredFormat(solution);
		
//		} // End while
		
	}


	/**
	 * Defines an empty unscheduledCourses arrayList
	 */
	public static ArrayList<Course> setUnscheduledCourses() {
		
		// Define empty unscheduledCourses arrayList
		ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
		
		// Add all courses to unscheduledCourses arrayList
		for (int c = 0; c < Data.ReadData.getCourses().size(); c++) {
			unscheduledCourses.add(Data.ReadData.getCourses().get(c));
		}
		
		return unscheduledCourses;
	}
	
	
	/**
	 * Defines an empty unscheduledLectures arrayList
	 */
	public static ArrayList<Course> setUnscheduledLectures() {
		
		// Define empty unscheduledCourses arrayList
		ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
		
		// Add all courses to unscheduledCourses arrayList
		for (int c = 0; c < Data.ReadData.getCourses().size(); c++) {
			int lectures = ReadData.getCourses().get(c).getLectures();
			for (int l = 0; l < lectures; l++) {
				unscheduledCourses.add(Data.ReadData.getCourses().get(c));
			}
		}
		
		return unscheduledCourses;
	}
	
	
	/**
	 * Initialises an empty double arrayList to store the lecture positions of all courses in schedules.
	 * @param numberOfCourses Total number of courses in courseFile.
	 * @return coursePositions Double arrayList.
	 */
	public static ArrayList<ArrayList<TimeSlot>> setCoursePositions(int numberOfCourses) {
		
		ArrayList<ArrayList<TimeSlot>> coursePositions = new ArrayList<ArrayList<TimeSlot>>();
		
		for (int c = 0; c < numberOfCourses; c++) {
			ArrayList<TimeSlot> courseArrayList = new ArrayList<TimeSlot>();
			coursePositions.add(courseArrayList);
		}
		
		return coursePositions;
	}
	
	
	public static long resetStartTime() {
		Date date = new Date();
		long startTime = date.getTime();
		return startTime;
	}
	
	
	public static boolean runningTimeTracker(long endTime) {
		Date date = new Date();

		if (date.getTime() < endTime) {
			return true;
		}
		return false;
	}
	
}