
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class WarehouseRobot {
	
	private int desiredSection; 
	private int desiredShelf;  
	private Warehouse warehouse; 
	
	private boolean right; // tells us in what part of the warehouse the desired rack is located
	private final int TARGETPATH = 360; // raw light reflected on the edge of floor and black line
										// normalized value worked better for line following than the % value
	
	private final double Kp = 0.5; //proportional variable, the higher the number, the sharper the steer
	private final int BLACK = 29; // light reflected (%) on the black line
	private final int turn = 90; 
	private final int YELLOW = 45; // light reflected (%) on the yellow line
	
	private final MotorPort MOTOR_C = MotorPort.C;
	private final LightSensor LIGHT = new LightSensor(SensorPort.S1, false); 
	private final DifferentialPilot PILOT = new DifferentialPilot(5.6f, 13.5f, Motor.B, Motor.A, false); 
	private final UltrasonicSensor ULTRASONIC = new UltrasonicSensor(SensorPort.S2); 

	
	
	public WarehouseRobot(int desiredSection, int desiredShelf, Warehouse warehouse) {
		this.warehouse = warehouse;
		this.desiredSection = desiredSection; 
		this.desiredShelf = desiredShelf; 
		right = desiredShelf > 0; // decides whether the robot is on right or left side of the warehouse 
		 
	}

	public void setDeisredSection(int section) {
		desiredSection = section;
	}
	
	public void setDesiredShelf(int shelf) {
		desiredShelf = shelf; 
		right = shelf > 0;
	}
	
	public void liftShelf(boolean up) {
		
		int tachosTurn = 100;  
		int direction; 
		MOTOR_C.resetTachoCount();
		
		if (up) direction = 1;     // 1 - forward, 2 - backward
		else direction = 2;
		
		while (Math.abs(MOTOR_C.getTachoCount()) < tachosTurn) {
			 MOTOR_C.controlMotor(65, direction); 
		}
		
		MOTOR_C.controlMotor(0, 3);
		
		// to celebrate successful rack-lifting some beeping  :) 
		for (int i = 0; i < 4; i++) {
			Sound.twoBeeps();
		}
		
		
	}
	
	// two methods for proportional line-following system
	
	public void followLine() {
		
		ULTRASONIC.continuous();
		LIGHT.setFloodlight(true); // turn LED on
		PILOT.setTravelSpeed(10);
		
		if (ULTRASONIC.getDistance() > 20) {
			
			PILOT.steer((LIGHT.readNormalizedValue()  - TARGETPATH) * Kp);
			
		}
		else {
			
			PILOT.stop();
			Sound.beepSequence();
			
		}
		
	}
	
	
	public void followLine(double distance) {
		
		ULTRASONIC.continuous();
		LIGHT.setFloodlight(true);  
		PILOT.setTravelSpeed(10);
		Motor.B.resetTachoCount(); // starting distance at 0 
		double start = 0; 
	
		while (start <= distance) { 
			
			// obstacle avoidance system, the robot stops and beeps 
			//if it detects an object closer than 20 cm
			
			if (ULTRASONIC.getDistance() > 20) {  
				
				PILOT.steer((LIGHT.readNormalizedValue() - TARGETPATH)*Kp);
				start = Motor.B.getTachoCount()*2*Math.PI*2.8/360;
				
			}
			
			else {
				
				PILOT.stop();
				Sound.beepSequence();
				
			}
			
		}
		
		PILOT.stop(); 
		LIGHT.setFloodlight(false);
	
	}
	
	public void goToSection() {
		
		double distance = warehouse.getDistanceSections() * desiredSection; 
		followLine(distance);   
		
	 }
	
	public void goUnderShelf(int num, boolean up) {
		
		/* num is 1 or -1 
		 * if 1: robot is on the right side, thus behaves accordingly
		 */
		
		ULTRASONIC.off(); // when going under the shelf, the obstacle avoiding mechanism is turned off to save battery
		PILOT.setRotateSpeed(40);
		PILOT.setTravelSpeed(7); // slowing down for increased safety
		PILOT.rotate(turn*num);
		LIGHT.setFloodlight(true);
		
		// the robot stops when it detects yellow, meaning it reached the point under the rack
		while (LIGHT.getLightValue() < YELLOW) {
			PILOT.forward();
		}
		
		PILOT.stop();
		
		liftShelf(up);
		
		// the robot stops when it detects the black line , therefore we are back on track
		while (LIGHT.getLightValue() > BLACK) {
			PILOT.backward();
		}
		
		PILOT.stop();
		PILOT.rotate(turn*num);
		
	}
	
	public void goToShelf() {
	
		PILOT.setRotateSpeed(40);
		
		
		if (right) PILOT.rotate(-turn); //negative number rotates clockwise (right) - I keep forgetting this
		
		else {
			PILOT.rotate(turn);
			desiredShelf = Math.abs(desiredShelf);  // number of the shelf turned into
		}
								  					// absolute value to be able to calculate the distance without messing up the direction
		
		
		 
		double distance = warehouse.getDistanceShelves() * desiredShelf; 
		followLine(distance); 
		
		
		if(right) { 
			// execution of the tasks when the robot is on the right side 
			goUnderShelf(1, true);
			followLine(distance);
			PILOT.rotate(-turn);
			
		}
		else {
			// execution of the tasks when the robot is on the left side
			goUnderShelf(-1, true);
			followLine(distance);
			PILOT.rotate(turn);
		
	}
}
	
	public void goToLoadingDock() {
		
		PILOT.setRotateSpeed(45);
		PILOT.setTravelSpeed(10);
									// total number of sections - the current one times the distance between each 
		double distanceToLastRow = (warehouse.getNumberOfSections() - desiredSection) * warehouse.getDistanceSections(); 
		
		followLine(distanceToLastRow);
		PILOT.rotate(-turn); 
		
		double distanceToDock = (warehouse.getNumberOfShelves()+ 1)* warehouse.getDistanceShelves(); 
		followLine(distanceToDock);
		PILOT.stop();
		
		// waits for the staff to empty/load the rack :), until a button is pressed to let the robot know
		// that it can proceed with execution
		
		Button.waitForAnyPress();
		
		PILOT.travel(-distanceToDock); 
		
		PILOT.rotate(-turn);
		
		PILOT.stop(); 
	
	}
	
	public void putShelfBack() { 

		PILOT.setRotateSpeed(45);
	
		double distanceToSection = (warehouse.getNumberOfSections() - desiredSection) * warehouse.getDistanceSections(); 
		followLine(distanceToSection);
	
		
		if (right) PILOT.rotate(turn);
		
		else PILOT.rotate(-turn);
		
		
		double distancetoShelf = desiredShelf * warehouse.getDistanceShelves(); 
		
		followLine(distancetoShelf); 

		if (right) goUnderShelf(1, false); 
		else goUnderShelf(-1, false); 
			
		
		
	}
	
	public void goHome() {
		
		PILOT.setRotateSpeed(45);
		PILOT.setTravelSpeed(10);
		
		
		double distanceToMain = desiredShelf * warehouse.getDistanceShelves();
		
		followLine(distanceToMain);
		
		if (right) PILOT.rotate(turn);
		
		else PILOT.rotate(-turn);
		
		double distanceToHome = desiredSection * warehouse.getDistanceSections();
				
		followLine(distanceToHome);
		
		PILOT.stop();
		Sound.beepSequenceUp();
		
		
	}
	
	public void execute() {
		// once everything is set, call only this method in the test class :) 
		
		goToSection(); 
		goToShelf(); 
		goToLoadingDock(); 
		putShelfBack(); 
		goHome(); 
		
	}
	


}
