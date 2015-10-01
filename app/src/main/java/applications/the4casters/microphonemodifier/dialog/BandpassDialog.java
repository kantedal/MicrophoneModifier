package applications.the4casters.microphonemodifier.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigInteger;
import java.security.SecureRandom;

import applications.the4casters.microphonemodifier.R;
import applications.the4casters.microphonemodifier.view.ExtendedEditText;

public class BandpassDialog extends DialogFragment  {
    private Button mConfirmButton;
    private ExtendedEditText mLowPassEditText;
    private ExtendedEditText mHighPassEditText;

    private BandpassDialogListener mCallback;
    public interface BandpassDialogListener {
        void OnBandpassDialogConfirm(int low_pass, int high_pass);
    }

    public void setBandpassListener(BandpassDialogListener callback){
        mCallback = callback;
    }

    public static BandpassDialog newInstance(int low, int high){
        BandpassDialog bandpassDialog = new BandpassDialog();

        Bundle args = new Bundle();
        args.putInt("low", low);
        args.putInt("high", high);
        bandpassDialog.setArguments(args);

        return bandpassDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_bandpass);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mLowPassEditText = (ExtendedEditText) dialog.findViewById(R.id.dialog_bandpass_low_edittext);
        mHighPassEditText = (ExtendedEditText) dialog.findViewById(R.id.dialog_bandpass_high_edittext);

        mLowPassEditText.setSuffix("Hz");
        mHighPassEditText.setSuffix("Hz");
        mLowPassEditText.setText(getArguments().getInt("low")+"");
        mHighPassEditText.setText(getArguments().getInt("high")+"");

        mLowPassEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mConfirmButton = (Button) dialog.findViewById(R.id.dialog_bandpass_confirm);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.OnBandpassDialogConfirm(Integer.parseInt(mLowPassEditText.getText().toString()), Integer.parseInt(mHighPassEditText.getText().toString()));
                dismiss();
            }
        });

        return dialog;
    }
}