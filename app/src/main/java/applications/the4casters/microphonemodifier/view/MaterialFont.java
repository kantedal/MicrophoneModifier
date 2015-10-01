package applications.the4casters.microphonemodifier.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by filles-dator on 2015-07-10.
 */
public class MaterialFont extends TextView {
    public MaterialFont(Context context, AttributeSet attrs){
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "icon_font_material.ttf"));
    }
}
