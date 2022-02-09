// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveCommand;
import frc.robot.subsystems.DriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.MecanumControllerCommand;
import edu.wpi.first.wpilibj2.command.RamseteCommand;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public static final DriveSubsystem driveSubsystem = new DriveSubsystem();
  public static final DriveCommand driveCommand = new DriveCommand(driveSubsystem);
  public static Joystick gamepad = new Joystick(0);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    TrajectoryConfig config = new TrajectoryConfig(
      DriveSubsystem.maxSpeed,
      DriveSubsystem.maxAcceleration
    ).setKinematics(DriveSubsystem.kinematics);

    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
      new Pose2d(0, 0, new Rotation2d(0)),
      List.of(
        new Translation2d(1, 1),
        new Translation2d(2, -1)
      ),
      new Pose2d(3, 0, new Rotation2d(0)),
      config
    );
    MecanumControllerCommand command = new MecanumControllerCommand(
      exampleTrajectory,
      driveSubsystem::getPose,
      new SimpleMotorFeedforward(
        DriveSubsystem.kS,
        DriveSubsystem.kV,
        DriveSubsystem.kA
      ),
      // Position
      DriveSubsystem.kinematics,
      new PIDController(0.25, 0, 0),
      new PIDController(0.25, 0, 0),
      // Rotation
      new ProfiledPIDController(0.25, 0, 0, new TrapezoidProfile.Constraints(Math.PI, Math.PI)),
      DriveSubsystem.maxSpeed,
      // Velocity
      new PIDController(0.25, 0, 0),
      new PIDController(0.25, 0, 0),
      new PIDController(0.25, 0, 0),
      new PIDController(0.25, 0, 0),
      driveSubsystem::getWheelSpeeds,
      driveSubsystem::driveVolts,
      driveSubsystem
    );

    driveSubsystem.resetOdometry(exampleTrajectory.getInitialPose());
    return command.andThen(() -> driveSubsystem.stop());
  }
}