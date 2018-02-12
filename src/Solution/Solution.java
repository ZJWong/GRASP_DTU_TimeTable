package Solution;

import java.io.PrintWriter;
import java.util.ArrayList;

import Data.Course;
import Data.ReadData;

public class Solution {
	
	private ArrayList<Schedule[][]> schedules = new ArrayList<Schedule[][]>();
	private ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
	private ArrayList<ArrayList<TimeSlot>> coursePositions = new ArrayList<ArrayList<TimeSlot>>(); 

	
	public Solution(ArrayList<Schedule[][]> schedules, ArrayList<Course> unscheduledCourses,
			ArrayList<ArrayList<TimeSlot>> coursePositions) {
		super();
		this.schedules = schedules;
		this.unscheduledCourses = unscheduledCourses;
		this.coursePositions = coursePositions;
	}
	
	public Solution() {}


	public void addSchedule(Schedule[][] schedule) {
		schedules.add(schedule);
	}	
	
	public ArrayList<Schedule[][]> getSchedules() {
		return schedules;
	}

	public void setSchedule(ArrayList<Schedule[][]> schedules) {
		this.schedules = schedules;
	}

	public ArrayList<Course> getUnscheduledCourses() {
		return unscheduledCourses;
	}

	public void setUnscheduledCourses(ArrayList<Course> unscheduledCourses) {
		this.unscheduledCourses = unscheduledCourses;
	}
		
	public ArrayList<ArrayList<TimeSlot>> getCoursePositions() {
		return coursePositions;
	}

	public void setCoursePositions(ArrayList<ArrayList<TimeSlot>> coursePositions) {
		this.coursePositions = coursePositions;
	}
	
	
	public static ArrayList<Schedule[][]> replaceScheduleInSolution(ArrayList<Schedule[][]> solution, Schedule[][] newSchedule, int currentScheduleIndex) {
		
		ArrayList<Schedule[][]> updatedSolution = new ArrayList<Schedule[][]>();
		
		// Add updated schedule at the same position
		solution.set(currentScheduleIndex, newSchedule);
		
		// Remove current schedule from currentScheduleIndex position
//		solution.remove(currentScheduleIndex + 1);
		
		// Save the update
		updatedSolution = solution;
		
		return updatedSolution;
		
	}
	

