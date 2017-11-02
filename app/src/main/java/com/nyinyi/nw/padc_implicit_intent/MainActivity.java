package com.nyinyi.nw.padc_implicit_intent;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.share_compat) Button share_compat;
    @BindView(R.id.nav_map) Button nav_map;
    @BindView(R.id.ph_call) Button ph_call;
    @BindView(R.id.send_email) Button send_email;
    @BindView(R.id.take_pic) Button take_pic;
    @BindView(R.id.select_pic) Button select_pic;
    @BindView(R.id.event) Button event;
    @BindView(R.id.img_view) ImageView img_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this);

        setSupportActionBar(toolbar);

    }
    @OnClick(R.id.share_compat)
    public void shareCompat() {
        shareText("Nyi Nyi Zaw");
    }
    @OnClick(R.id.nav_map)
    public void navagationMap() {
        Uri location = Uri.parse("geo:37.422219,-122.08364?z=14");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }
    @OnClick(R.id.ph_call)
    public void makePhoneCall() {
        Uri phonecall = Uri.parse("tel:09425028868");
        Intent makecallIntent = new Intent(Intent.ACTION_DIAL, phonecall);
        startActivity(makecallIntent);
    }
    @OnClick(R.id.send_email)
    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String mime = "text/plain";
        emailIntent.setType(mime);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"aungpyaephyo@padcmyanmar.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "PADC 3");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I am Nyi Nyi Zaw");
        startActivity(emailIntent);
    }
    @OnClick(R.id.event)
    public void eventCreate()
    {
        long time= System.currentTimeMillis();
        addEvent("PADC","Yangon",time,time);
    }
    @OnClick(R.id.take_pic)
    public void takeCamera()
    {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @OnClick(R.id.select_pic)
    public void selectPicture()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_CAPTURE);
    }
    public void addEvent(String title, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void shareText(String text) {
        String mimeType = "text/plain";
        String title = "PADC 3";

        Intent shareIntent =   ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(text)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null){
            startActivity(shareIntent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = { MediaStore.Images.Media.DATA };
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();

            Toast.makeText(this, " "+filePath, Toast.LENGTH_SHORT).show();
            Uri uriFromPath = Uri.fromFile(new File(filePath));
            img_view.setImageURI(uriFromPath);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
