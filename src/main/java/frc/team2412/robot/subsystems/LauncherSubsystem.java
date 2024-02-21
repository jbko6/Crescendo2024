package frc.team2412.robot.subsystems;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkAbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.SparkPIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team2412.robot.Hardware;
import frc.team2412.robot.util.SparkPIDWidget;
import java.util.Map;

public class LauncherSubsystem extends SubsystemBase {
	// CONSTANTS
	// ANGLE VALUES
	public static final int SUBWOOFER_AIM_ANGLE = 54;
	public static final int PODIUM_AIM_ANGLE = 39;
	// MOTOR VALUES
	// max Free Speed: 6784 RPM
	private static final int MAX_FREE_SPEED_RPM = 6784;
	public static final double ANGLE_TOLERANCE = 0.5;
	// RPM
	public static final int SPEAKER_SHOOT_SPEED_RPM = 3392; // 50%
	// 3392 RPM = 50% Speed
	// 1356 RPM = 20% Speed
	// 1017 RPM = 15% Speed

	// HARDWARE
	private final CANSparkFlex launcherTopMotor;
	private final CANSparkFlex launcherBottomMotor;
	private final CANSparkFlex launcherAngleMotor;
	private final RelativeEncoder launcherTopEncoder;
	private final RelativeEncoder launcherBottomEncoder;
	private final SparkAbsoluteEncoder launcherAngleEncoder;
	private final SparkPIDController launcherAnglePIDController;
	private final SparkPIDController launcherTopPIDController;
	private final SparkPIDController launcherBottomPIDController;

	private final GenericEntry setLauncherSpeedEntry =
			Shuffleboard.getTab("Launcher")
					.addPersistent("Launcher Speed setpoint", SPEAKER_SHOOT_SPEED_RPM)
					.withSize(3, 1)
					.withWidget(BuiltInWidgets.kNumberSlider)
					.withProperties(Map.of("Min", -MAX_FREE_SPEED_RPM, "Max", MAX_FREE_SPEED_RPM))
					.getEntry();

	private final GenericEntry launcherAngleEntry =
			Shuffleboard.getTab("Launcher")
					.add("Launcher angle", 0)
					.withSize(2, 1)
					.withWidget(BuiltInWidgets.kTextView)
					.getEntry();
	private final GenericEntry launcherSpeedEntry =
			Shuffleboard.getTab("Launcher")
					.add("Launcher Speed", 0)
					.withSize(1, 1)
					.withWidget(BuiltInWidgets.kTextView)
					.getEntry();

	private final GenericEntry launcherAngleSpeedEntry =
			Shuffleboard.getTab("Launcher")
					.add("Launcher angle Speed", 0)
					.withSize(2, 1)
					.withWidget(BuiltInWidgets.kTextView)
					.getEntry();
	// Constructor
	public LauncherSubsystem() {

		// MOTOR INSTANCE VARIBLES
		// motors
		launcherTopMotor = new CANSparkFlex(Hardware.LAUNCHER_TOP_MOTOR_ID, MotorType.kBrushless);
		launcherBottomMotor = new CANSparkFlex(Hardware.LAUNCHER_BOTTOM_MOTOR_ID, MotorType.kBrushless);
		launcherAngleMotor = new CANSparkFlex(Hardware.LAUNCHER_ANGLE_MOTOR_ID, MotorType.kBrushless);
		// encoders
		launcherTopEncoder = launcherTopMotor.getEncoder();
		launcherBottomEncoder = launcherBottomMotor.getEncoder();
		launcherAngleEncoder = launcherAngleMotor.getAbsoluteEncoder(Type.kDutyCycle);

		// PID controllers
		// Create launcherTopPIDController and launcherTopMotor]
		launcherTopPIDController = launcherTopMotor.getPIDController();
		launcherTopPIDController.setFeedbackDevice(launcherTopEncoder);
		launcherBottomPIDController = launcherBottomMotor.getPIDController();
		launcherBottomPIDController.setFeedbackDevice(launcherBottomEncoder);
		launcherAnglePIDController = launcherAngleMotor.getPIDController();
		launcherAnglePIDController.setFeedbackDevice(launcherAngleEncoder);
		Shuffleboard.getTab("Launcher")
				.add(new SparkPIDWidget(launcherAnglePIDController, "launcherAnglePIDController"));
		Shuffleboard.getTab("Launcher")
				.add(new SparkPIDWidget(launcherTopPIDController, "launcherTopPIDController"));
		Shuffleboard.getTab("Launcher")
				.add(new SparkPIDWidget(launcherBottomPIDController, "launcherBottomPIDController"));
	}

