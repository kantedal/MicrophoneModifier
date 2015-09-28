package applications.the4casters.microphonemodifier.effects;

/**
 * Created by filles-dator on 2015-09-28.
 */
public class Echo extends AudioEffect {
    private int delay;

    public Echo(){
        effectType = AudioEffect.ECHO;

        this.delay = 500;
    }

    public void setDelay(int delay){ this.delay = delay; }
    public int getDelay() { return this.delay; }

    @Override
    public double[] runEffect(double[] fft) {
        return new double[0];
    }
}