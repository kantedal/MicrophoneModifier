package applications.the4casters.microphonemodifier;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

/**
 * A placeholder RealtimeGraphFragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AudioPlayback.AudioUpdateListener {

    private final Handler mHandler = new Handler();
    private Runnable mTimer;
    private LineGraphSeries<DataPoint> mSeries;
    private double graph2LastXValue = 5d;

    private AudioPlayback audioPlayback;
    private FrameLayout playbackButton;
    private TextView playbackTextView;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        audioPlayback = new AudioPlayback();

        playbackButton = (FrameLayout) mainView.findViewById(R.id.fragment_main_playback_button);
        playbackTextView = (TextView) mainView.findViewById(R.id.fragment_main_playback_text);

        playbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioPlayback.isRecording()) {
                    playbackTextView.setText("Spela upp");
                    audioPlayback.cancelRecord();
                } else {
                    playbackTextView.setText("Sluta spela upp");
                    audioPlayback.record();
                }

            }
        });

        GraphView graph = (GraphView) mainView.findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(200);
        mSeries = new LineGraphSeries<>();
        graph.addSeries(mSeries);


        return mainView;
    }

    private void newDataPoint(int y){
        graph2LastXValue += 1d;
        mSeries.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 640);
    }

    private static final int GRAPH_DIVISION = 1;
    @Override
    public void onResume() {
        super.onResume();

        mTimer = new Runnable() {
            @Override
            public void run() {
                if(audioPlayback.isRecording()){
                    if(audioPlayback.graphBuffer != null) {

                        for (int i = 0; i < audioPlayback.graphBuffer.length; i+=GRAPH_DIVISION) {
                            graph2LastXValue += 1d;
                            mSeries.appendData(new DataPoint(graph2LastXValue, audioPlayback.graphBuffer[i]), true, audioPlayback.graphBuffer.length/GRAPH_DIVISION);
                        }
                    }
                }
                mHandler.postDelayed(this, 50);
            }
        };
        mHandler.postDelayed(mTimer, 1000);
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }

    @Override
    public void onAudioEvent(byte[] buffer) {
        newDataPoint(1);
    }
}
