package applications.the4casters.microphonemodifier.effects;

/**
 * Created by filles-dator on 2015-09-28.
 */
public abstract class AudioEffect {
    public static final int BANDPASS = 0;
    public static final int ECHO = 1;
    public static final int ROBOTIC = 2;

    protected int effectType;
    public int getEffectType(){ return effectType; }

    abstract public void runEffect(double[] fft);
}
