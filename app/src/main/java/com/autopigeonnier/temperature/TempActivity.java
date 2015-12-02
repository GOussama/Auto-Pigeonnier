package com.autopigeonnier.temperature;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.GetStartedWithData.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;

public class TempActivity extends Activity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Temperature> mTemperatureTable;

    private ProgressBar mProgressBar;

    private TextView mActuelle;
    private TextView Cstatus;

    private EditText cmax;
    private EditText cmin;

    Button btnsave;
    Button btnann;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        mActuelle = (TextView) findViewById(R.id.actuel);
        Cstatus = (TextView) findViewById(R.id.status);

        cmax = (EditText) findViewById(R.id.max);
        cmin = (EditText) findViewById(R.id.min);

        btnsave = (Button) findViewById(R.id.savebtn);

        try {
            // Create the Mobile Service Client instance, using the provided
//			// Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://autopigeonnier.azure-mobile.net/",
                    "iWcdasKsCoNPWBjAnfqQFPMxgDUpYq56",
                    this).withFilter(new ProgressFilter());
            // Get the Mobile Service Table instance to use
            mTemperatureTable = mClient.getTable(Temperature.class);

        }catch (MalformedURLException e){
        };

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    public void Register(){

        final Temperature temperature = new Temperature();

        temperature.setmMax(cmax.getText().toString());
        temperature.setmMin(cmin.getText().toString());
        temperature.setmInstantatee(mActuelle.getText().toString());

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mTemperatureTable.insert(temperature).get();

                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                ServiceFilterRequest request, NextServiceFilterCallback next) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
            try {
                ServiceFilterResponse response = next.onNext(request).get();
                result.set(response);
            } catch (Exception exc) {
                result.setException(exc);
            }

            dismissProgressBar();
            return result;
        }
        private void dismissProgressBar() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                }
            });
        }
    }

    private void createAndShowDialog(Exception exception, String title) {
        createAndShowDialog(exception.toString(), title);
    }

    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

}
