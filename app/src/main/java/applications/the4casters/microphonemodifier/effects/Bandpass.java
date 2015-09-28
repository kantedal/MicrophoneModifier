package applications.the4casters.microphonemodifier.effects;

/**
 * Created by filles-dator on 2015-09-28.
 */
public class Bandpass extends AudioEffect {
    private int low_pass;
    private int high_pass;

    public Bandpass(){
        effectType = AudioEffect.BANDPASS;

        this.low_pass = 0;
        this.high_pass = 0;
    }

    public void setLowPass(int low_pass){ this.low_pass = low_pass; }
    public void setHighPass(int high_pass) { this.high_pass = high_pass;}

    public int getLowPass() { return low_pass;}
    public int getHighPass() { return high_pass; }

    @Override
    public void runEffect(double[] fft) {
        for(int i = 0; i < fft.length; i++)
        {
            if(i < low_pass && i > high_pass)
                fft[i] = 0;
        }
    }
}
