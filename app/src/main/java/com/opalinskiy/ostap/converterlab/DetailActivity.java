package com.opalinskiy.ostap.converterlab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import com.getbase.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.opalinskiy.ostap.converterlab.abstractActivities.AbstractActionActivity;
import com.opalinskiy.ostap.converterlab.api.Api;
import com.opalinskiy.ostap.converterlab.constants.Constants;
import com.opalinskiy.ostap.converterlab.customView.CurrencyListElementView;
import com.opalinskiy.ostap.converterlab.customView.ShareImageBody;
import com.opalinskiy.ostap.converterlab.customView.ShareImageTitle;
import com.opalinskiy.ostap.converterlab.interfaces.ConnectCallback;
import com.opalinskiy.ostap.converterlab.model.Currency;
import com.opalinskiy.ostap.converterlab.model.DataResponse;
import com.opalinskiy.ostap.converterlab.model.Organisation;
import com.opalinskiy.ostap.converterlab.services.LoaderService;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

public class DetailActivity extends AbstractActionActivity implements SwipeRefreshLayout.OnRefreshListener {

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
    private SwipeRefreshLayout swipeRefreshLayout;


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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_DA);
        swipeRefreshLayout.setOnRefreshListener(this);
        snackbar = Snackbar
                .make(swipeRefreshLayout, "", Snackbar.LENGTH_INDEFINITE);


        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        layout.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        ShareImageTitle title = new ShareImageTitle(this, organisation);
//        layout.addView(title);


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
        layout.removeAllViews();
        ShareImageTitle title = new ShareImageTitle(this, organisation);
        layout.addView(title);
        List<Currency> list = organisation.getCurrencies().getCurrencyList();

        for (int i = 0; i < list.size(); i++) {
            ShareImageBody currencyItem = new ShareImageBody(this, list.get(i));
            layout.addView(currencyItem);
        }
        Bitmap bitmap = getBitmapFromView(layout);
        String filePath = saveImage(bitmap);
        dialog = ShareFragment.newInstance(bitmap, filePath);
        dialog.show(DetailActivity.this.getFragmentManager(), Constants.DIALOG_FRAGMENT_TAG);

        //icon = getBitmapFromView(layout);
        Log.d("TAG", "on menu item seected");
//        dialog = ShareFragment.newInstance(icon);

        return super.onOptionsItemSelected(item);
    }

//    public  Bitmap getBitmapFromView(View view) {
//        Display display = getWindowManager().getDefaultDisplay();
//        int width = display.getWidth();
//        int height = display.getHeight();
//        //Define a bitmap with the same size as the view
//
//        view.layout(0, 0, width, layout.getMeasuredHeight());
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
//                Bitmap.Config.ARGB_8888);
//   //     Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
//        //Bind a canvas to it
//        Canvas canvas = new Canvas(returnedBitmap);
//        //Get the view's background
//        Drawable bgDrawable =view.getBackground();
//        if (bgDrawable!=null)
//            //has background drawable, then draw it on the canvas
//            bgDrawable.draw(canvas);
//        else
//            //does not have background drawable, then draw white background on the canvas
//            canvas.drawColor(Color.WHITE);
//        // draw the view on the canvas
//        view.draw(canvas);
//        //return the bitmap
//        return returnedBitmap;
//    }

    public Bitmap getBitmapFromView(View view) {

        // layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.layout(0, 0, view.getMeasuredWidth(), layout.getMeasuredHeight());
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

    private String saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        String rootPath = root + "/saved_images";
        File myDir = new File(rootPath);
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + organisation.getTitle() + n + ".jpg";
        String fullPath = rootPath + fname;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fullPath;
    }

    @Override
    public void onRefresh() {
        loadDataFromServer();
    }

    private void loadDataFromServer() {
        startLoaderService();
        Api.getDataResponse(new ConnectCallback() {

            @Override
            public void onProgress(long percentage) {
                String message = "Progress:" + percentage + "%";
                // updateNotification(message);
                snackbar.setText(message);
                snackbar.show();
            }

            @Override
            public void onSuccess(Object object) {
                Log.d(Constants.LOG_TAG, "On success");
                DataResponse dataResponse = (DataResponse) object;
                List listOrganisations = dataResponse.getOrganisations();
                organisation = getOrganisationById(listOrganisations, organisation.getId());
                setText();
                fillExchangeRatesList(organisation);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Toast.makeText(DetailActivity.this, "Cannot load data", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public Organisation getOrganisationById(List<Organisation> list, String id) {
        Organisation result = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                result = list.get(i);
                break;
            }
        }
        return result;
    }

}
