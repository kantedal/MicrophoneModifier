package applications.the4casters.microphonemodifier.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import applications.the4casters.microphonemodifier.R;
import applications.the4casters.microphonemodifier.view.ExtendedEditText;

public class EchoDialog extends DialogFragment  {
    private Button mConfirmButton;
    private ExtendedEditText mStrengthEditText;
    private ExtendedEditText mDelayEditText;

    private EchoDialogListener mCallback;
    public interface EchoDialogListener {
        void OnEchoDialogConfirm(double strength, double delay);
    }

    public void setBandpassListener(EchoDialogListener callback){
        mCallback = callback;
    }

    public static EchoDialog newInstance(double delay, double strength){
        EchoDialog echoDialog = new EchoDialog();

        Bundle args = new Bundle();
        args.putDouble("strength", strength);
        args.putDouble("delay", delay);
        echoDialog.setArguments(args);

        return echoDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_echo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mStrengthEditText = (ExtendedEditText) dialog.findViewById(R.id.dialog_echo_strength_edittext);
        mDelayEditText = (ExtendedEditText) dialog.findViewById(R.id.dialog_echo_delay_edittext);

        mDelayEditText.setSuffix("s");
        mStrengthEditText.setText(getArguments().getDouble("strength") + "");
        mDelayEditText.setText(getArguments().getDouble("delay") + "");

        mStrengthEditText.addTextChangedListener(new TextWatcher() {
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
                mCallback.OnEchoDialogConfirm(Double.parseDouble(mStrengthEditText.getText().toString()), Double.parseDouble(mDelayEditText.getText().toString()));
                dismiss();
            }
        });

        return dialog;
    }
}