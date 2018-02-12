package Solver;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Data.Basic;
import Data.Course;
import Data.Room;
import Data.ReadData;
import Solution.HardConstraints;
import Solution.Schedule;
import Solution.Solution;
import Solution.TimeSlot;

public class NaiveSolver implements Solver {
	
	ArrayList<Course> unscheduledCourses = new ArrayList<Course>();
	ArrayList<Room> rooms = new ArrayList<Room>();
	Basic basicData = ReadData.getBasicData();
	
	private static HashMap<String, ArrayList<TimeSlot>> timeSlotByIdentifier;

	public static ArrayList<TimeSlot> getTimeSlotByIdentifier(String courseId) {
		return timeSlotByIdentifier.get(courseId);
	}
	
	
	@Override
	public void solve(ArrayList<Schedule[][]> solution, ArrayList<ArrayList<TimeSlot>> coursePositions,
			ArrayList<Course> unscheduledCourses, ArrayList<Room> rooms, Basic basicData, PrintWriter outputFile) {		
		
		// Initial schedule index
		int scheduleIndex = 0;
		
		// Initial positions
		int periodIndex = 0;
		int dayIndex = 0;
		
		// Initial room
		int roomIndex = 0;


		Schedule[][] schedule = Schedule.makeNewSchedule(basicData.getPeriods(), basicData.getDays());
		
		outputFile.println("Calling Construction Heuristic...\n");
		
		// Until all courses are scheduled (assigned a timeslot)
		while (!unscheduledCourses.isEmpty()) {

			Course course = unscheduledCourses.remove(0); // remove first course from unscheduled-arrayList
			
			int lectures = course.getLectures();
			
			// Choose a room
			roomIndex = 0;
			Room room = rooms.get(roomIndex);
			
			// For all times (lectures) that the given course lasts 
			
			label:
			for (int l = 0; l < lectures; l++) {
				
				// Call for Hardconstraints
				int count = 0;
				Boolean checkanswer = true;
				while(checkanswer == true){
				room = rooms.get(roomIndex);

				
				String courseId = course.getCourseId();
				String roomId = room.getRoomId();
				HardConstraints callHardCon = new HardConstraints(courseId, roomId, dayIndex, periodIndex, coursePositions, solution);
				checkanswer = callHardCon.hardConstraintsViolated(0);
				
				
				if(checkanswer == false){
					break;
				}
				
				roomIndex++;
				
				if(basicData.getRooms() == roomIndex){
					roomIndex = 0;
					periodIndex++;
					if(periodIndex == basicData.getPeriods()){
						dayIndex++;
						periodIndex = 0;
						if(dayIndex == basicData.getDays()){
							dayIndex = 0;
							count++;
							if(count == 2){
								break label;
							}
							solution.add(schedule);
							schedule = Schedule.makeNewSchedule(basicData.getPeriods(), basicData.getDays());
							scheduleIndex++;
						}
					}
				}
				
				}
				// Finish Call	
				
				
				// Add unscheduled course and room to schedule at the specified timeslot position
				schedule = Schedule.addCourseSetRoom(schedule, course, room, periodIndex, dayIndex);

				// Update position of the course
				TimeSlot.addPosition(coursePositions, course, room, scheduleIndex, dayIndex, periodIndex);
			
				// UPDATE TIMESLOT POSITIONS
				// if the last period of a day is reached, but following day still unscheduled => move to next day
				if (periodIndex == basicData.getPeriods() - 1 && dayIndex < basicData.getDays() - 1) {		
					dayIndex++; // move to next day
					periodIndex = 0; // start from first time slot
			
				// else if the schedule is totally booked -> all timeslots are assigned a course & room => create a new schedule 
				} else if (periodIndex == basicData.getPeriods() - 1 && dayIndex == basicData.getDays() - 1 /*&& unscheduledCourses.size() > 0*/) {
				
					// Add current schedule to solution arrayList
					solution.add(schedule);
				
					// Replace current schedule by a new empty schedule 
					schedule = Schedule.makeNewSchedule(basicData.getPeriods(), basicData.getDays());
					scheduleIndex++;
					periodIndex = 0;
					dayIndex = 0;
			
				// else => move to next timeslot
				} else {
				
					periodIndex++;
				
				}
			
			}// End for all lectures 
			
		} // End while loop
		
		// Add current schedule to solution arrayList
		solution.add(schedule);
		
		Solution.printSolutionInTableToFile(solution, outputFile);
		outputFile.close();
		
		System.out.println("");
	
	}
	

	@Override
	public ArrayList<Course> getUnscheduledCourses() {
		return getUnscheduledCourses();
	}

	public void setUnscheduledCourses(ArrayList<Course> unscheduledCourses) {
		this.unscheduledCourses = unscheduledCourses;
	}

}

