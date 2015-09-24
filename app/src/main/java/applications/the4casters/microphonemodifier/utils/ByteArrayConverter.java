package applications.the4casters.microphonemodifier.utils;

import java.nio.ByteBuffer;

/**
 * Created by filles-dator on 2015-09-16.
 */
public class ByteArrayConverter {

    public static double[] floatMe(short[] pcms) {
        double[] doubleBuffer = new double[pcms.length];
        for (int i = 0; i < pcms.length; i++) {
            doubleBuffer[i] = pcms[i];
        }
        return doubleBuffer;
    }

    public static short[] shortMe(byte[] bytes) {
        short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getShort();
        }
        return out;
    }
}
