// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveSubsystem;

public class RobotContainer {
  private SwerveSubsystem m_Swerve;

  public RobotContainer() {
    initSubsystems();
    configureBindings();
  }

  private void initSubsystems() {
    m_Swerve = SwerveSubsystem.configure();
  }

  private void configureBindings() {}

  public Command getAutonomousCommand() {
    return null;
  }
}
