package com.example.wilfriedeh.splashtalk;

import com.example.wilfriedeh.splashtalk.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


/**
 */
public class MainScreen extends Activity {

    /**
     * Turn On and Off Splash Talk on Click
     */
    private static boolean turnAppOn = false;
    private static boolean receiverOn = false;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen);

        final View contentView = findViewById(R.id.fullscreen_content);
        final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

        final IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.hide();

        //Initializing the Splash Screen text View
        final TextView splashScreen = (TextView)contentView;
        displayOnAndOffViewState(turnAppOn, splashScreen);

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (turnAppOn) {
                    //IF application is turned On DO THE FOLLOWING
                    Log.i("SplashTalk", "TurnedOn");
                    //Turn on the receiver
                    registerReceiver(receiver_SMS, filter);
                    receiverOn = true;
                    Log.i("Receiver", "Turned On");
                }
                if (receiverOn && !turnAppOn){
                    //If the receiver is Turned on and app is not on make sure to unregister it the receiver.
                    Log.i("SplashTalk", "Turned Off");
                    unregisterReceiver(receiver_SMS);
                    Log.i("Receiver", "Turned Off");
                    receiverOn = false;
                }
                displayOnAndOffViewState(turnAppOn, splashScreen);
                //After actions taken, Toggle TurnAppOn on click
                turnAppOn = !turnAppOn;
            }
        });
    }

    private void displayOnAndOffViewState(boolean turnedOnState,TextView view) {
        if (turnedOnState) {
            view.setText(Html.fromHtml("<h2><font color=#09B786>On</font></h2><br><p>Tap to Turn Off</p>"));
        } else {
            view.setText(Html.fromHtml("<h2><font color=#C50029>Off</font></h2><br><p>Tap to turn On</p>"));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
    /**
     * Called when a new message is received
     * previously scheduled calls.
     */
    BroadcastReceiver receiver_SMS = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            Bundle extras = intent.getExtras();
            if (extras == null)
                return;

            Object[] pdus = (Object[]) extras.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String sender = SMessage.getOriginatingAddress();
                String body = SMessage.getMessageBody().toString();
                receivedMessage(body);
            }
        }
    };
    private void receivedMessage(String message)
    {
        Log.i("Splash-Message", message);
        //... do whatever with the message here
    }





    @Override
    protected void onDestroy (){
        unregisterReceiver(receiver_SMS);
    }
}
