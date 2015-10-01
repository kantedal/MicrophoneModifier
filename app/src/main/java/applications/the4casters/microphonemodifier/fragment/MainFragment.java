package applications.the4casters.microphonemodifier.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import applications.the4casters.microphonemodifier.AudioPlayback;
import applications.the4casters.microphonemodifier.MainActivity;
import applications.the4casters.microphonemodifier.R;
import applications.the4casters.microphonemodifier.adapter.EffectListAdapter;
import applications.the4casters.microphonemodifier.effects.AudioEffect;
import applications.the4casters.microphonemodifier.effects.Bandpass;
import applications.the4casters.microphonemodifier.effects.Echo;
import applications.the4casters.microphonemodifier.effects.Robotic;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A placeholder RealtimeGraphFragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    private final Handler mHandler = new Handler();
    private Runnable mTimer;

    private LinearLayout addEffectButton;
    private AudioPlayback audioPlayback;
    private FrameLayout playbackButton;
    private TextView playbackTextView;

    private LineChartView audioChart;
    private LineChartData audioLineData;
    private List<PointValue> audioValues;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        audioPlayback = new AudioPlayback();

        addEffectButton = (LinearLayout) mainView.findViewById(R.id.fragment_main_add_effect);
        addEffectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                //builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Select an audio effect below");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Band-pass filter");
                arrayAdapter.add("Echo effect");
                arrayAdapter.add("Robotic effect");

                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case AudioEffect.BANDPASS:
                                        audioPlayback.addAudioEffect(new Bandpass());
                                        break;
                                    case AudioEffect.ECHO:
                                        audioPlayback.addAudioEffect(new Echo());
                                        break;
                                    case AudioEffect.ROBOTIC:
                                        audioPlayback.addAudioEffect(new Robotic());
                                        break;
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                builderSingle.show();
            }
        });

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

        audioChart = (LineChartView) mainView.findViewById(R.id.chart);

        final Viewport v = new Viewport(audioChart.getMaximumViewport());
        v.bottom = -10.0f;
        v.top = 200.0f;
        v.right = 640.0f;
        v.left  = 0.0f;
        audioChart.setMaximumViewport(v);
        audioChart.setCurrentViewport(v);
        audioChart.setViewportCalculationEnabled(false);

        audioValues = new ArrayList<>();
        audioLineData = new LineChartData();

        for(int i=0; i<GRAPH_VALUE_COUNT; i++)
            audioValues.add(new PointValue(i,0));

        Line line = new Line(audioValues).setColor(Color.BLACK).setCubic(true).setStrokeWidth(1).setHasPoints(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        audioLineData = new LineChartData();
        audioLineData.setLines(lines);
        audioChart.setLineChartData(audioLineData);

        return mainView;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();


        final EffectListAdapter myItemAdapter = new EffectListAdapter(audioPlayback, getActivity());
        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    private static final int GRAPH_VALUE_COUNT = 50;
    @Override
    public void onResume() {
        super.onResume();

        mTimer = new Runnable() {
            @Override
            public void run() {
                if(audioPlayback.isRecording()){
                    if(audioPlayback.graphBuffer != null) {
                        int step = Math.round(audioPlayback.graphBuffer.length/GRAPH_VALUE_COUNT);

                        audioValues.clear();
                        int count = 0;
                        for (int i = 0; i < audioPlayback.graphBuffer.length; i+=step) {
                            float value = 0;
                            int sum_count = 0;
                            for(int x=i-step/3; x<i+step/3; x++){
                                if(x >= 0 && x < audioPlayback.graphBuffer.length){
                                    value += (float) audioPlayback.graphBuffer[x];
                                    sum_count++;
                                }
                            }
                            value /= sum_count;
                            //Log.d("Log", audioChart.getLineChartData().getLines().get(0).getValues().get(i) + " x");
                            //if(audioChart.getLineChartData().getLines().get(0).getValues().get(0) != null)
                            //    audioChart.getLineChartData().getLines().get(0).getValues().get(0).set(i, value);
                            count++;
                            audioValues.add(new PointValue(i, value));
                        }

                        Line line = new Line(audioValues).setColor(Color.WHITE).setCubic(true).setStrokeWidth(1).setHasPoints(false);
                        List<Line> lines = new ArrayList<>();
                        lines.add(line);

                        audioLineData = new LineChartData();
                        audioLineData.setLines(lines);
                        audioChart.setLineChartData(audioLineData);
                    }
                }
                mHandler.postDelayed(this, 100);
            }
        };
        mHandler.postDelayed(mTimer, 1000);
    }

}
