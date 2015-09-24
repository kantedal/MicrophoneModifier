package applications.the4casters.microphonemodifier;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * Created by filles-dator on 2015-09-09.
 */
public class AudioPlayback {

    public double[] graphBuffer;

    public interface AudioUpdateListener{
        void onAudioEvent(byte[] buffer);
    }

    private AudioUpdateListener mCallback;
    public void setCallback(AudioUpdateListener audioUpdateListener){
        mCallback = audioUpdateListener;
    }

    private final static int SAMPLING_RATE = 8000;

    private boolean isRecording;
    private AudioTrack atrack;
    private AudioRecord arec;
    private Thread recordThread;

    public AudioPlayback(){
        isRecording = false;
        run();
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

                        for(int i=0; i<fft.length; i++) {
                            graphBuffer[i] = fft[i];
                        }

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
