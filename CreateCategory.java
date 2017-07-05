package shift.shift;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mark Dissel on 4-7-2017.
 */

public class CreateCategory extends AppCompatActivity {
    final Context context = this;
    String name;
    String token;
    String hex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcategory);
        setToken();
        addListenerOnButton();
        setupColorCodePicker();
    }

    public void setToken() {
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        token = userDetails.getString("token", "");
    }

    public void addListenerOnButton() {

        final Context context = this;

        TextView displayTextView = (TextView) findViewById(R.id.createcategory);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EditText etname = (EditText) findViewById(R.id.name);
                String name = etname.getText().toString();
                if (!name.isEmpty()) {
                    new CreateCategory.PostClass(token, name).execute();
                } else {
                    CreateAlert.createAlert("Cat_fail", context);
                }
            }

        });


    }

    private class PostClass extends AsyncTask<String, Void, Void> {
        String token, name;

        private PostClass(String token, String name) {
            this.token = token;
            this.name = name;
        }


        @Override
        protected Void doInBackground(String... params) {
            try {
                boolean error = false;
                URL url = new URL("http://shift.beertjebijtje.nl/api/category");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "name=" + name + "&colour=" + hex;
                connection.setRequestMethod("POST");
                connection.setRequestProperty("token", token);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();
                final int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    final StringBuilder responseOutput = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line);
                    }
                    br.close();
                } catch (Exception e) {
                    error = true;
                }

                final boolean finalError = error;
                CreateCategory.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (finalError == false) {
                            CreateAlert.createAlert("Gelukt_cat", context, true);
                            delayCode();
                        } else {
                            CreateAlert.createAlert("Cat_fail", context);
                        }

                    }
                });


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

    /**
     * Bedoelt voor Alert
     */
    private void delayCode() {
        int WELCOME_TIMEOUT = 4000;
        final Context context = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcome = new Intent(context, Landing.class);
                startActivity(welcome);
                finish();
            }
        },WELCOME_TIMEOUT);
    }

    public void setupColorCodePicker() {
        TextView displayTextView = (TextView)findViewById(R.id.colorPicker);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final ColorPicker colorPicker = new ColorPicker(
                        CreateCategory.this, // Context
                        127, // Default Red value
                        123, // Default Green value
                        67 // Default Blue value
                );
                colorPicker.show();
                Button okColor = (Button)colorPicker.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int selectedColorR = colorPicker.getRed();
                        int selectedColorG = colorPicker.getGreen();
                        int selectedColorB = colorPicker.getBlue();

                        hex = String.format("#%02x%02x%02x", selectedColorR, selectedColorG,selectedColorB);

                        Button tijdelijk = (Button)findViewById(R.id.colorPicker);
                        tijdelijk.setBackgroundColor(Color.parseColor(hex));

                        colorPicker.dismiss();
                    }
                });
            }

        });
    }

}
