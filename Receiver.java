import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;

import lejos.hardware.Bluetooth;
import lejos.hardware.motor.Motor;
import lejos.remote.nxt.NXTCommConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class Receiver {
	
	public static void main(String[] args) throws Exception{
		NXTCommConnector connector = Bluetooth.getNXTCommConnector();
		
		System.out.println("Waiting for connection ...");
		NXTConnection con = connector.waitForConnection(0, NXTConnection.RAW);
		System.out.println("Connected");
		
		DataInputStream dis = con.openDataInputStream();
		DataOutputStream dos = con.openDataOutputStream();
		
		Wheel rwheel = WheeledChassis.modelWheel(Motor.B, 1.5).offset(7.6);
		Wheel lwheel = WheeledChassis.modelWheel(Motor.C, 1.5).offset(-7.6);
		Chassis chassis = new WheeledChassis(new Wheel[]{rwheel, lwheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		MovePilot pilot = new MovePilot(chassis);
		
		byte[] n = new byte [8];
		
		byte stop = 0;
		byte forward = 1; 
		byte left = 2;
		byte right = 3;
		byte backwards = 4;
		
		while(true){
			try{
				if (dis.read(n) == -1)
					break;
			} catch (EOFException e)
			{
				break;
			}
			
			if(n[0] == stop){
				pilot.stop();
			}
			if(n[0] == forward){
				Motor.B.setSpeed(700);
				Motor.C.setSpeed(700);
				pilot.forward();
			}
			if(n[0] == left){
//				pilot.rotate(-15);
				Motor.B.setSpeed(500);
				Motor.C.setSpeed(500);
				Motor.B.forward();
				Motor.C.backward();
			}
			if(n[0] == right){
//				pilot.rotate(15);
				Motor.B.setSpeed(500);
				Motor.C.setSpeed(500);
				Motor.B.backward();
				Motor.C.forward();
			}
			if(n[0] == backwards){
				pilot.backward();
			}
			System.out.println("Read " + n[0] + n[1] + n[2] + n[3] + n[4] + n[5] + n[6] + n[7]);
			dos.write(n);
			dos.flush();
		}
		
		Delay.msDelay(1000);
		
		dis.close();
		dos.close();
		con.close();
		
	}

}
