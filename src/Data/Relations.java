package Data;

import java.util.ArrayList;

public class Relations {
	
	// Declarations of variables
	String curriculumId;
	String courseId;
	
	private static ArrayList<ArrayList<Relations>> relationsArrayListByCurricula;
	
	private static ArrayList<ArrayList<Relations>> relationsArrayListByCourses;
	
	
	// Print data content?
	private static boolean printDataContent = true;

	public static boolean isPrintDataContent() {
		return printDataContent;
	}

	public static void setPrintDataContent(boolean printDataContent) {
		Relations.printDataContent = printDataContent;
	}


	/**
	 * Adds all relations records from relationsFile to a double ArrayList.
	 * @param relationData Relations data obtained from relationsFile.
	 * @return relationsArrayList Updated double arrayList of relations records. 
	 */
	public static ArrayList<ArrayList<Relations>> addRelationsToArrayListByCurricula(
			ArrayList<ArrayList<Relations>> relationsArrayList, ArrayList<Relation> relationData) {
		
		for (int i = 0; i < relationData.size(); i++) {
			
			String curriculumId = relationData.get(i).getCurriculum().getCurriculumId();
			String courseId = relationData.get(i).getCourse().getCourseId();
		
			// Extract curriculum number used as identifier for the arrayList of relation
			int curriculumNumber = Integer.parseInt(curriculumId.substring(1, curriculumId.length()));
			
			Relations relationsTmp = new Relations();
		
			// Set relations data for the curriculum and courses
			relationsTmp.setCurriculumId(curriculumId);
			relationsTmp.setCourseId(courseId);
		
			// Add positions data to arrayList
			relationsArrayList.get(curriculumNumber).add(relationsTmp);
			
		}
		
		// Print relationsArrayList
		if (printDataContent) {
			printCoursePositions(relationsArrayList);
		}
		
		return relationsArrayList;
	}
	
	
	/**
	 * Adds all relations records from relationsFile to a double ArrayList.
	 * @param relationData Relations data obtained from relationsFile.
	 * @return relationsArrayList Updated double arrayList of relations records. 
	 */
	public static ArrayList<ArrayList<Relations>> addRelationsToArrayListByCourses (
			ArrayList<ArrayList<Relations>> relationsArrayListByCourses, ArrayList<Relation> relationData) {
		
			for (int i = 0; i < relationData.size(); i++) {
			
			String curriculumId = relationData.get(i).getCurriculum().getCurriculumId();
			String courseId = relationData.get(i).getCourse().getCourseId();
		
			// Extract curriculum number used as identifier for the arrayList of relation
			int courseNumber = Integer.parseInt(courseId.substring(1, courseId.length()));
			
			Relations relationsTmp = new Relations();
		
			// Set relations data for the curriculum and courses
			relationsTmp.setCurriculumId(curriculumId);
			relationsTmp.setCourseId(courseId);
		
			// Add positions data to arrayList
			relationsArrayListByCourses.get(courseNumber).add(relationsTmp);
			
		}

		if (printDataContent) {
			printCoursePositions(relationsArrayListByCourses);
		}
		
		return relationsArrayListByCourses;
	}
	
	
	/**
	 * Prints the arrayList containing all relations records based on the curriculum id.
	 * @param relationsArrayList ArrayList to be printed.
	 */
	public static void printCoursePositions(ArrayList<ArrayList<Relations>> relationsArrayList) {

		System.out.println("\n\n--------------------------------------------------");
		System.out.println("Printing relations arrayList data:\n");

		System.out.println("Comment: Data printed in the following order: ");
		System.out.println("1. curriculum index");
		System.out.println("2. course index\n");

		// For all curricular arrayLists
		for (int i = 0; i < relationsArrayList.size(); i++) {

			// For all relations records in curricular arrayList
			for (int c = 0; c < relationsArrayList.get(i).size(); c++) {

				Relations currentRelation = relationsArrayList.get(i).get(c);
				System.out.print("[" + currentRelation.getCurriculumId() + ", "
							         + currentRelation.getCourseId() + "] ");
				
				if (c == relationsArrayList.get(i).size() - 1) {
					System.out.println();
				}
			}
		}

		System.out.println("--------------------------------------------------\n\n");
	}
	
	
	/**
	 * Initialises an empty double arrayList to store the courseIds to all relations of curricular.
	 * @param numberOfRelations Total number of relations in relationsFile.
	 * @return relationsArrayListByCourses Double arrayList.
	 */
	public static ArrayList<ArrayList<Relations>> setRelationsArrayListByCourses(int numberOfRelations) {
		
		ArrayList<ArrayList<Relations>> relationsArrayListByCourses = new ArrayList<ArrayList<Relations>>();
		
		for (int c = 0; c < numberOfRelations; c++) {
			ArrayList<Relations> relation = new ArrayList<Relations>();
			relationsArrayListByCourses.add(relation);
		}
		
		return relationsArrayListByCourses;
	}
	
	
	/**
	 * Initialises an empty double arrayList to store the courseIds to all relations of curricular.
	 * @param numberOfRelations Total number of relations in relationsFile.
	 * @return relationsArrayListByCurricula Double arrayList.
	 */
	public static ArrayList<ArrayList<Relations>> setRelationsArrayListByCurricula(int numberOfRelations) {
		
		ArrayList<ArrayList<Relations>> relationsArrayListByCurricula = new ArrayList<ArrayList<Relations>>();
		
		for (int c = 0; c < numberOfRelations; c++) {
			ArrayList<Relations> relation = new ArrayList<Relations>();
			relationsArrayListByCurricula.add(relation);
		}
		
		return relationsArrayListByCurricula;
	}
	
	
	// Getters & Setters
	public String getCurriculumId() {
		return curriculumId;
	}
	
	public void setCurriculumId(String curriculumId) {
		this.curriculumId = curriculumId;
	}
	
	
	public String getCourseId() {
		return courseId;
	}
	
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	public static ArrayList<ArrayList<Relations>> getRelationsArrayListByCurricula() {
		return relationsArrayListByCurricula;
	}
	
	public static ArrayList<ArrayList<Relations>> getRelationsArrayListByCourses() {
		return relationsArrayListByCourses;
	}
	
}