	/**
	 * Prints all current schedules present in the solution to console
	 * @param solution ArrayList of schedules (solution)
	 */
	public static void printSolutionInTableToConsole(ArrayList<Schedule[][]> solution) {
		
		System.out.println("\n\nPrinting solution:\n");

		// For all schedules in the solution
		for (int s = 0; s < solution.size(); s++) {
			Schedule[][] sch = solution.get(s);
			

			System.out.println("Schedule #" + (s+1));

			// Print day abbreviations
			tableWidthToConsole(sch[0].length);
			String[] dayAbb = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

			int days = sch[0].length;

			for (int d = 0; d < days; d++) {
				if (d == days - 1) {
					System.out.print("|  " + dayAbb[d] + "  || p |");
				} else {
					System.out.print("|  " + dayAbb[d] + "  |");
				}
			}
			System.out.println();
			tableWidthToConsole(days);

			// Print schedule content
			for (int p = 0; p < Data.ReadData.getBasicData().getPeriods(); p++) {

				// print course Id's
				for (int c = 0; c < sch[p].length; c++) {
					
					// print course id's:
					// If last day of the week -> print period #
					if (c == sch[c].length - 1) {
						
						// Check if empty
						if (sch[p][c].getCourseId().equals("")) {
							System.out.print("|       || " + (p + 1) + " |");
						} else {
							System.out.print("| " + sch[p][c].getCourseId() + " || " + (p + 1) + " |");
						}
					} else {
						
						// Check if empty
						if (sch[p][c].getCourseId().equals("")) {
							System.out.print("|       |");
						} else {
							System.out.print("| " + sch[p][c].getCourseId() + " |");
						}
					}

				}
				
				
				System.out.println(); // print rooms in next line right beneath course Id's

				// print lecturers
				for (int l = 0; l < sch[p].length; l++) {
					
					// PRINT LECTURER ID'S
					// If last day of the week -> print period #
					if (l == sch[l].length - 1) {
						
						// Check if empty
						if (sch[p][l].getCourseId().equals("")) {
							System.out.print("|       ||   |");
						} else {
							System.out.print("| " + ReadData.getCourseByIdentifier(sch[p][l].getCourseId()).getLecturer() + " ||   |");
						}
						
					} else {
						// Check if empty
						if (sch[p][l].getCourseId().equals("")) {
							System.out.print("|       |");
						} else {
							System.out.print("| " + ReadData.getCourseByIdentifier(sch[p][l].getCourseId()).getLecturer() + " |");
						}
							
					}
	
				}
				

				System.out.println(); // print rooms in next line right beneath course Id's

				// print rooms
				for (int r = 0; r < sch[p].length; r++) {
					
					// PRINT ROOM ID'S
					// If last day of the week -> print period #
					if (r == sch[r].length - 1) {
						
						// Check if empty
						if (sch[p][r].getCourseId().equals("")) {
							System.out.print("|       ||   |");
						} else {
							System.out.print("| " + sch[p][r].getRoomId() + " ||   |");
						}
						
					} else {
						// Check if empty
						if (sch[p][r].getCourseId().equals("")) {
							System.out.print("|       |");
						} else {
							System.out.print("| " + sch[p][r].getRoomId() + " |");
						}
							
					}
	
				}

				System.out.println();
				tableWidthToConsole(days);
			}
			
			System.out.println("\n");

		}
		
	}
	
	
	/**
	 * Prints all current schedules present in the solution to console
	 * @param solution ArrayList of schedules (solution)
	 */
	public static void printSolutionInTableToFile(ArrayList<Schedule[][]> solution, PrintWriter outputFile) {
		
		outputFile.println("\n\n--------------- PRINTING SOLUTION ---------------\n");

		// For all schedules in the solution
		for (int s = 0; s < solution.size(); s++) {
			Schedule[][] sch = solution.get(s);
			

			outputFile.println("Schedule #" + (s+1));

			// Print day abbreviations
			tableWidthToFile(outputFile, sch[0].length);
			String[] dayAbb = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

			int days = sch[0].length;

			for (int d = 0; d < days; d++) {
				if (d == days - 1) {
					outputFile.print("|  " + dayAbb[d] + "  || p |");
				} else {
					outputFile.print("|  " + dayAbb[d] + "  |");
				}
			}
			outputFile.println();
			tableWidthToFile(outputFile, days);

			// Print schedule content
			for (int p = 0; p < Data.ReadData.getBasicData().getPeriods(); p++) {

				// PRINT COURSE ID's
				for (int c = 0; c < sch[p].length; c++) {
					
					// print course id's:
					// If last day of the week -> print period #
					if (c == sch[c].length - 1) {
						
						// Check if empty
						if (sch[p][c].getCourseId().equals("")) {
							outputFile.print("|       || " + (p + 1) + " |");
						} else {
							outputFile.print("| " + sch[p][c].getCourseId() + " || " + (p + 1) + " |");
						}
					} else {
						
						// Check if empty
						if (sch[p][c].getCourseId().equals("")) {
							outputFile.print("|       |");
						} else {
							outputFile.print("| " + sch[p][c].getCourseId() + " |");
						}
					}

				}
				
				outputFile.println(); // print lecturer Id's in next line right beneath course Id's

				// PRINT LECTURER ID's
				for (int l = 0; l < sch[p].length; l++) {
					
					// If last day of the week -> print period #
					if (l == sch[l].length - 1) {
						
						// Check if empty
						if (sch[p][l].getCourseId().equals("")) {
							outputFile.print("|       ||   |");
						} else {
							outputFile.print("| " + ReadData.getCourseByIdentifier(sch[p][l].getCourseId()).getLecturer() + " ||   |");
						}
						
					} else {
						// Check if empty
						if (sch[p][l].getCourseId().equals("")) {
							outputFile.print("|       |");
						} else {
							outputFile.print("| " + ReadData.getCourseByIdentifier(sch[p][l].getCourseId()).getLecturer() + " |");
						}
							
					}
	
				}


				outputFile.println(); // print rooms in next line right beneath lecturer Id's

				// print rooms
				for (int r = 0; r < sch[p].length; r++) {
					
					// PRINT ROOM ID'S
					// If last day of the week -> print period #
					if (r == sch[r].length - 1) {
						
						// Check if empty
						if (sch[p][r].getCourseId().equals("")) {
							outputFile.print("|       ||   |");
						} else {
							outputFile.print("| " + sch[p][r].getRoomId() + " ||   |");
						}
						
					} else {
						// Check if empty
						if (sch[p][r].getCourseId().equals("")) {
							outputFile.print("|       |");
						} else {
							outputFile.print("| " + sch[p][r].getRoomId() + " |");
						}
							
					}
	
				}

				outputFile.println();
				tableWidthToFile(outputFile, days);
			}
			
			outputFile.println();

		}
		
	}
	
	
	/**
	 * Defines the border width of printed schedules to output file.
	 */
	public static void tableWidthToFile(PrintWriter outputFile, int days) {
		
		if (days == 5) {
			outputFile.println("--------------------------------------------------");
		}
		else if (days == 6) {
			outputFile.println("-----------------------------------------------------------");
		}
		
	}
	
	
	/**
	 * Defines the border width of printed schedules to output file.
	 */
	public static void tableWidthToConsole(int days) {
		
		if (days == 5) {
			System.out.println("--------------------------------------------------");
		}
		else if (days == 6) {
			System.out.println("-----------------------------------------------------------");
		}
		
	}
	
	
	/**
	 * Prints course, room and timeSlot assignments in desired format.
	 */
	public static void printSolutionInDesiredFormat(ArrayList<Schedule[][]> solution) {
		
		System.out.println();
		System.out.println("---------- Printing Solution ----------");
		System.out.println("CourseID Day Period RoomID");
		
		for (int sch = 0; sch < solution.size(); sch++) {
			Schedule[][] schedule = solution.get(sch);
			for (int p = 0; p < Data.ReadData.getBasicData().getPeriods(); p++) {
				for (int d = 0; d < Data.ReadData.getBasicData().getDays(); d++) {
					if (!schedule[p][d].getCourseId().equals("") && !schedule[p][d].getRoomId().equals("")) {
						System.out.println(schedule[p][d].getCourseId() + "     " + d + "    " + p + "    " + schedule[p][d].getRoomId());
					}
				}
			}
		}
		
		System.out.println("\n\n");
	}

}