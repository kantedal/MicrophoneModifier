package applications.the4casters.microphonemodifier;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;

import applications.the4casters.microphonemodifier.effects.AudioEffect;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by filles-dator on 2015-09-09.
 */
public class AudioPlayback {

    private ArrayList<AudioEffect> audioEffects;
    public double[] graphBuffer;

    private final static int SAMPLING_RATE = 11025;

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

                while(true) {
                    if(isRecording) {
                        arec.read(buffer, 0, buffersize);
                        double[] fft = new double[buffer.length];
                        final int bytesPerSample = 2; // As it is 16bit PCM
                        final double amplification = 10.0; // choose a number as you like
                        for (int index = 0, floatIndex = 0; index < buffer.length-1; index += bytesPerSample, floatIndex++) {
                            double sample = 0;
                            for (int b = 0; b < bytesPerSample; b++) {
                                int v = buffer[index + b];
                                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                                    v &= 0xFF;
                                }
                                sample += v << (b * 8);
                            }
                            Double sample32 = amplification * (sample / 32768.0);
                            fft[floatIndex] = sample32;
                        }

                        DoubleFFT_1D fft1d = new DoubleFFT_1D(buffer.length);
                        fft1d.realForward(fft);

                        double[] fftInverse = new double[graphBuffer.length];
                        for(int i=0; i<fft.length; i++) {
                            graphBuffer[i] = Math.abs(fft[i]);
                            //fftInverse[graphBuffer.length-i-1] = fft[i];
                        }
                        //fft = fftInverse;

                        fft1d.realInverse(fft, false);

                        byte[] output = new byte[fft.length];
                        for(int i=0; i<fft.length/2; i++){
                            short s1 = (short) fft[i];
                            output[i * 2] += (byte) (s1 & 0xff);
                            output[i * 2 + 1] += (byte) ((s1 >> 8) & 0xff);
                        }

                        atrack.write(output, 0, output.length);
                    }
                }
            }
        });

        recordThread.start();
    }

}
