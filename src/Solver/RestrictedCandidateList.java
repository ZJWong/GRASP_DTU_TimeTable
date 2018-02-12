package Solver;

import Data.Course;
import Data.Room;

public class RestrictedCandidateList {
	
	private Course bestCourse;
	private int bestCourseIndex;
	private Room bestRoom;
	private int bestDayIndex;
	private int bestPeriodIndex;
	
	
	public Course getBestCourse() {
		return bestCourse;
	}
	public void setBestCourse(Course bestCourse) {
		this.bestCourse = bestCourse;
	}
	public int getBestCourseIndex() {
		return bestCourseIndex;
	}
	public void setBestCourseIndex(int bestCourseIndex) {
		this.bestCourseIndex = bestCourseIndex;
	}
	public Room getBestRoom() {
		return bestRoom;
	}
	public void setBestRoom(Room bestRoom) {
		this.bestRoom = bestRoom;
	}
	public int getBestDayIndex() {
		return bestDayIndex;
	}
	public void setBestDayIndex(int bestDayIndex) {
		this.bestDayIndex = bestDayIndex;
	}
	public int getBestPeriodIndex() {
		return bestPeriodIndex;
	}
	public void setBestPeriodIndex(int bestPeriodIndex) {
		this.bestPeriodIndex = bestPeriodIndex;
	}

}
