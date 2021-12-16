
public class Warehouse {
	
	// digital layout of the warehouse, 
	
	
	private int numberOfSections; //the last row/section is always the loading dock
	private int numberOfShelves; // per section on one side, either a positive or a negative number
	private double distanceSections; // distance between 2 nearest sections in centimeters
	private double distanceShelves; // distance between 2 nearest shelves/racks
	
	public Warehouse(int numberOfSections, int numberOfShelves, double distanceSections, double distanceShelves ) {
		this.numberOfSections = numberOfSections; 
		this.numberOfShelves = numberOfShelves; 
		this.distanceSections = distanceSections; 
		this.distanceShelves = distanceShelves;
	}
	
	public int getNumberOfShelves() {
		return numberOfShelves;
	}
	
	public void setNumberOfShelves(int numberOfShelves) {
		this.numberOfShelves = numberOfShelves;
	}

	public int getNumberOfSections() {
		return numberOfSections;
	}

	public void setNumberOfSections(int numberOfSections) {
		this.numberOfSections = numberOfSections;
	}

	public double getDistanceSections() {
		return distanceSections;
	}

	public void setDistanceSections(double distanceSections) {
		this.distanceSections = distanceSections;
	}

	public double getDistanceShelves() {
		return distanceShelves;
	}

	public void setDistanceShelves(double distanceShelves) {
		this.distanceShelves = distanceShelves;
	}

	
	// prototype warehouse from the video has the following attributes
	
	//distance between Sections = 35 cm 
	//distance between Shelves = 25 cm 
	//number of shelves = 1 per side 
	//number of sections = 4 ==> 3 rows with racks, the last one is the loading dock, robot starts at "position 0"
	
	
}
