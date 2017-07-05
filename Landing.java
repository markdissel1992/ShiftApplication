package shift.shift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mark Dissel on 27-6-2017.
 */

public class Landing extends AppCompatActivity {

    TextView name;
    final Context context = this;
    String token;
    String userid = "";
    String pre_name;
    String urltje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        setToken();
        addListenerOnLogOut();
        addListenerOnOverview();
        addListenerOnAddHours();
        addListenerOnCreateCategory();
        getUser gu = new getUser(token);
        gu.execute();
    }

    private void addListenerOnCreateCategory() {

        TextView displayTextView = (TextView) findViewById(R.id.addcategory);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, CreateCategory.class);
                startActivity(intent);

            }

        });
    }

    public void setToken() {
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        token = userDetails.getString("token", "");
    }

    public void addListenerOnOverview() {

        TextView displayTextView = (TextView) findViewById(R.id.overview);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Overview.class);
                startActivity(intent);

            }

        });

    }

    public void addListenerOnLogOut() {
        TextView displayTextView = (TextView) findViewById(R.id.logout);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SharedPreferences preferences = getSharedPreferences("userdetails", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                finish();
                Intent intent = new Intent(context, Login.class);
                startActivity(intent);

            }

        });

    }

    public void addListenerOnAddHours() {

        TextView displayTextView = (TextView) findViewById(R.id.addhours);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, AddHours.class);
                startActivity(intent);

            }

        });
    }

    private class getUser extends AsyncTask<String, Void, Void> {
        String token;

        private getUser(String token) {
            this.token = token;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String message = "jo";
                URL oracle = new URL("http://shift.beertjebijtje.nl/api/token/" + token);
                final BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    message = inputLine;
                in.close();
                final String finalmessage = message;
                Landing.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        String message = finalmessage;
                        String id = "";
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(message.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            userid = jsonObj.getString("user");
                            getNames gn = new getNames(token, userid);
                            gn.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
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

    private class getNames extends AsyncTask<String, Void, Void> {
        String token;
        String userid;

        private getNames(String token, String userid) {
            this.token = token;
            this.userid = userid;
        }

        @Override
        protected Void doInBackground(String... params) {

            URL obj = null;
            try {
                obj = new URL("http://shift.beertjebijtje.nl/api/user/" + userid);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            HttpURLConnection con = null;
            try {
                con = (HttpURLConnection) obj.openConnection();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // optional default is GET
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            }

            //add request header
            con.setRequestProperty("token", token);

            BufferedReader in = null;
            try {
                in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String inputLine;
            final StringBuffer response = new StringBuffer();

            try {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Landing.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String message = response.toString();
                    JSONObject jsonObj = null;
                    String last_name = "";
                    try {
                        jsonObj = new JSONObject(message.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        pre_name = jsonObj.getString("first_name");
                        last_name = jsonObj.getString("last_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final TextView outputView = (TextView) findViewById(R.id.landingText);
                    outputView.setText("Welkom " + pre_name + " " + last_name);
                }
            });


            return null;
        }


    }
}