	public void configMotors() {
		launcherTopMotor.restoreFactoryDefaults();
		launcherBottomMotor.restoreFactoryDefaults();
		launcherAngleMotor.restoreFactoryDefaults();
		// idle mode (wow)
		launcherTopMotor.setIdleMode(IdleMode.kCoast);
		launcherBottomMotor.setIdleMode(IdleMode.kCoast);
		launcherAngleMotor.setIdleMode(IdleMode.kBrake);
		// inveritng the bottom motor lmao
		launcherBottomMotor.setInverted(true);

		// current limit
		launcherTopMotor.setSmartCurrentLimit(20);
		launcherBottomMotor.setSmartCurrentLimit(20);
		launcherAngleMotor.setSmartCurrentLimit(20);

		launcherAngleMotor.setSoftLimit(CANSparkBase.SoftLimitDirection.kForward, 100);
		launcherAngleMotor.setSoftLimit(CANSparkBase.SoftLimitDirection.kReverse, 2);

		launcherTopMotor.burnFlash();
		launcherBottomMotor.burnFlash();
		launcherAngleMotor.burnFlash();

		// PID
		launcherAnglePIDController.setP(0.1);
		launcherAnglePIDController.setI(0);
		launcherAnglePIDController.setD(0);
		launcherAnglePIDController.setFF(0);

		launcherTopPIDController.setP(0.1);
		launcherTopPIDController.setI(0);
		launcherTopPIDController.setD(0);
		launcherTopPIDController.setFF(0);

		launcherBottomPIDController.setP(0.1);
		launcherBottomPIDController.setI(0);
		launcherBottomPIDController.setD(0);
		launcherBottomPIDController.setFF(0);
	}
	// stop launcher motors method
	public void stopLauncher() {
		launcherTopMotor.stopMotor();
		launcherBottomMotor.stopMotor();
	}
	// uses the value from the entry
	public void launch() {
		double speed = setLauncherSpeedEntry.getDouble(SPEAKER_SHOOT_SPEED_RPM);
		launcherTopPIDController.setReference(speed, ControlType.kVelocity);
		launcherBottomPIDController.setReference(speed, ControlType.kVelocity);
	}
	// used for presets
	public void launch(double speed) {
		launcherTopPIDController.setReference(speed, ControlType.kVelocity);
		launcherBottomPIDController.setReference(speed, ControlType.kVelocity);
		setLauncherSpeedEntry.setDouble(speed);
	}

	public double getLauncherSpeed() {
		return launcherTopEncoder.getVelocity();
	}
	// returns the degrees of the angle of the launcher
	public double getAngle() {
		// get position returns a double in the form of rotations
		return Units.rotationsToDegrees(launcherAngleEncoder.getPosition());
	}

	public void setAngle(double angle) {
		launcherAnglePIDController.setReference(Units.degreesToRotations(angle), ControlType.kPosition);
	}

	public double getAngleSpeed() {
		return launcherAngleEncoder.getVelocity();
	}

	public void setAngleSpeed(double Speed) {
		launcherAnglePIDController.setReference(Speed, ControlType.kPosition);
	}

	@Override
	public void periodic() {
		launcherAngleEntry.setDouble(getAngle());
		launcherSpeedEntry.setDouble(getLauncherSpeed());
		launcherAngleSpeedEntry.setDouble(getAngleSpeed());
	}
}
