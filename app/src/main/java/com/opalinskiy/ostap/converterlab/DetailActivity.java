package com.opalinskiy.ostap.converterlab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.opalinskiy.ostap.converterlab.abstractActivities.AbstractActionActivity;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.customView.CurrencyListElementView;
import com.opalinskiy.ostap.converterlab.customView.ShareImageTitle;
import com.opalinskiy.ostap.converterlab.model.Currency;
import com.opalinskiy.ostap.converterlab.model.Organisation;

import java.util.List;

public class DetailActivity extends AbstractActionActivity {

    private TextView tvTitle;
    private TextView tvLink;
    private TextView tvAddress;
    private TextView tvCity;
    private TextView tvRegion;
    private TextView tvPhone;
    private LinearLayout llListElement;
    private Organisation organisation;
    private FloatingActionsMenu floatingMenu;
    private FloatingActionButton buttonMap;
    private FloatingActionButton buttonLink;
    private FloatingActionButton buttonCall;
    private FrameLayout semiTransparentFrame;
    private boolean isMenuOpened;
    private ShareFragment dialog;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();
        setText();
        fillExchangeRatesList(organisation);
    }

    private void init() {
        tvTitle = (TextView) findViewById(R.id.tv_title_AD);
        tvLink = (TextView) findViewById(R.id.tv_link_AD);
        tvAddress = (TextView) findViewById(R.id.tv_address_AD);
        tvCity = (TextView) findViewById(R.id.tv_city_AD);
        tvRegion = (TextView) findViewById(R.id.tv_region_AD);
        tvPhone = (TextView) findViewById(R.id.tv_phone_AD);
        llListElement = (LinearLayout) findViewById(R.id.ll_list_element_AD);
        organisation = (Organisation) getIntent().getSerializableExtra(Constants.ORG_SERIALISE);
        floatingMenu = (FloatingActionsMenu) findViewById(R.id.floating_menu);
        buttonMap = (FloatingActionButton) findViewById(R.id.item_map);
        buttonLink = (FloatingActionButton) findViewById(R.id.item_link);
        buttonCall = (FloatingActionButton) findViewById(R.id.item_call);
        semiTransparentFrame = (FrameLayout) findViewById(R.id.fl_semi_transparent);

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ShareImageTitle title = new ShareImageTitle(this, organisation);
        layout.addView(title);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setMenuListeners();
    }

    private void setText() {
        tvTitle.setText(organisation.getTitle());
        tvLink.setText(organisation.getLink());
        tvCity.setText(organisation.getCity());
        tvAddress.setText(organisation.getAddress());
        tvRegion.setText(organisation.getRegion());
        tvPhone.setText(organisation.getPhone());
    }

    private void fillExchangeRatesList(Organisation organisation) {
        List<Currency> list = organisation.getCurrencies().getCurrencyList();
        for (int i = 0; i < list.size(); i++) {
            CurrencyListElementView elementView = new CurrencyListElementView(this);
            elementView.setViews(list.get(i));
            llListElement.addView(elementView);
        }


    }

    private void setMenuListeners() {
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowMap(organisation);
            }
        });

        buttonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenLink(organisation);
            }
        });

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallNumber(organisation);
            }
        });

        floatingMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                isMenuOpened = true;
                semiTransparentFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                isMenuOpened = false;
                semiTransparentFrame.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



                dialog = ShareFragment.newInstance(getBitmapFromView(layout));
                dialog.show(DetailActivity.this.getFragmentManager(), Constants.DIALOG_FRAGMENT_TAG);

       //icon = getBitmapFromView(layout);
        Log.d("TAG", "on menu item seected");
//        dialog = ShareFragment.newInstance(icon);

        return super.onOptionsItemSelected(item);
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view

//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
//                Bitmap.Config.ARGB_8888);
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
