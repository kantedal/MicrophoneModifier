package applications.the4casters.microphonemodifier.effects;

import android.util.Log;

/**
 * Created by filles-dator on 2015-09-30.
 */
public class Robot extends AudioEffect{

    private int n1 = 441;
    private int n2 = n1;
    private int WLen = 448;
    private double[] w1;
    private double[] w2;

    public Robot(){
        effectType = AudioEffect.ROBOTIC;

        w1 = new double[WLen];
        w2 = new double[WLen];
        for(int i=0; i<WLen; i++){
            w1[i] = .5*(1 - Math.cos(2.0*Math.PI*i/(WLen)));
            w2[i] = .5*(1 - Math.cos(2.0*Math.PI*i/(WLen)));
        }
    }

    @Override
    public void runEffect(double[] fft) {
        for(int i=0; i<WLen; i++){
            fft[i] *= w1[i]*5;
        }
    }
}