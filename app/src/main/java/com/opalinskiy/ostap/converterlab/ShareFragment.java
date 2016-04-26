package com.opalinskiy.ostap.converterlab;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.opalinskiy.ostap.converterlab.constants.Constants;

import java.io.Serializable;


public class ShareFragment extends DialogFragment {
   public ImageView imageView;
    Bitmap bitmap;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("TAG", "on create dialog" );
        Dialog dialog = new Dialog(getActivity());
        bitmap = (Bitmap) getArguments().get(Constants.BITMAP_KEY);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.share_fragment, null);
        imageView = (ImageView) layout.findViewById(R.id.iv_bitmap_SF);
        imageView.setImageBitmap(bitmap);
        dialog.setContentView(layout);
        return dialog;
    }

    //
    public static ShareFragment newInstance(Bitmap bitmapArg) {
        ShareFragment dialog = new ShareFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.BITMAP_KEY, (Parcelable) bitmapArg);
        dialog.setArguments(args);
        Log.d("TAG", "dialog is null" + (dialog == null) );
        Log.d("TAG", "image is null" + (dialog.imageView == null));
        return dialog;
    }
}
