package Data;

import java.util.ArrayList;

public class UnavailabilityByCourse {
int day;
int period;
String courseId;

//Print data content?
private static boolean printDataContent = false;

public static boolean isPrintDataContent() {
	return printDataContent;
}

public static void setPrintDataContent(boolean printDataContent) {
	UnavailabilityByCourse.printDataContent = printDataContent;
}


private static ArrayList<ArrayList<UnavailabilityByCourse>> unavailibilityArrayListByCourses;

public static ArrayList<ArrayList<UnavailabilityByCourse>> addUnavailibilityToArrayListByCourses (
		ArrayList<ArrayList<UnavailabilityByCourse>> unavailibilityByCourse, ArrayList<Unavailability> unavailability) {
	
		for (int i = 0; i < unavailability.size(); i++) {
		
		int day = unavailability.get(i).getDay();
		int period = unavailability.get(i).getPeriod();
		String courseId = unavailability.get(i).getCourse().courseId;
		int courseNumber= Integer.parseInt(courseId.substring(1,courseId.length()));
		// Extract curriculum number used as identifier for the arrayList of relation
		
		
		UnavailabilityByCourse UnavailabilityByCourseTmp= new UnavailabilityByCourse();
	
		// Set UnavailabilityByCourse data for the curriculum and courses
		UnavailabilityByCourseTmp.setDay(day);
		UnavailabilityByCourseTmp.setPeriod(period);
		UnavailabilityByCourseTmp.setCourseId(courseId);
		// Add positions data to arrayList
		unavailibilityByCourse.get(courseNumber).add(UnavailabilityByCourseTmp);
		
	}

	if (printDataContent) {	
		printit(unavailibilityByCourse);
	}
	
	return unavailibilityByCourse;
}
private static void printit(ArrayList<ArrayList<UnavailabilityByCourse>> unavailibilityByCourse) {
	System.out.println("\n\n--------------------------------------------------");
	System.out.println("Printing unavailibilityDataByCourses arrayList data:\n");

	System.out.println("Comment: Data printed in the following order: ");
	System.out.println("1. CourseId");
	System.out.println("2. Day");
	System.out.println("3. Period");
	// For all curricular arrayLists
	for (int i = 0; i < unavailibilityByCourse.size(); i++) {

		// For all relations records in curricular arrayList
		for (int c = 0; c < unavailibilityByCourse.get(i).size(); c++) {

			UnavailabilityByCourse currentUnavailibility = unavailibilityByCourse.get(i).get(c);
			System.out.print("[" + currentUnavailibility.getCourseId() + ", "
						         + currentUnavailibility.getDay() + " "+currentUnavailibility.getPeriod() + "] ");
			
			if (c == unavailibilityByCourse.get(i).size() - 1) {
				System.out.println();
			}
		}
	}

	System.out.println("--------------------------------------------------\n\n");
}

	

public static ArrayList<ArrayList<UnavailabilityByCourse>> setUnavailabilityArrayListByCourses(int numberOfUnavailability) {
	
	ArrayList<ArrayList<UnavailabilityByCourse>> unavailibilityArrayListByCourses = new ArrayList<ArrayList<UnavailabilityByCourse>>();
	
	for (int c = 0; c < numberOfUnavailability; c++) {
		ArrayList<UnavailabilityByCourse> unavailibility = new ArrayList<UnavailabilityByCourse>();
		unavailibilityArrayListByCourses.add(unavailibility);
	}
	
	return unavailibilityArrayListByCourses;
}





public int getDay() {
	return day;
}

public void setDay(int day) {
	this.day = day;
}


public int getPeriod() {
	return period;
}

public void setPeriod(int period) {
	this.period = period;
}


public void setCourseId(String courseId) {
	this.courseId = courseId;
}

public String getCourseId() {
	return courseId;
}


public static ArrayList<ArrayList<UnavailabilityByCourse>> getUnavailability() {
	return unavailibilityArrayListByCourses;
}




}
