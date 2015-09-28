package applications.the4casters.microphonemodifier.effects;

import android.util.Log;

/**
 * Created by Albin on 28/09/2015.
 */
public class Robotic extends AudioEffect {

    public Robotic() {
        effectType = AudioEffect.ROBOTIC;
    }

    @Override
    public void runEffect(double[] fft) {
        double[] fft2 = new double[fft.length];

        for(int i = 0, k = 0; i < fft.length; i+=2)
        {
            if(i < fft.length/2) {
                fft2[fft.length / 2 + k - 1] = (fft[fft.length / 2 + i] + fft[fft.length / 2 + i]) / 2;
                fft2[fft.length / 2 - k - 1] = (fft[fft.length / 2 - i] + fft[fft.length / 2 - i]) / 2;
            }
            //fft2[k] = 0;
            //fft2[fft2.length - k -1] = 0;
            k++;
        }
        for(int i = 0; i < fft.length; i++)
        {
            fft[i] = fft2[i];
        }
    }
}