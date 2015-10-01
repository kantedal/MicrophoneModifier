package applications.the4casters.microphonemodifier.effects;

import applications.the4casters.microphonemodifier.AudioPlayback;

/**
 * Created by filles-dator on 2015-09-28.
 */
public class Bandpass extends AudioEffect {
    private int low_pass;
    private int high_pass;

    public Bandpass(){
        effectType = AudioEffect.BANDPASS;

        this.low_pass = AudioPlayback.frequencyToIndex(500);
        this.high_pass = AudioPlayback.frequencyToIndex(3000);
    }

    public void setLowPass(int low_pass){ this.low_pass = AudioPlayback.frequencyToIndex(low_pass); }
    public void setHighPass(int high_pass) { this.high_pass = AudioPlayback.frequencyToIndex(high_pass);}

    public int getLowPass() { return AudioPlayback.indexToFrequency(low_pass);}
    public int getHighPass() { return AudioPlayback.indexToFrequency(high_pass); }

    @Override
    public void runEffect(double[] fft) {
        for(int i = 0; i < fft.length; i++)
        {
            if(i <= low_pass || i >= high_pass)
                fft[i] = 0;
        }
    }
}
