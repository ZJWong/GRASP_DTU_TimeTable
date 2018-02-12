package Solver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Data.Basic;
import Data.Course;
import Data.Room;
import Solution.Schedule;
import Solution.TimeSlot;

public interface Solver {
	
	/**
	 * 
	 * @param solution The solution (schedule) as it has been constructed so far
	 * @param unplannedCourses A list of all unplanned courses
	 * @param cf Objective function
	 * @throws FileNotFoundException 
	 */
	public void solve(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions, 
			ArrayList<Course> unscheduledCourses, ArrayList<Room> rooms,
			Basic basicData, PrintWriter outputFile) throws FileNotFoundException;

	public ArrayList<Course> getUnscheduledCourses();

}
