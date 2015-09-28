package applications.the4casters.microphonemodifier.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.internal.widget.DrawableUtils;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import applications.the4casters.microphonemodifier.AudioPlayback;
import applications.the4casters.microphonemodifier.R;
import applications.the4casters.microphonemodifier.effects.AudioEffect;

public class EffectListAdapter
        extends RecyclerView.Adapter<EffectListAdapter.EffectViewHolder>
        implements DraggableItemAdapter<EffectListAdapter.EffectViewHolder> {

    private AudioPlayback mAudioPlayback;

    public static class EffectViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;

        public EffectViewHolder(View v) {
            super(v);
            //mContainer = (FrameLayout) v.findViewById(R.id.container);
            //mDragHandle = v.findViewById(R.id.drag_handle);
            //mTextView = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public EffectListAdapter(AudioPlayback audioPlayback) {
        this.mAudioPlayback = audioPlayback;

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
        final View v = inflater.inflate(R.layout.viewholder_bandpass, parent, false);
        return new EffectViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EffectViewHolder holder, int position) {
        final AudioEffect audioEffect = mAudioPlayback.getAudioEffect(position);

        final int dragState = holder.getDragStateFlags();

//        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
//            int bgResId;
//
//            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
//                //bgResId = R.drawable.bg_item_dragging_active_state;
//
//                //DrawableUtils.clearState(holder.mContainer.getForeground());
//            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
//                //bgResId = R.drawable.bg_item_dragging_state;
//            } else {
//               // bgResId = R.drawable.bg_item_normal_state;
//            }
//
//            //holder.mContainer.setBackgroundResource(bgResId);
//        }
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