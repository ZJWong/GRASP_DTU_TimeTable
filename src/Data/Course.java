package Data;

public class Course {
	
	// Define variables
	String courseId;
	String lecturer;
	int lectures;
	int workingDays;
	int students;
	Room room;
	
	
	
	public String getCourseId() {
		return courseId;
	}
	
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	
	
	public String getLecturer() {
		return lecturer;
	}
	
	public void setLecturer(String lecturer) {
		this.lecturer = lecturer;
	}
	
	
	public int getLectures() {
		return lectures;
	}
	
	public void setLectures(int lectures) {
		this.lectures = lectures;
	}
	
	
	public int getWorkingDays() {
		return workingDays;
	}
	
	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}
	
	
	public int getStudents() {
		return students;
	}
	
	public void setStudents(int students) {
		this.students = students;
	}

	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}	

}
