package shift.shift;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mark Dissel on 26-6-2017.
 */

public class Register extends AppCompatActivity {

    EditText etfirst_name, etlast_name, etemail, etpass1, etpass2;
    String first_name, last_name, email, pass1, pass2;
    boolean register_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addListenerOnText();
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        final Context context = this;

        TextView displayTextView = (TextView)findViewById(R.id.register);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                etfirst_name = (EditText) findViewById(R.id.first_name);
                String first_name = etfirst_name.getText().toString();
                etlast_name = (EditText) findViewById(R.id.last_name);
                String last_name = etlast_name.getText().toString();
                etemail = (EditText) findViewById(R.id.email);
                String email = etemail.getText().toString();
                etpass1 = (EditText) findViewById(R.id.pass1);
                String pass1 = etpass1.getText().toString();
                etpass2 = (EditText) findViewById(R.id.pass2);
                String pass2 = etpass2.getText().toString();

                /**
                 * Hier worden alle alerts aangeroepen. Deze kunnen worden bewerkt in createAlert(), zorg er voor dat je wel een parameter mee geeft.
                 */
                if(!first_name.isEmpty() && !last_name.isEmpty() && !email.isEmpty() && !pass1.isEmpty() && !pass2.isEmpty()) {
                    if (!pass1.equals(pass2)) {
                        CreateAlert.createAlert("Niet_gelijk", context);
                    } else if(!email.contains("@")) {
                        CreateAlert.createAlert("Geen_email", context);
                    } else if(!email.contains(".")) {
                        CreateAlert.createAlert("Geen_email", context);
                    } else {
                        new PostClass(first_name, last_name, email, pass1, pass2).execute();
                        CreateAlert.createAlert("Gelukt", context, false);
                        delayCode();
                    }
                } else {
                    CreateAlert.createAlert("Niet_ingevuld", context);
                }
            }

        });
    }

        private void delayCode() {
        int WELCOME_TIMEOUT = 4000;

        final Context context = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcome = new Intent(context, Login.class);
                startActivity(welcome);
                finish();
            }
        },WELCOME_TIMEOUT);
    }

    public void addListenerOnText() {

        final Context context = this;

        TextView displayTextView = (TextView)findViewById(R.id.loginClick);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Login.class);
                startActivity(intent);

            }

        });

    }



    private class PostClass extends AsyncTask<String, Void, Void> {
        String first_name, last_name, email, pass1, pass2;

        private PostClass(String first, String last, String email, String pass1, String pass2) {
            this.first_name = first;
            this.last_name = last;
            this.email = email;
            this.pass1 = pass1;
            this.pass2 = pass2;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {


                URL url = new URL("http://shift.beertjebijtje.nl/api/user");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "first_name=" + first_name + "&last_name=" + last_name + "&email=" + email + "&pass1=" + pass1 + "&pass2=" + pass2 + "";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();
                int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }




    }
}
