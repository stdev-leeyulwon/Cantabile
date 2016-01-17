package net.override.cantabile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int KEY_LENGTH = 7;
    public static final int OCTAVE_COUNT = 6;
    public static final String KEYS = "cdefgab";
    public static final String[] PITCHES = new String[]{"c", "cs", "d", "ds", "e", "f", "fs", "g", "gs", "a", "as", "b"}; // Chromatic 스케일 음계

    private ViewGroup viewKeys, keysContainer;
    private LinearLayout imgKey;

    private SoundPool sPool;
    private Map<String, Integer> sMap;
    private AudioManager mAudioManager;

    private int imgKeyWidth, programNo, octaveShift;
    private SharedPreferences pref;

    @SuppressWarnings("unused")
    private int sKey0, sKey1, sKey2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("net.override.cantabile", Activity.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewKeys = (ViewGroup) findViewById(R.id.hscroll);
        viewKeys.setOnTouchListener(keysTouchListener);

        keysContainer = (ViewGroup) findViewById(R.id.keys_container);
        keysContainer.setOnTouchListener(keysContainerTouchListener);

        //imgKey = (LinearLayout) findViewById(R.id.piano_view);
        //imgKeyWidth = pref.getInt("imgKeyWidth", 10000);
        //imgKey.setLayoutParams(new LinearLayout.LayoutParams(imgKeyWidth, ViewGroup.LayoutParams.FILL_PARENT));

        resetSoundPool();
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setTag(R.drawable.ic_fiber_manual_record_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

                Integer integer = (Integer) fab.getTag();
                integer = integer == null ? 0 : integer;
                switch(integer) {
                    case R.drawable.ic_fiber_manual_record_white_24dp:
                        Snackbar.make(view, R.string.rec_start, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        fab.setImageResource(R.drawable.ic_stop_white_24dp);
                        fab.setTag(R.drawable.ic_stop_white_24dp);
                        break;
                    case R.drawable.ic_stop_white_24dp:
                    default:

                        Context mContext = getApplicationContext();
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialogue,(ViewGroup) findViewById(R.id.layout_root));

                        AlertDialog.Builder aDialog = new AlertDialog.Builder(MainActivity.this);
                        aDialog.setTitle(R.string.song_name);
                        aDialog.setView(layout);

                        aDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog ad = aDialog.create();
                        ad.show();

                        Snackbar.make(view, R.string.rec_stop, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        fab.setImageResource(R.drawable.ic_fiber_manual_record_white_24dp);
                        fab.setTag(R.drawable.ic_fiber_manual_record_white_24dp);
                        break;
                }

            }
        });

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mine) {
            Intent intent = new Intent(MainActivity.this, MySongActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.network) {
            Intent intent = new Intent(MainActivity.this, NetworkActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        } else if (id == R.id.info) {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private View.OnTouchListener keysTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            int downPointerIndex = -1;
            int upPointerIndex = -1;

            if (action == MotionEvent.ACTION_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_1_DOWN) downPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_2_DOWN) downPointerIndex = 1;
            else if(action == MotionEvent.ACTION_POINTER_3_DOWN) downPointerIndex = 2;
            else if(action == MotionEvent.ACTION_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_1_UP) upPointerIndex = 0;
            else if(action == MotionEvent.ACTION_POINTER_2_UP) upPointerIndex = 1;
            else if(action == MotionEvent.ACTION_POINTER_3_UP) upPointerIndex = 2;

            if(downPointerIndex>=0){
                int scrollWidth = keysContainer.getRight(); // 스크롤 폭
                int keyWhiteWidth = (int) (((float) scrollWidth) / (KEY_LENGTH * OCTAVE_COUNT)); // 건반 하나당 할당 폭
                int octaveWidth = (int)((float)scrollWidth/OCTAVE_COUNT); // 옥타브당 폭

                int scrollX = view.getScrollX();
                int bottom = view.getBottom();

                float touchX = event.getX(downPointerIndex);
                float touchY = event.getY(downPointerIndex);
                if(touchY<0) return false;

                int touchKeyX = scrollX + (int) touchX;
                int touchKeyPos = (touchKeyX / keyWhiteWidth);
                int touchYPosPercent = (int)((touchY/((float)bottom))*100);

                int octave = (touchKeyX/octaveWidth)+1;

                String key = ""+KEYS.charAt(touchKeyPos % (KEY_LENGTH));
                if(touchYPosPercent<55){
                    int nearLineX1 = ((touchKeyX/keyWhiteWidth)*keyWhiteWidth);
                    int nearLineX2 = (((touchKeyX/keyWhiteWidth)+1)*keyWhiteWidth);
                    if((touchKeyX-nearLineX1)<(nearLineX2-touchKeyX)){

                        if(((touchKeyX-nearLineX1)/(float)keyWhiteWidth)<0.3f){

                            if("cf".indexOf(key)<0){
                                int keyCharPos = KEYS.indexOf(key)-1;
                                if(keyCharPos<0) keyCharPos = KEY_LENGTH;
                                key = ""+KEYS.charAt(keyCharPos);
                                key += "s";
                            }
                        }
                    }else{

                        if(((nearLineX2-touchKeyX)/(float)keyWhiteWidth)<0.3f){

                            if("eb".indexOf(key)<0) key += "s";
                        }
                    }
                }
                key += octave;

                int soundKey = sMap.get(key);
                int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int sKey = sPool.play(soundKey, streamVolume, streamVolume, 0, 0, 1);

                if(downPointerIndex==0) sKey0 = sKey;
                else if(downPointerIndex==1) sKey1 = sKey;
                else if(downPointerIndex==2) sKey2 = sKey;
            }
            else if(upPointerIndex>=0){
                /**
                 * 여기를 활성화 시키면 음이 너무 끊어져서 듣기 거북하다
                 */

                //	if(upPointerIndex==0){
                //		if(sKey0>0) sPool.stop(sKey0);
                //		sKey0 = 0;
                //	}else if(upPointerIndex==1){
                //		if(sKey1>0) sPool.stop(sKey1);
                //		sKey1 = 0;
                //	}else if(upPointerIndex==2){
                //		if(sKey2>0) sPool.stop(sKey2);
                //		sKey2 = 0;
                //	}

            }

            return false;
        }
    };

    private View.OnTouchListener keysContainerTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void resetSoundPool(){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Please wait");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Integer> tmpMap = new HashMap<String, Integer>();
                SoundPool tmpPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

                try {
                    programNo = pref.getInt("programNo", 1);
                    octaveShift = pref.getInt("octaveShift", 0);
                    MidiFileCreator midiFileCreator = new MidiFileCreator(MainActivity.this);
                    midiFileCreator.createMidiFiles(programNo, octaveShift);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String dir = getDir("", MODE_PRIVATE).getAbsolutePath();
                for(int i=1;i<=OCTAVE_COUNT;i++){
                    for (int j=0;j<PITCHES.length;j++){
                        String soundPath = dir+ File.separator+PITCHES[j]+i+".mid";
                        tmpMap.put(PITCHES[j]+i, tmpPool.load(soundPath, 1));
                    }
                }

                sMap = tmpMap;
                sPool = tmpPool;

                progress.dismiss();
            }
        });
        progress.show();
        thread.start();
    }
}
