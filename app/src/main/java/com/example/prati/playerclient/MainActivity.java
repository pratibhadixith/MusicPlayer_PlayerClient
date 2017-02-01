package com.example.prati.playerclient;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.prati.KeyCommon.MyMP;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton pauseBtn = null;
    private ImageButton playBtn = null;
    private ImageButton stopBtn = null;
    private Button viewTransactionBtn = null;
    private EditText clipnumber = null;
    private int audionumber;
    private MyMP myMPGeneratorService;
    private boolean mIsBound = false;
    private ServiceConnection mConnection;
    ArrayList<String> listofTransactions = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create UI objects
        pauseBtn = (ImageButton) findViewById(R.id.pause);
        playBtn = (ImageButton) findViewById(R.id.play);
        stopBtn = (ImageButton) findViewById(R.id.stop);
        viewTransactionBtn = (Button) findViewById(R.id.transactions);
        clipnumber = (EditText) findViewById(R.id.audionumber);


        // Set Listeners for the UI elements to perform actions when buttons are clicked
        pauseBtn.setOnClickListener(onSomeIconClick);
        playBtn.setOnClickListener(onSomeIconClick);
        stopBtn.setOnClickListener(onSomeIconClick);
        viewTransactionBtn.setOnClickListener(onSomeIconClick);

        //Create ServiceConnection object and define the actions on Service connection and disconnection
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder iservice) {
                myMPGeneratorService = MyMP.Stub.asInterface(iservice);
                mIsBound = true;
            }

            public void onServiceDisconnected(ComponentName className) {
                myMPGeneratorService = null;
                mIsBound = false;
            }
        };

        // Bind to MyMp Service
        if (!mIsBound) {
            boolean bindStatus = false;
            Intent i = new Intent(MyMP.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, Context.BIND_AUTO_CREATE);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            bindStatus = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (bindStatus) {
                Log.i("Mplayer says", "BindService was successfull");
            } else {
                Log.i("Mplayer says", "BindService failed");
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            myMPGeneratorService.stopclip(audionumber);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(mConnection);

    }

    //verify the text edited and perform corresponding actions
    private View.OnClickListener onSomeIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //check the input and test if its a valid number
            if (v.getId() != R.id.transactions) {
                String showtoast = "Enter clip number from 1-5";
                String Userchoice = clipnumber.getText().toString();
                String regex_pattern = "^[0-5]*$";

                if (Userchoice.length() == 0) {
                    Toast.makeText(getApplicationContext(), (String) showtoast, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Userchoice.matches(regex_pattern)) {
                    audionumber = Integer.parseInt(clipnumber.getText().toString());
                    if (audionumber <= 0 || audionumber > 5) {
                        Toast.makeText(getApplicationContext(), (String) showtoast, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), (String) showtoast, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //switch the actions based on the input entered by user
            switch (v.getId()) {
                case R.id.play: {
                    try {
                        if (mIsBound) {
                            //call the method resume_play_clip (declared in AIDL) implemented at the server side whenever
                            // user does play or resume song
                            myMPGeneratorService.resume_play_clip(audionumber);
                            listofTransactions.add("Clip " + audionumber + " Play");
                        } else {
                            Log.i("Mplayer says", "Service not bound");
                        }
                    } catch (RemoteException e) {
                        Log.i("Mplayer says","Exception thown in play at client");
                    }
                    break;
                }
                case R.id.stop: {

                    try {
                        if (mIsBound) {
                            //call the method stopclip (declared in AIDL) implemented at server side whenever
                            // user does stop song
                            myMPGeneratorService.stopclip(audionumber);
                            listofTransactions.add("Clip " + audionumber + " Stop");
                        } else {
                            Log.i("Mplayer says", "Service not bound");
                        }
                    } catch (RemoteException e) {
                        Log.i("Mplayer says","Exception thown in stop at client");
                    }
                    break;
                }
                case R.id.pause: {
                    try {

                        if (mIsBound) {
                            //call the method pauseclip (declared in AIDL) implemented at server side whenever
                            // user does pause song
                            myMPGeneratorService.pauseclip(audionumber);
                            listofTransactions.add("Clip " + audionumber + " Pause");
                        } else {
                            Log.i("Mplayer says", "Service not bound");
                        }
                    } catch (RemoteException e) {
                        Log.i("Mplayer says","Exception thown in pause at client");
                    }
                    break;
                }
                case R.id.transactions: {

                    if (mIsBound) {
                        //create an intent to show listofTransactions, and call class ViewTransactions.class to display the list
                        Intent showlist = new Intent(getApplicationContext(), ViewTransactions.class);
                        showlist.putExtra("list", (ArrayList) listofTransactions);
                        startActivity(showlist);
                    } else {
                        Log.i("Mplayer says", "Service not bound");
                    }
                }

            }
        }


    };

}
