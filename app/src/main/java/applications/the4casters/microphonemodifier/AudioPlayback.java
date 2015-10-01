package applications.the4casters.microphonemodifier;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import java.util.ArrayList;

import applications.the4casters.microphonemodifier.effects.AudioEffect;
import applications.the4casters.microphonemodifier.effects.ChangePitch;
import applications.the4casters.microphonemodifier.effects.Robotic;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by filles-dator on 2015-09-09.
 */
public class AudioPlayback {

    private ArrayList<AudioEffect> audioEffects;
    public double[] graphBuffer;

    private final static int SAMPLING_RATE = 11025;
    private final static int LOWEST_FREQ = 300;
    private final static int HIGHEST_FREQ = 4000;
    public final static int FFT_SIZE = 448;

    private boolean isRecording;
    private AudioTrack atrack;
    private AudioRecord arec;
    private Thread recordThread;

    public AudioPlayback(){
        isRecording = false;
        audioEffects = new ArrayList<>();
        run();
    }

    public void addAudioEffect(AudioEffect audioEffect){
        audioEffects.add(audioEffect);
    }

    public void removeAudioEffect(int index){
        audioEffects.remove(index);
    }

    public void moveAudioEffect(int from, int to){
        AudioEffect toMove = audioEffects.get(from);
        audioEffects.remove(from);
        audioEffects.add(to, toMove);
    }

    public AudioEffect getAudioEffect(int index){
        return audioEffects.get(index);
    }

    public int getEffectCount(){
        return audioEffects.size();
    }

    public void record(){
        isRecording = true;
    }

    public void cancelRecord(){
        isRecording = false;
    }

    public boolean isRecording(){
        return isRecording;
    }

    public static int indexToFrequency(int index){
        double step_size = (HIGHEST_FREQ-LOWEST_FREQ)/FFT_SIZE;
        return (int) Math.round(step_size * index + LOWEST_FREQ);
    }

    public static int frequencyToIndex(int frequency){
        double step_size = (HIGHEST_FREQ-LOWEST_FREQ)/FFT_SIZE;
        return (int) Math.round((frequency - LOWEST_FREQ) / step_size);
    }

    private void runEffects(double[] fft, int buffersize){
        DoubleFFT_1D fft1d = new DoubleFFT_1D(buffersize/2);
        fft1d.realForward(fft);

        //Albins test-skit
        ChangePitch hej = new ChangePitch();
        hej.runEffect(fft);
        //Robotic ost = new Robotic();
        //ost.runEffect(fft);

        for(AudioEffect audioEffect : audioEffects)
            audioEffect.runEffect(fft);

        for(int i=0; i<fft.length; i++) {
            graphBuffer[i] = Math.abs(fft[i]);
        }

        fft1d.realInverse(fft, false);
    }

    private void run() {
        recordThread = new Thread(new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int buffersize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);

                arec = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
                atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLING_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
                atrack.setPlaybackRate(SAMPLING_RATE);

                byte[] buffer = new byte[buffersize];
                arec.startRecording();
                atrack.play();

                graphBuffer = new double[buffer.length];

                final int bytesPerSample = 2; // As it is 16bit PCM
                final double amplification = 10.0; // choose a number as you like

                double[] fft2 = new double[buffer.length/2];
                double[] fft_smoothing1 = new double[buffer.length/2];
                double[] fft_smoothing2 = new double[buffer.length/2];


                while(true){
                    if(isRecording) {
                        arec.read(buffer, 0, buffersize);

                        double[] fft1 = new double[buffer.length/2];

                        for (int index = 0, floatIndex = 0; index < buffer.length; index += bytesPerSample, floatIndex++) {
                            double sample = 0;
                            for (int b = 0; b < bytesPerSample; b++) {
                                int v = buffer[index + b];
                                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                                    v &= 0xFF;
                                }
                                sample += v << (b * 8);
                            }
                            Double sample32 = amplification * (sample / 32768.0);

                            fft1[floatIndex] = sample32;

                            if(floatIndex == buffer.length/4){
                                runEffects(fft_smoothing1, buffersize);

                                double[] blendedOutput = new double[fft1.length];
                                for(int i=0; i<blendedOutput.length; i++){
                                    if(i < blendedOutput.length/2){
                                        blendedOutput[i] = Math.abs(Math.cos(Math.PI*(i/(blendedOutput.length-1.0)))) * fft_smoothing2[i+blendedOutput.length/2] * 1.7 + Math.abs(Math.sin(Math.PI * (i / (blendedOutput.length-1.0)))) * fft2[i] * 1.7;
                                    }else{
                                        blendedOutput[i] = Math.abs(Math.cos(Math.PI*(i/(blendedOutput.length-1.0)))) * fft_smoothing1[i-blendedOutput.length/2] * 1.7 + Math.abs(Math.sin(Math.PI * (i / (blendedOutput.length-1.0)))) * fft2[i] * 1.7;
                                    }
                                }

                                byte[] output = new byte[blendedOutput.length*2];
                                for(int i=0; i<blendedOutput.length; i++){
                                    short s1 = (short) Math.round(blendedOutput[i]);
                                    output[i * 2] += (byte) (s1 & 0xff);
                                    output[i * 2 + 1] += (byte) ((s1 >> 8) & 0xff);
                                }
                                atrack.write(output, 0, output.length);

                                fft_smoothing2 = new double[buffer.length/2];
                                for(int i=0; i<fft_smoothing2.length; i++)
                                    fft_smoothing2[i] = fft_smoothing1[i];

                                fft_smoothing1 = new double[buffer.length/2];
                            }

                            if(floatIndex < buffersize/4){
                                fft_smoothing1[floatIndex+buffersize/4] = sample32;
                            }else if(floatIndex >= buffersize/4){
                                fft_smoothing1[floatIndex-buffersize/4] = sample32;
                            }
                        }
                        runEffects(fft1, buffersize);

                        fft2 = new double[buffer.length/2];
                        for(int i=0; i<fft2.length; i++)
                            fft2[i] = fft1[i];
                    }
                }
            }
        });

        recordThread.start();
    }

}
