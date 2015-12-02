package com.autopigeonnier.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.GetStartedWithData.R;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import java.net.MalformedURLException;


public class AuthActivity extends Activity {

    EditText Cemail;
    EditText Cpassword;

    Button btnsign;
    Button btnregi;

    ProgressBar mProgressBar;

    MobileServiceClient mClient;
    MobileServiceTable<User> mUserTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Cemail = (EditText) findViewById(R.id.email);
        Cpassword = (EditText) findViewById(R.id.password);

        btnsign = (Button) findViewById(R.id.signin);
        btnregi = (Button) findViewById(R.id.register);

        try {
            // Create the Mobile Service Client instance, using the provided
//			// Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://autopigeonnier.azure-mobile.net/",
                    "iWcdasKsCoNPWBjAnfqQFPMxgDUpYq56",
                    this).withFilter(new ProgressFilter());
             // Get the Mobile Service Table instance to use
            mUserTable = mClient.getTable(User.class);

        }catch (MalformedURLException e){
        };

        btnregi.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Register();
           }
          }
        );
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    signin();
            }
          }
        );
    }
    public void Register(){

          final User user = new User();

          user.setLogin(Cemail.getText().toString());
          user.setPassword(Cpassword.getText().toString());

           new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    mUserTable.insert(user).get();

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


    public void signin(){

                    final String email = Cemail.getText().toString();
                    final String password = Cpassword.getText().toString();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //final MobileServiceList<User> result = mUserTable.where().field("login").eq("MyEmail").execute().get();
                    //MobileServiceList<User> result = mUserTable.
                  final MobileServiceList<User> result = mUserTable.where().field("login").eq(email).execute().get();

                    for (final User item : result) {

                       if(item.getPassword().equals(password)){
                        runOnUiThread(new Runnable() {
                        public void run() {

                            Context context = getApplicationContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context,"Your welcome",duration);
                            toast.show();
                            Intent intent = new Intent(AuthActivity.this, com.autopigeonnier.dashboard.MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                   }
                   else{
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    Context context = getApplicationContext();
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context,"No go back and check on your login or password ", duration);
                                    toast.show();
                                }
                            });
                        }
                        }

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
}
