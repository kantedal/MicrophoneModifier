package applications.the4casters.microphonemodifier.effects;

import android.util.Log;

import applications.the4casters.microphonemodifier.AudioPlayback;

/**
 * Created by Albin on 01/10/2015.
 */
public class ChangePitch extends AudioEffect {
    private int pitchMove;
    private double pitchScale;

    public ChangePitch(){
        effectType = AudioEffect.CHANGEPITCH;
        this.pitchScale = 1.5;
    }
    public void setPitch(double pitch) { this.pitchScale = pitch; }
    public double getPich() { return this.pitchScale; }

    @Override
    public void runEffect(double[] fft) {
        double[] temp = new double[fft.length];

        for(int i = 0; i < fft.length; i++)
        {
            temp[i] = fft[i];
        }
        if(pitchScale < 1)
            for(int i = 0; i < fft.length; i++)
            {
                fft[i] = temp[(int)(i*pitchScale)];
            }
        else
            for(int i = 0; i*pitchScale < fft.length; i++)
            {
                fft[i] = temp[(int)(i*pitchScale)];
            }




    }
}
