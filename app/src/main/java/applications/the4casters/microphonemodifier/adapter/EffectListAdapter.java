package applications.the4casters.microphonemodifier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import applications.the4casters.microphonemodifier.AudioPlayback;
import applications.the4casters.microphonemodifier.MainActivity;
import applications.the4casters.microphonemodifier.R;
import applications.the4casters.microphonemodifier.dialog.BandpassDialog;
import applications.the4casters.microphonemodifier.dialog.EchoDialog;
import applications.the4casters.microphonemodifier.effects.AudioEffect;
import applications.the4casters.microphonemodifier.effects.Bandpass;
import applications.the4casters.microphonemodifier.effects.Echo;
import applications.the4casters.microphonemodifier.view.MaterialFont;
import lecho.lib.hellocharts.model.Line;

public class EffectListAdapter
        extends RecyclerView.Adapter<EffectListAdapter.EffectViewHolder>
        implements DraggableItemAdapter<EffectListAdapter.EffectViewHolder> {

    private Context mContext;
    private AudioPlayback mAudioPlayback;

    public static class EffectViewHolder extends AbstractDraggableItemViewHolder {
        public LinearLayout mContainer;
        public TextView mTitleTextView;
        public TextView mSettingFirstTextView;
        public TextView mSettingSecondTextView;
        public MaterialFont mHandle;

        public EffectViewHolder(View v) {
            super(v);
            mContainer = (LinearLayout) v.findViewById(R.id.viewholder_audioeffect_container);
            mTitleTextView = (TextView) v.findViewById(R.id.viewholder_audioeffect_title);
            mSettingFirstTextView = (TextView) v.findViewById(R.id.viewholder_audioeffect_setting_first);
            mSettingSecondTextView = (TextView) v.findViewById(R.id.viewholder_audioeffect_setting_second);
            mHandle = (MaterialFont) v.findViewById(R.id.viewholder_audioeffect_handle);
        }
    }

    public EffectListAdapter(AudioPlayback audioPlayback, Context context) {
        this.mAudioPlayback = audioPlayback;
        this.mContext = context;

        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mAudioPlayback.getAudioEffect(position).getEffectType();
    }

    @Override
    public int getItemViewType(int position) {
        return mAudioPlayback.getAudioEffect(position).getEffectType();
    }

    @Override
    public EffectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.viewholder_audioeffect, parent, false);
        return new EffectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EffectViewHolder holder, int position) {
        final AudioEffect audioEffect = mAudioPlayback.getAudioEffect(position);
        switch (audioEffect.getEffectType()){
            case AudioEffect.ECHO:
                holder.mTitleTextView.setText("Echo");
                holder.mSettingFirstTextView.setText("Delay: " + ((Echo)audioEffect).getDelay());
                holder.mSettingSecondTextView.setText("Echo strength: " + ((Echo)audioEffect).getStrength());
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EchoDialog echoDialog = EchoDialog.newInstance(
                                (int) ((Echo) audioEffect).getDelay(),
                                ((Echo) audioEffect).getStrength());
                        echoDialog.setBandpassListener(new EchoDialog.EchoDialogListener() {
                            @Override
                            public void OnEchoDialogConfirm(double strength, double delay) {
                                ((Echo) audioEffect).setDelay((int) delay);
                                ((Echo) audioEffect).setStrength(strength);
                                notifyDataSetChanged();
                            }
                        });
                        echoDialog.show(((MainActivity) mContext).getSupportFragmentManager(), "echoDialog");
                    }
                });
                break;
            case AudioEffect.BANDPASS:
                holder.mTitleTextView.setText("Bandpass");
                holder.mSettingFirstTextView.setText("Low-pass: " + ((Bandpass)audioEffect).getLowPass());
                holder.mSettingSecondTextView.setText("High-pass: " + ((Bandpass)audioEffect).getHighPass());
                holder.mContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BandpassDialog bandpassDialog = BandpassDialog.newInstance(
                                ((Bandpass) audioEffect).getLowPass(),
                                ((Bandpass)audioEffect).getHighPass());
                        bandpassDialog.setBandpassListener(new BandpassDialog.BandpassDialogListener() {
                            @Override
                            public void OnBandpassDialogConfirm(int low_pass, int high_pass) {
                                ((Bandpass) audioEffect).setLowPass(low_pass);
                                ((Bandpass) audioEffect).setHighPass(high_pass);
                                notifyDataSetChanged();
                            }
                        });
                        bandpassDialog.show(((MainActivity) mContext).getSupportFragmentManager(), "bandpassDialog");
                    }
                });

                break;
            case AudioEffect.ROBOTIC:
                holder.mTitleTextView.setText("Robotic");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mAudioPlayback.getEffectCount();
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(EffectListAdapter.EffectViewHolder myViewHolder, int i) {
        return null;
    }


    @Override
    public void onMoveItem(int fromPosition, int toPosition) {

        if (fromPosition == toPosition) {
            return;
        }

        mAudioPlayback.moveAudioEffect(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(EffectViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
//        final View containerView = holder.mContainer;
//        final View dragHandleView = holder.mDragHandle;
//
//        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
//        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        //return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
        return true;
    }
}