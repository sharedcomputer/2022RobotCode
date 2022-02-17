package frc.robot.commands.playback;

public class PlaybackStep {
    // NOTE: This class is JSON-serialized and deserialized!

    // in seconds
    public double time = 0;

    // in [-1, 1]
    public double inputX = 0;
    public double inputY = 0;
    public double inputZ = 0;

    // in raw sensor units
    public double targetFrontLeft = 0;
    public double targetFrontRight = 0;
    public double targetBackLeft = 0;
    public double targetBackRight = 0;
    public double positionFrontLeft = 0;
    public double positionFrontRight = 0;
    public double positionBackLeft = 0;
    public double positionBackRight = 0;
    public double velocityFrontLeft = 0;
    public double velocityFrontRight = 0;
    public double velocityBackLeft = 0;
    public double velocityBackRight = 0;
}
