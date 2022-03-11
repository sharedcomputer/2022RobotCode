package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

public class BetterTalonFX {
    public static double rpmToSensorVelocity(double rpm) {
        double revolutionsPerSecond = rpm / 60.0;
        double sensorUnitsPerSecond = revolutionsPerSecond * 2048.0;
        double sensorUnitsPer100MS = sensorUnitsPerSecond / 10.0;
        return sensorUnitsPer100MS;
    }

    public static double sensorVelocityToRPM(double sensorUnitsPer100MS) {
        double sensorUnitsPerSecond = sensorUnitsPer100MS * 10.0;
        double revolutionsPerSecond = sensorUnitsPerSecond / 2048.0;
        double revolutionsPerMinute = revolutionsPerSecond * 60.0;
        return revolutionsPerMinute;
    }

    private final WPI_TalonFX talon;

    public SimpleMotorFeedforward feedforward = null;
    public PIDController linearVelocityPID = null;
    private double metersPerRevolution = -1;

    public BetterTalonFX(int port) {
        talon = new WPI_TalonFX(port);
        talon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        zeroFixedEncoderTarget();
    }

    public WPI_TalonFX getWPI() {
        return talon;
    }

    public void setInverted(boolean inverted) {
        talon.setInverted(inverted);
    }

    public void stop() {
        talon.stopMotor();
    }

    // Percent in [-1, 1]
    public void setPower(double percent) {
        talon.set(ControlMode.PercentOutput, percent);
    }

    public double getRPM() {
        return sensorVelocityToRPM(talon.getSelectedSensorVelocity());
    }
    public void setRPM(double rpm) {
        talon.set(ControlMode.Velocity, rpmToSensorVelocity(rpm));
    }

    public double getLinearVelocity() {
        if (metersPerRevolution == -1) {
            throw new IllegalStateException("metersPerRevolution not configured");
        }
        return getRPM() / 60.0 * metersPerRevolution;
    }
    public void setLinearVelocity(double metersPerSecond) {
        if (metersPerRevolution == -1) {
            throw new IllegalStateException("metersPerRevolution not configured");
        }
        if (feedforward == null) {
            throw new IllegalStateException("feedforward not configured");
        }
        if (linearVelocityPID == null) {
            throw new IllegalStateException("PID not configured");
        }
        setVoltage(feedforward.calculate(metersPerSecond) + linearVelocityPID.calculate(getLinearVelocity(), metersPerSecond));
    }

    public void setVoltage(double volts) {
        talon.setVoltage(volts);
    }

    private double fixedEncoderTargetZero = 0;
    private double fixedEncoderTarget = 0;
    public double getFixedEncoderTarget() {
        return fixedEncoderTarget;
    }
    public void setFixedEncoderTarget(double target) {
        fixedEncoderTarget = target;
        talon.set(ControlMode.Position, fixedEncoderTargetZero + fixedEncoderTarget);
    }
    public void zeroFixedEncoderTarget() {
        fixedEncoderTargetZero = talon.getSelectedSensorPosition();
        fixedEncoderTarget = 0;
    }

    public double getPositionSensorUnits() {
        return talon.getSelectedSensorPosition() - fixedEncoderTargetZero;
    }

    public void configurePID(double kp, double ki, double kd) {
        linearVelocityPID = new PIDController(kp, ki, kd);
        talon.config_kP(0, kp);
        talon.config_kI(0, ki);
        talon.config_kD(0, kd);
    }
    public void configurePID(PIDSettings pid) {
        configurePID(pid.kp, pid.ki, pid.kd);
    }

    public void configureFeedforward(SimpleMotorFeedforward feedforward) {
        this.feedforward = feedforward;
    }

    public void configureDistancePerRevolution(double meters) {
        this.metersPerRevolution = meters;
    }

    public void configureRamp(double secondsFromNeutralToFull) {
        talon.configOpenloopRamp(secondsFromNeutralToFull);
        talon.configClosedloopRamp(secondsFromNeutralToFull);
    }
}
