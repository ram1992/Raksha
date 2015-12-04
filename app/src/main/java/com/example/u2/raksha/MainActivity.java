package com.example.u2.raksha;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ParseUser user;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private CardView mCardview;
    TextView mTextview;
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = ParseUser.getCurrentUser();
        status = (String) user.get(getString(R.string.status));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mDrawerList = (ListView) findViewById(R.id.navList);
        mCardview = (CardView) findViewById(R.id.nav_card_view);
        addDrawerItems();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == 1)
                displyDialog(position);
            }
        });
        setupDrawer();
        mTextview = (TextView) findViewById(R.id.nav_text_view);
        mTextview.setText(user.get("fullName").toString() + "\n" + user.getUsername());

    }

    //...........................................................................................
    private void displyDialog(final int position) {

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.prompt_indoor, null);

        android.support.v7.app.AlertDialog.Builder alertDialogBuilderMinPts = new android.support.v7.app.AlertDialog.Builder(
                MainActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilderMinPts.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editText_dialog_info);
        final TextView textView = (TextView) promptsView.findViewById(R.id.textview_dialog);
        if(position == 0){
            textView.setText("Please Enter Minimum Duration For Secured Areas");
        }else{
            textView.setText("Please Enter Minimum Distance For Secured Areas");
        }

        // set dialog message
        alertDialogBuilderMinPts
                .setCancelable(true)
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if (id == KeyEvent.KEYCODE_BACK) {
                                    dialog.dismiss();
                                }
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Child");
                                if(status.equalsIgnoreCase("child")){
                                    query.whereEqualTo("emailId", user.getUsername());
                                }
                                else{
                                    query.whereEqualTo("parentEmailId", user.getUsername());
                                }
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> list, ParseException e) {
                                        if (e == null) {
                                            ParseObject result = list.get(0);
                                            if(position == 0){
                                                result.put("minDuration",userInput.getText().toString());
                                            }else{
                                                result.put("minDistance", userInput.getText().toString());
                                            }
                                            result.saveInBackground(new SaveCallback() {
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        dialog.dismiss();
                                                    } else {
                                                        // The save failed.
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d("score", "Error: " + e.getMessage());
                                            Toast.makeText(MainActivity.this, R.string.error_internet_connection_database, Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                });
                            }
                        });
        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilderMinPts.create();

        // show it
        alertDialog.show();
        alertDialog.getWindow().setLayout(1000, 1000);

    }

    //...........................................................................................
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Raksha");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.syncState();
    }
    // ................................................................................................

    private void addDrawerItems() {
        String[] osArray = {"Set Min Points", "Set Min Distance", "Subsribe Indoor Map"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }
    //............................................................................................................

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrackingFragment(), "HOME");
        adapter.addFragment(new FeaturesFragment(), "CALL OR TEXT");
        adapter.addFragment(new IndoorFragment(), "INDOOR");
        viewPager.setAdapter(adapter);
    }
    //............................................................................................................

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //............................................................................................................

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
        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            user.logOut();
                            Intent intent = new Intent(MainActivity.this, DispatchActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //............................................................................................................

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    //............................................................................................................

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}