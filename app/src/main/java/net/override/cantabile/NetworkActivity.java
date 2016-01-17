package net.override.cantabile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<HashMap<String, String>> networklist = new ArrayList<HashMap<String, String>>();
    ListView lv;
    TextView nick, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        lv = (ListView)findViewById(R.id.mylist);
        SimpleAdapter adapter = new SimpleAdapter(this, networklist, R.layout.network_content_item,new String [] {"name","duration"}, new int[] {R.id.songname,R.id.songduration});

        makelist("Addicted To A Memory", "05:03");
        makelist("I Want You To Know", "04:00");
        makelist("Beautiful Now", "03:38");
        makelist("Transmission", "04:02");
        makelist("Done With Love", "04:56");
        makelist("True Colors", "03:48");
        makelist("Straight Into The Fire", "03:42");
        makelist("Papercut", "07:23");
        makelist("Bumble Bee", "04:08");
        makelist("Daisy", "02:55");
        makelist("Illusion", "06:26");

        lv.setAdapter(adapter);
        lv.setDividerHeight(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            Toast.makeText(getBaseContext(), R.string.exit, Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();

    }

    public void makelist(String N, String D){
        HashMap<String, String> tmp = new HashMap<String, String>();
        tmp = new HashMap();
        tmp.put("name", N);
        tmp.put("duration", D);
        networklist.add(tmp);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mine) {
            Intent intent = new Intent(NetworkActivity.this, MySongActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.info) {
            Intent intent = new Intent(NetworkActivity.this, InfoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.piano) {
            Intent intent = new Intent(NetworkActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
