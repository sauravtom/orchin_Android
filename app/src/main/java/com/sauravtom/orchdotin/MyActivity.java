package com.sauravtom.orchdotin;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MyActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
        boolean doubleBackToExitPressedOnce;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    String baseUrl = "http://orch.in";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (!isNetworkAvailable()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setMessage("Cannot find the internet !!");

            alert.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    finish();
                    startActivity(getIntent());
                }
            });


            alert.show();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction()
        //        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
        //        .commit();
        WebViewFragment rFragment = new WebViewFragment();

        String sub = "";
        if(position ==0) sub = "/";
        if(position ==1) sub = "/videos";
        if(position ==2) sub = "/about";


        Bundle data = new Bundle();
        data.putInt("position", position);
        data.putString("url", baseUrl + sub);
        //Toast.makeText(getBaseContext(),position+ baseUrl + sub, Toast.LENGTH_LONG).show();
        rFragment.setArguments(data);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, rFragment);
        ft.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.my, menu);
            restoreActionBar();
            return true;
        }

        //Toast.makeText(getBaseContext(),WebViewFragment.webView.getTitle(), Toast.LENGTH_SHORT).show();


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_download) {
            String url = WebViewFragment.webView.getUrl();
            String[] bits = url.split("#");
            String image_url = bits[bits.length-1];
            if (WebViewFragment.typeUrl(image_url) == "image"){
                downloadFile(url);
            }else{
                Toast.makeText(getBaseContext(), "No media found in url.", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        else if(id == R.id.action_reload){
            WebViewFragment.webView.reload();
            return true;
        }
        else if(id == R.id.action_openBrowser){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WebViewFragment.webView.getUrl()));
            startActivity(browserIntent);
            return true;
        }
        else if(id == R.id.action_share){
            String current_url = WebViewFragment.webView.getUrl();
            //MenuItem icon_share = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            Intent myIntent = new Intent();
            myIntent.setAction(Intent.ACTION_SEND);
            myIntent.putExtra(Intent.EXTRA_TEXT, current_url + " via @orch_in");
            myIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MyActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }


        /*Class that prevents closing tha app on back button press, instead go to the previous page. Double press closes the app.*/
        @Override
        public void onBackPressed() {

            if(doubleBackToExitPressedOnce){
                finish();
            }
            //getActionBar().show();
            //getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C7DEF4")));
            //getActionBar().setIcon(R.drawable.ic_launcher);

            String url = WebViewFragment.webView.getUrl();

            if (url.startsWith(baseUrl)) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            }

            if(url.startsWith(baseUrl+"/o/")){
                //getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                Toast.makeText(this, "This is a post page", Toast.LENGTH_SHORT).show();
            }

            invalidateOptionsMenu();
            if(WebViewFragment.webView.canGoBack()){
                WebViewFragment.webView.goBack();
            }else{
                InitFragment();
            }

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }

        public void InitFragment(){

            WebViewFragment rFragment = new WebViewFragment();
            Bundle data = new Bundle();
            data.putInt("position", 1);
            data.putString("url", "http://orch.in");
            rFragment.setArguments(data);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, rFragment);
            ft.commit();
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        public void downloadFile(String uRl) {
            String dirName = "Orchin";
            String[] bits = uRl.split("/");
            String fileName = bits[bits.length-1];

            File direct = new File(Environment.getExternalStorageDirectory() + "/"+dirName);

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle(fileName)
                    .setDescription("Image downloaded via Orchin App")
                    .setDestinationInExternalPublicDir("/"+dirName, fileName);

            Toast.makeText(this, "Downloading Image to Orchin folder.", Toast.LENGTH_LONG).show();
            mgr.enqueue(request);

        }


    }
