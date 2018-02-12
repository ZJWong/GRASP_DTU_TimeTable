package Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class ReadData {

	// Print data content?
	private static boolean printDataContent = true;

	public boolean isPrintDataContent() {
		return printDataContent;
	}

	public static void setPrintDataContent(boolean printDataContent) {
		ReadData.printDataContent = printDataContent;
	}

	

	public static void readAllData(int instanceNumber) throws FileNotFoundException {
		
		// TestData file path
		String filePath = System.getProperty("user.dir") + "\\TestData\\";
		
		String testDataSubFoldersName = "Test";
		
			
		// Determine Test folder number
		String subFolderIndex = "";
		if (instanceNumber < 10) {
			subFolderIndex = "0" + instanceNumber;
		} else {
			subFolderIndex = "" + instanceNumber;
		}
		
		if (printDataContent) {
			System.out.println("----------------------------------------------");
		}
		
		// Read Basic data
		basicFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\basic.utt");
		basicData = readBasic(basicFile);
			
		// Read Courses data
		courseFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\courses.utt");
		courses = readCourses(courseFile);
			
		// Read Curriculum data
		curriculaFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\curricula.utt");
		curricula = readCurricula(curriculaFile);
			
		// Read Lectures data
		lecturersFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\lecturers.utt");
		lecturers = readLecturers(lecturersFile);
			
		// Read Relation data
		relationsFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\relation.utt");
		relations = readRelation(relationsFile);
		
		// Save all courses related to a specific curricular in one arrayList
		relationsArrayListByCurricula = Relations.setRelationsArrayListByCurricula(relations.size());
		relationsArrayListByCurricula = Relations.addRelationsToArrayListByCurricula(relationsArrayListByCurricula, relations);

		relationsArrayListByCourses = Relations.setRelationsArrayListByCourses(relations.size());
		relationsArrayListByCourses = Relations.addRelationsToArrayListByCourses(relationsArrayListByCourses, relations);
			
		// Read Room data
		roomsFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\rooms.utt");
		rooms = readRooms(roomsFile);
			
		// Read Unavailability data
		unavailabilityFile = new File(filePath + testDataSubFoldersName + subFolderIndex + "\\unavailability.utt");
		unavailabilities = readUnavailability(unavailabilityFile);
		unavailabilitiesByCourse = UnavailabilityByCourse.setUnavailabilityArrayListByCourses(ReadData.basicData.getCourses());
		unavailabilitiesByCourse = UnavailabilityByCourse.addUnavailibilityToArrayListByCourses(unavailabilitiesByCourse, unavailabilities);
		
		
		if (printDataContent) {
			System.out.println("----------------------------------------------\n");
		}

	}
	
	
	/**
	 * 
	 * @param basicFile
	 * @param testScenario
	 * @throws FileNotFoundException
	 */
	public static Basic readBasic(File basicFile) throws FileNotFoundException {
		
		basicDataByIdentifier = new HashMap<Integer, Basic>();
		
		Basic basicData = new Basic();
		
		Scanner basicScanner = new Scanner(basicFile);
		basicScanner.nextLine(); // Skip first line
		
		int courses = basicScanner.nextInt(); basicData.setCourses(courses);
		int rooms = basicScanner.nextInt(); basicData.setRooms(rooms);
		int days = basicScanner.nextInt(); basicData.setDays(days);
		int periods = basicScanner.nextInt(); basicData.setPeriods(periods);
		int curricula = basicScanner.nextInt(); basicData.setCurricula(curricula);
		int constraints = basicScanner.nextInt(); basicData.setConstraints(constraints);
		int lecturers = basicScanner.nextInt(); basicData.setLecturers(lecturers);
		
		if (printDataContent) {
			System.out.println("Basic data:");
			System.out.println("# courses: " + courses);
			System.out.println("# rooms: " + rooms);
			System.out.println("# days: " + days);
			System.out.println("# periods: " + periods);
			System.out.println("# curricula: " + curricula);
			System.out.println("# constriants: " + constraints);
			System.out.println("# lecturers: " + lecturers + "\n");
		}
		
		basicScanner.close();
		
		return basicData;
	}
	
	
	/**
	 * 
	 * @param courseFile
	 * @param testScenario
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Course> readCourses(File courseFile) throws FileNotFoundException {
		
		courseByIdentifier = new HashMap<String, Course>();
		
		
		ArrayList<Course> courseData = new ArrayList<Course>();
		Course courseTmp = new Course();
		
		Scanner courseScanner = new Scanner(courseFile);
		courseScanner.nextLine(); // Skip first line
		
		while (courseScanner.hasNextLine()) {
			
			String line = courseScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String courseId = lineScanner.next();
				courseTmp.setCourseId(courseId);
				String lecturer = lineScanner.next();
				courseTmp.setLecturer(lecturer);
				int lectures = lineScanner.nextInt();
				courseTmp.setLectures(lectures);
				lecturesTotal += lectures; // update lecture counter
				int workingDays = lineScanner.nextInt();
				courseTmp.setWorkingDays(workingDays);
				int students = lineScanner.nextInt();
				courseTmp.setStudents(students);
				courseTmp.setRoom(null); // no course is scheduled yet
				
				// Add data to arraylist
				courseData.add(courseTmp);
				courseByIdentifier.put(courseTmp.getCourseId(), courseTmp);
				courseTmp = new Course();
			}	
			lineScanner.close();
		}
		courseScanner.close();
		
		if (printDataContent) {
			// print courses
			int courseIndex = 1;
			System.out.println("Courses: ");
			for (int i = 0; i < courseData.size(); i++) {
				System.out.println("#" + courseIndex + ": " 
						+ courseData.get(i).getCourseId() + " " 
						+ courseData.get(i).getLecturer() + " "
						+ courseData.get(i).getLectures() + " "
						+ courseData.get(i).getWorkingDays() + " "
						+ courseData.get(i).getStudents()); 
				courseIndex++;
			}
		
			// print total number of lectures (all courses):
			System.out.println("Total number of lectures (all courses): " + lecturesTotal);
		
			System.out.println();
		}
		
		return courseData;
	}
	
	
	/**
	 * 
	 * @param curriculaFile
	 * @param testScenario
	 * @throws FileNotFoundException 
	 */
	public static ArrayList<Curriculum> readCurricula(File curriculaFile) throws FileNotFoundException {
		
		curriculumByIdentifier = new HashMap<String, Curriculum>();
		
		ArrayList<Curriculum> curriculumData = new ArrayList<Curriculum>();
		Curriculum curriculumTmp = new Curriculum();
		
		Scanner curriculaScanner = new Scanner(curriculaFile);
		curriculaScanner.nextLine(); // Skip first line
		
		while (curriculaScanner.hasNextLine()) {
			
			String line = curriculaScanner.nextLine();;
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String curriculum = lineScanner.next();
				curriculumTmp.setCurriculumId(curriculum);
				int courses = lineScanner.nextInt();
				curriculumTmp.setCourses(courses);

				// Add curriculum data to curriculum arraylist
				curriculumData.add(curriculumTmp);
				curriculumByIdentifier.put(curriculumTmp.getCurriculumId(), curriculumTmp);
				curriculumTmp = new Curriculum();
			}
			lineScanner.close();
		}
		curriculaScanner.close();
		
		if (printDataContent) {
			// print curricula
			int curriculumIndex = 1;
			System.out.println("Curricula: ");
			for (int i = 0; i < curriculumData.size(); i++) {
				System.out.println("#" + curriculumIndex + ": " + curriculumData.get(i).getCurriculumId() + " " 
															    + curriculumData.get(i).getCourses()); 
				curriculumIndex++;
			}
			System.out.println();
			}
		
		return curriculumData;		
	}
	
	
	/**
	 * 
	 * @param lecturesFile
	 * @param testScenario
	 * @throws FileNotFoundException 
	 */
	public static ArrayList<Lecturer> readLecturers(File lecturersFile) throws FileNotFoundException {

		ArrayList<Lecturer> lecturerData = new ArrayList<Lecturer>();
		Lecturer lecturerTmp = new Lecturer();
		
		Scanner lecturerScanner = new Scanner(lecturersFile);
		lecturerScanner.nextLine(); // Skip first line
		
		while (lecturerScanner.hasNextLine()) {
			
			String line = lecturerScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String lecturer = lineScanner.next();
				lecturerTmp.setLecturer(lecturer);

				// Add lecturer data to arraylist
				lecturerData.add(lecturerTmp);
				lecturerTmp = new Lecturer();
				
			}
			lineScanner.close();
		}
		lecturerScanner.close();
		
		if (printDataContent) {
			// print lecturers
			int lecturerIndex = 1;
			System.out.println("Lecturers: ");
			for (int i = 0; i < lecturerData.size(); i++) {
				System.out.println("#" + lecturerIndex + ": " + lecturerData.get(i).getLecturer()); 
				lecturerIndex++;
			}
			System.out.println();
			}
		
		return lecturerData;
	}
	
	
	/**
	 * 
	 * @param relationFile
	 * @param testScenario
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Relation> readRelation(File relationFile) throws FileNotFoundException {
		
		ArrayList<Relation> relationData = new ArrayList<Relation>();
		Relation relationTmp = new Relation();
		
		Scanner relationScanner = new Scanner(relationFile);
		relationScanner.nextLine(); // Skip first line
		
		while (relationScanner.hasNextLine()) {
			
			String line = relationScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String curriculum = lineScanner.next();
				relationTmp.setCurriculum(getCurriculumByIdentifier(curriculum));
				String course = lineScanner.next();
				relationTmp.setCourse(getCourseByIdentifier(course));

				// Add data to relation arraylist
				relationData.add(relationTmp);
				relationTmp = new Relation();

			}
			lineScanner.close();
		}
		relationScanner.close();
		
		if (printDataContent) {
			// print relations
			int relationIndex = 1;
			System.out.println("Relations: ");
			for (int i = 0; i < relationData.size(); i++) {
				System.out.println("#" + relationIndex + ": " + relationData.get(i).getCurriculum().getCurriculumId() + " " 
														  + relationData.get(i).getCourse().getCourseId()); 
				relationIndex++;
			}
			System.out.println();
		}
		
		return relationData;
	}
	
	
	/**
	 * 
	 * @param roomsFile
	 * @param testScenario
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Room> readRooms(File roomsFile) throws FileNotFoundException {
		
		roomByIdentifier = new HashMap<String, Room>();
		
		ArrayList<Room> roomData = new ArrayList<Room>();
		Room roomTmp = new Room();
		
		Scanner roomScanner = new Scanner(roomsFile);
		roomScanner.nextLine(); // Skip first line
		
		while (roomScanner.hasNextLine()) {
			
			String line = roomScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String roomId = lineScanner.next();
				roomTmp.setRoomId(roomId);
				int capacity = lineScanner.nextInt();
				roomTmp.setCapacity(capacity);

				// Add room data to arraylist
				roomData.add(roomTmp);
				
				roomTmp = new Room();
				roomByIdentifier.put(roomTmp.getRoomId(), roomTmp);
			}
			lineScanner.close();
		}
		roomScanner.close();
		
		if (printDataContent) {
			// print rooms
			int roomIndex = 1;
			System.out.println("Rooms: ");
			for (int i = 0; i < roomData.size(); i++) {
				System.out.println("#" + roomIndex + ": " + roomData.get(i).getRoomId() + " " 
													  	  + roomData.get(i).getCapacity()); 
				roomIndex++;
			}
			System.out.println();
		}
		
		return roomData;
	}
	
	
	
	public static ArrayList<Unavailability> readUnavailability(File unavailabilityFile) throws FileNotFoundException {
		
		ArrayList<Unavailability> unavailabilityData = new ArrayList<Unavailability>();
		Unavailability unavailabilityTmp = new Unavailability();
		
		Scanner unavailabilityScanner = new Scanner(unavailabilityFile);
		unavailabilityScanner.nextLine(); // Skip first line
		
		while (unavailabilityScanner.hasNextLine()) {
			
			String line = unavailabilityScanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			
			if (!lineEmpty(line)) {
//				System.out.println(line);
				String course = lineScanner.next();
				unavailabilityTmp.setCourse(getCourseByIdentifier(course));
				int day = lineScanner.nextInt();
				unavailabilityTmp.setDay(day);
				int period = lineScanner.nextInt();
				unavailabilityTmp.setPeriod(period);

				// Add unavailability data to arraylist
				unavailabilityData.add(unavailabilityTmp);
				unavailabilityTmp = new Unavailability();

			}
			lineScanner.close();
		}
		unavailabilityScanner.close();
		
		if (printDataContent) {
			// print unavailabilities
			int unavIndex = 1;
			System.out.println("Unavailabilities: ");
			for (int i = 0; i < unavailabilityData.size(); i++) {
				System.out.println("#" + unavIndex + ": " + unavailabilityData.get(i).getCourse().getCourseId() + " " 
													  	  + unavailabilityData.get(i).getDay() + " " 
													      + unavailabilityData.get(i).getPeriod()); 
				unavIndex++;
			}
			System.out.println();
		}
		
		return unavailabilityData;
	}
	
	
	/**
	 * Returns true if provided line is empty
	 * @param line
	 * @return
	 */
	public static boolean lineEmpty(String line) {
		boolean empty = false;
		if (line.isEmpty()) {
			empty = true;
		}
		return empty;
	}
	
	

	// GETTERS AND SETTERS FOR FILES
	private static File basicFile;
	private static File courseFile;
	private static File curriculaFile;
	private static File lecturersFile;
	private static File relationsFile;
	private static File roomsFile;
	private static File unavailabilityFile;
	

	public static File getBasicFile() {
		return basicFile;
	}

	public static File getCourseFile() {
		return courseFile;
	}

	public static File getCurriculaFile() {
		return curriculaFile;
	}

	public static File getLecturersFile() {
		return lecturersFile;
	}
	
	public static File getRelationsFile() {
		return relationsFile;
	}
	
	public static File getRoomsFile() {
		return roomsFile;
	}
	
	public static File getUnavailabilityFile() {
		return unavailabilityFile;
	}
	

	
	// GETTERS AND SETTERS FOR THE DATA
	// Basic Data
	public static Basic basicData;
	
	public static Basic getBasicData() {
		return basicData;
	}
	
	// Courses
	private static ArrayList<Course> courses;
	
	public static ArrayList<Course> getCourses() {
		return courses;
	}
	
	// Curricula
    public static ArrayList<Curriculum> curricula;
	
	public static ArrayList<Curriculum> getCurriculaData() {
		return curricula;
	}
	
	// Lecturers
	private static ArrayList<Lecturer> lecturers;
	
	public static ArrayList<Lecturer> getLecturers() {
		return lecturers;
	}
	
	// Relations
	private static ArrayList<Relation> relations;
	
	public static ArrayList<Relation> getRelations() {
		return relations;
	}
	
	// Relations Double ArrayList By Courses
	private static ArrayList<ArrayList<Relations>> relationsArrayListByCourses;
	
	public static ArrayList<ArrayList<Relations>> getRelationsArrayListByCourses() {
		return relationsArrayListByCourses;
	}
	
	// Relations Double ArrayList By Curricular
	private static ArrayList<ArrayList<Relations>> relationsArrayListByCurricula;
		
	public static ArrayList<ArrayList<Relations>> getRelationsArrayListByCurricula() {
		return relationsArrayListByCurricula;
	}
	
	// Rooms
	private static ArrayList<Room> rooms;
	
	public static ArrayList<Room> getRooms() {
		return rooms;
	}
	
	// Unavailability
	private static ArrayList<Unavailability> unavailabilities;
	
	public static ArrayList<Unavailability> getUnavailabilities() {
		return unavailabilities;
	}
	
	// Unavailability By Courses
	private static ArrayList<ArrayList<UnavailabilityByCourse>> unavailabilitiesByCourse;
	
	public static ArrayList<ArrayList<UnavailabilityByCourse>> getUnavailabilitiesBycourse() {
		return unavailabilitiesByCourse;
	}

	
	// HASH-MAP CALLING METHODS
	private static HashMap<Integer, Basic> basicDataByIdentifier;

	public static Basic getBasicDataByIdentifier(int testScenario) {
		return basicDataByIdentifier.get(testScenario);
	}
	
	
	private static HashMap<String, Course> courseByIdentifier;

	public static Course getCourseByIdentifier(String id) {
		return courseByIdentifier.get(id);
	}
	
	
	private static HashMap<String, Curriculum> curriculumByIdentifier;
	
	public static Curriculum getCurriculumByIdentifier(String id) {
		return curriculumByIdentifier.get(id);
	}
	
	
	private static HashMap<String, Room> roomByIdentifier;
	
	public static Room getRoomByIdentifier(String id) {
		return roomByIdentifier.get(id);
	}
	
	
	
	private static int lecturesTotal;


	public static int getLecturesTotal() {
		return lecturesTotal;
	}


}
