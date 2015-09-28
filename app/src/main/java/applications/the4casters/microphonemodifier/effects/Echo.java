package applications.the4casters.microphonemodifier.effects;

import java.util.ArrayList;

/**
 * Created by filles-dator on 2015-09-28.
 */
public class Echo extends AudioEffect {
    private int delay;
    private int counter;
    private double echo_strength;
    private ArrayList<double[]> buffers;

    public Echo(){
        effectType = AudioEffect.ECHO;
        buffers = new ArrayList<>();
        this.delay = 10;
        this.counter = 0;
        this.echo_strength = 0.2;
    }

    public void setDelay(int delay){ this.delay = delay; this.counter = 0; }
    public void setStrength(double strength){ this.echo_strength = strength; }
    public int getDelay() { return this.delay; }
    public double getStrength() { return this.echo_strength; }

    @Override
    public void runEffect(double[] fft) {
        if(counter > delay)
        {
            buffers.remove(0);

            double[] fft2 = new double[fft.length];

            for(int i = 0; i < fft.length; i++)
            {
                fft2[i] = fft[i];
                fft[i] = fft[i] + echo_strength * (buffers.get(0))[i];
            }
            buffers.add(fft2);
        }
        else
        {
            buffers.add(fft);
        }
        counter++;
    }
}