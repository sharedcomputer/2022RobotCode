package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

public class BetterSparkMaxBrushless {
  private CANSparkMax canSparkMax;
  private SparkMaxPIDController pidController;
  private RelativeEncoder encoder;

  public BetterSparkMaxBrushless(int id) {
    canSparkMax = new CANSparkMax(id, MotorType.kBrushless);
    encoder = canSparkMax.getEncoder();
    pidController = canSparkMax.getPIDController();
    zero();
  }

  public BetterSparkMaxBrushless configureInverted(boolean inverted) {
    canSparkMax.setInverted(true);
    return this;
  }

  public void setPower(double power) {
    canSparkMax.set(power);
  }

  public void stop() {
    canSparkMax.stopMotor();
  }

  public void setRPM(double rpm) {
    pidController.setReference(rpm, ControlType.kVelocity);
  }
  public double getRPM() {
    return encoder.getVelocity();
  }

  private double zero;
  public void zero() {
    zero = encoder.getPosition();
  }
  public void setPosition(double turns) {
    pidController.setReference(zero + turns, ControlType.kPosition);
  }
  public double getPosition() {
    return encoder.getPosition();
  }

  public void configurePID(PIDSettings pidSettings) {
    pidController.setP(pidSettings.kp);
    pidController.setI(pidSettings.ki);
    pidController.setD(pidSettings.kd);
  }

  public void setMaxPIDOutput(double output) {
    pidController.setOutputRange(-output, output);
  }

  public CANSparkMax getCanSparkMax() {
    return canSparkMax;
  }
}
