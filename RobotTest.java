

public class RobotTest {

	public static void main(String[] args) {
		
		//UI will be provided in the future, until then, tasks can be given and adjusted
		// via setters/constructors in this java file. 
		
		// the following setup is the one show-cased in the video attached 
		//in Project Report appendices :-) 
	
		Warehouse warehousePrototype = new Warehouse(4,1,35,25); 
		WarehouseRobot robotPrototype = new WarehouseRobot(3,-1, warehousePrototype);
		robotPrototype.execute();
		
		// number of sections = 4 ( three rows with racks, the last one is for loading/unloading
		// number of shelves on one side per section = 1
		// distance between shelves = 25 (or between the middle points under the racks) 
		// distance between sections = 35
		// robot goes to location [3,-1] => section three, first shelf on the left side
		
	}

}
