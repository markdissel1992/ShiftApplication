package shift.shift;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hgv_m on 28-6-2017.
 */

public class Overview extends AppCompatActivity {
    String token;
    final Context context = this;
    ArrayList<String> items = new ArrayList<String>();
    ArrayList<String> items2 = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        addListenerOnGoBack();
        setToken();
        getCategory gc = new getCategory(token);
        gc.execute();
        getHours();
    }

    public void addListenerOnGoBack() {

        final Context context = this;

        TextView displayTextView = (TextView)findViewById(R.id.back);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Landing.class);
                startActivity(intent);

            }

        });

    }

    public void setToken() {
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        token = userDetails.getString("token", "");
    }
    public void getHours() {
        getHours gh = new getHours(token);
        gh.execute();
    }

    private class getHours extends AsyncTask<String, Void, Void> {
        String token;

        private getHours(String token) {
            this.token = token;
        }
        @Override
        protected Void doInBackground(String... params) {
            URL obj = null;
            try {
                obj = new URL("http://shift.beertjebijtje.nl/api/registration");
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
            Overview.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String responses = response.toString();
                    JSONArray arr = null;
                    try {
                        arr = new JSONArray(responses);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    TableLayout tl = (TableLayout)findViewById(R.id.table);

                    for (int i=0; i < arr.length(); i++) {

                        try {
                            TableRow tr = new TableRow(context);
                            tr.setBackgroundColor(Color.parseColor("#ECEFF1"));
                            tr.setMinimumHeight(60);
                            TextView tv1 = new TextView(context);
                            tv1.setGravity(Gravity.CENTER);
                            TextView tv2 = new TextView(context);
                            tv2.setGravity(Gravity.CENTER);
                            TextView tv3 = new TextView(context);
                            tv3.setGravity(Gravity.CENTER);
                            TextView tv4 = new TextView(context);
                            tv4.setGravity(Gravity.CENTER);
                            TextView tv5 = new TextView(context);
                            tv5.setGravity(Gravity.CENTER);
                            JSONObject obj = arr.getJSONObject(i);
                            String status = obj.getString("status");
                            final String id = obj.getString("id");
                            if(status.equals("1")) {
                                status = "Concept";
                            }
                            if(status.equals("2")) {
                                status = "Verstuurd";
                            }
                            if(status.equals("3")) {
                                status = "Behandeld";
                            }
                            if(status.equals("4")) {
                                status = "Afgewezen";
                            }
                            tv1.setText(status);
                            String day = obj.getString("day");
                            if(Integer.parseInt(day) < 10){
                                day = "0" + day;
                            }
                            String month = obj.getString("month");
                            if(Integer.parseInt(month) < 10){
                                month = "0" + month;
                            }
                            String year = obj.getString("year");
                            final String datum = day + " - " + month;
                            final String passdatum = datum + " - " + year;
                            tv2.setText(datum);
                            final String uren = obj.getString("hours");
                            tv3.setText(uren);
                            final String nr = obj.getString("category");
                            int pos = items2.indexOf(nr);
                            String catName = items.get(pos);
                            tv4.setText(catName);
                            final String omschrijving = obj.getString("description");
                            tv5.setText(omschrijving);
                            tv1.setTextSize(20);
                            tv2.setTextSize(20);
                            tv3.setTextSize(20);
                            tv4.setTextSize(20);
                            tv5.setTextSize(20);
                            tv1.setHeight(100);
                            tv2.setHeight(100);
                            tv3.setHeight(100);
                            tv4.setHeight(100);
                            tv5.setHeight(100);
                            tr.addView(tv1);
                            tr.addView(tv2);
                            tr.addView(tv3);
                            tr.addView(tv4);
                            tr.addView(tv5);
                            final String finalStatus = status;
                            tr.setClickable(true);

                            View.OnClickListener onClickListener= new View.OnClickListener() {
                                public void onClick(View v) {
                                    Intent myIntent = new Intent(context, EditHours.class);
                                    myIntent.putExtra("status", finalStatus);
                                    myIntent.putExtra("datum", passdatum);
                                    myIntent.putExtra("id", id);
                                    myIntent.putExtra("category", nr);
                                    myIntent.putExtra("omschrijving", omschrijving);
                                    myIntent.putExtra("uren", uren);
                                    myIntent.putStringArrayListExtra("items", items);
                                    myIntent.putStringArrayListExtra("items2", items2);
                                    startActivity(myIntent);

                                }
                            };
                            tr.setOnClickListener(onClickListener);
                            tl.addView(tr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            return null;
        }




    }
    private class getCategory extends AsyncTask<String, Void, Void> {
        String token;

        private getCategory(String token) {
            this.token = token;
        }
        @Override
        protected Void doInBackground(String... params) {
            URL obj = null;
            try {
                obj = new URL("http://shift.beertjebijtje.nl/api/category");
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
            Overview.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    String responses = response.toString();
                    JSONArray arr = null;
                    try {
                        arr = new JSONArray(responses);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i=0; i < arr.length(); i++) {

                        try {
                            String namew = arr.getJSONObject(i).getString("name");
                            items.add(namew);
                            String id = arr.getJSONObject(i).getString("id");
                            items2.add(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
            return null;
        }




    }

}
