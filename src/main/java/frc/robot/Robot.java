/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
//import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  public static DriveTrain driveTrain;
  public static Intake intake;
  Climbing climbing;

  Joystick joy;
  ADXRS450_Gyro gyro;
  ControlPanel controlPanel;

  NetworkTable limeTable;
  NetworkTable mainTable;

  public static boolean crossedLine = false;

  public static String gameData;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    // Initialize Drive Subsystem
    driveTrain = new DriveTrain();

    // Initialize Intake Subsystem
    intake = new Intake();
    intake.setupShooterMotorConfigs();
    
    // Initialize Climbing Subsystem
    climbing = new Climbing();
    climbing.PIDSetup();

    // Initialize Joystick
    joy = new Joystick(0);
    gyro = new ADXRS450_Gyro();

    controlPanel = new ControlPanel();

    limeTable = NetworkTableInstance.getDefault().getTable("limelight");

    // Setting Camera view for Shuffleboard
    limeTable.getEntry("stream").setNumber(2.0);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    climbing.printClimbThing();
    controlPanel.encoder();
    climbing.checkEncoderReset();
    limeTable.getEntry("stream").setNumber(2.0);
    driveTrain.putEncoders();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    DriveTrain.startBackUpTime = System.currentTimeMillis();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
      if(crossedLine) {
        driveTrain.targetGoalWithRange(joy);
        // Anything else that happens during auto goes here.
      } else {
        driveTrain.backUp(); 
      }
  }
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    climbing.checkClimb(joy.getPOV());



    if(joy.getRawButton(3)) {
      climbing.setRaiserUp();
    }
    else if(joy.getRawButton(4)) {
      climbing.setRaiserDown();
    }
    else if(joy.getRawButton(8)) {
      climbing.raiseArmToPosition();
    }
    else if(joy.getRawButton(7)) {
      climbing.armReset();
    }
    else {
      climbing.stopRaiser();
    }
    

    if (joy.getTrigger()){
      limeTable.getEntry("ledMode").setNumber(3);
      driveTrain.targetGoal(joy); 
    }
    else {
      if(joy.getRawButton(10)) {
        intake.setFullConvey(true);
      }
      else {
        intake.setFullShoot(false);
        intake.setFullConvey(false);
        intake.spinUpShooter(false, 0);
        intake.checkIntake(joy.getRawButton(2), joy.getRawButton(9));   
      }
      driveTrain.mecDrive(joy);
      driveTrain.resetErrors();
      limeTable.getEntry("ledMode").setNumber(1);
    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    
  }
}