package com.autopigeonnier.note;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class ShowNoteActivity extends Activity {

    private MobileServiceClient mClient;
    private MobileServiceTable<Note> mNoteTable;

    private TextView mTextViewTitle;
    private TextView mTextViewBody;

    private ProgressBar mProgressBar;
    private String id ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);

        mTextViewTitle = (TextView) findViewById(R.id.titresow);
        mTextViewBody = (TextView) findViewById(R.id.bodyshow);

        try {
//			// Create the Mobile Service Client instance, using the provided
//			// Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://autopigeonnier.azure-mobile.net/",
                    "iWcdasKsCoNPWBjAnfqQFPMxgDUpYq56",
                    this).withFilter(new ProgressFilter());

            // Get the Mobile Service Table instance to use
            mNoteTable = mClient.getTable(Note.class);
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        }

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Note result = mNoteTable.lookUp(id).get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mTextViewTitle.setText(result.getmTitle());
                            mTextViewBody.setText(result.getmBody());
                        }
                    });
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
    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

}
