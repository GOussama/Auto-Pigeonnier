package com.autopigeonnier.note;

import android.app.Activity;

import com.example.GetStartedWithData.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.net.MalformedURLException;

public class AddNoteActivity extends Activity {


    MobileServiceClient mClient;
    MobileServiceTable<Note> mNoteTable;

    ProgressBar mProgressBar;

    EditText Ctitle;
    EditText Cbody;

    Button btnsave;
    Button btnann;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Ctitle = (EditText) findViewById(R.id.titretext);
        Cbody = (EditText) findViewById(R.id.bodytext);

        btnsave = (Button) findViewById(R.id.savebtn);
        btnann = (Button) findViewById(R.id.cancelbtn);

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

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    public void save(){

        final Note note = new Note();

        note.setmTitle(Ctitle.getText().toString());
        note.setmBody(Cbody.getText().toString());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    mNoteTable.insert(note).get();

                    /*
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context,"Bien enregistrer", duration);
                    toast.show();
                    */

                } catch (Exception exception) {
                    createAndShowDialog(exception, "Error");
                }
                return null;
            }
        }.execute();
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


}
