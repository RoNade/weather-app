package com.nadero.weather.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.nadero.weather.R;

public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_ok_button, null);

        return builder.create();
    }
}
