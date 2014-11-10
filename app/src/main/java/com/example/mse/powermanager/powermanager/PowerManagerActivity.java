package com.example.mse.powermanager.powermanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PowerManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    //button start / stop

    public void buttonFirstOnClick(View v){
        Button b = (Button) v;
            if (!PowerManagerActivity.this.isAlarmSet()) {
                PowerManagerActivity.this.setRepeatingAlarm();

                b.setText("Stop Measuring");

                Toast.makeText(
                        getApplicationContext(),
                        "Measurement started",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                PowerManagerActivity.this.removeRepeatingAlarm();

                b.setText("Start Measuring");

                Toast.makeText(
                        getApplicationContext(),
                        "Measurement stopped",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

    //save measurements button
    public void buttonSecondOnClick(View v){
        Button b = (Button) v;
        PowerManagerApp.writeToLog();
        Toast.makeText(
                getApplicationContext(),
                "Written to log",
                Toast.LENGTH_SHORT
        ).show();
    }


    private boolean isAlarmSet(){
        Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                PowerManagerApp.getContext(),
                0,
                intent,
                PendingIntent.FLAG_NO_CREATE
        );

        if (pending != null) {
            return true;
        }

        return false;
    }

    private void removeRepeatingAlarm(){
        AlarmManager am = (AlarmManager)PowerManagerApp.getContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                PowerManagerApp.getContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        PowerManagerApp.setFileId("");
        am.cancel(pending);
        pending.cancel();
    }

    private void setRepeatingAlarm(){
        Thread th = new Thread(new Runnable() {
            public void run() {
                AlarmManager am = (AlarmManager)PowerManagerApp.getContext().getSystemService(Context.ALARM_SERVICE);
                PowerManagerApp.setFileId("" + (System.currentTimeMillis() / 1000));
                Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
                // use the current timestamp as id for the CSVWriter
                intent.putExtra("fileid", "" + (System.currentTimeMillis() / 1000));

                // initialize PendingIntent
                PendingIntent pending = PendingIntent.getBroadcast(
                        PowerManagerApp.getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

                // set repeating alarm every minute
                am.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        60 * 1000,
                        pending
                );
            }
        });

        th.start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
