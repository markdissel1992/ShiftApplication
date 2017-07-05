package shift.shift;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mark Dissel on 26-6-2017.
 */

public class AddHours extends AppCompatActivity {
    final Context context = this;
    String token;
    TextView etomschrijving, eturen, etdatetime;
    Spinner etcategory;
    ArrayList items = new ArrayList();
    ArrayList items2 = new ArrayList();
    ArrayList list = new ArrayList();
    boolean noCat = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhours);
        setToken();
        setCategories();
        addListenerOnBack();
        addListenerOnSave();
        ArrayList<String> teams = new ArrayList<>();
        teams.add("Geen team");
        Spinner dropdown = (Spinner) findViewById(R.id.spinner_team);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, teams);
        dropdown.setAdapter(adapter);
    }

    public void setCategories() {
        new AddHours.getCategory(token).execute();
    }
    public void addListenerOnBack() {

        TextView displayTextView = (TextView)findViewById(R.id.back);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Landing.class);
                startActivity(intent);

            }

        });

    }
    public void addListenerOnSave() {

        TextView displayTextView = (TextView)findViewById(R.id.save);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etomschrijving = (EditText) findViewById(R.id.omschrijving);
                String omschrijving = etomschrijving.getText().toString();
                etcategory = (Spinner) findViewById(R.id.spinner);
                String item = etcategory.getSelectedItem().toString();
                if(noCat == false) {
                    int pos = items.indexOf(item);
                    String cat = (String) items2.get(pos);
                    int category = Integer.parseInt(cat);
                    eturen = (EditText) findViewById(R.id.uren);
                    String uren = eturen.getText().toString();
                    etdatetime = (EditText) findViewById(R.id.datetime);
                    String datetime = etdatetime.getText().toString();
                    datetime = datetime.replaceAll("\\s+", "");
                    new AddHours.PostClass(uren, category, datetime, omschrijving, token).execute();
                }



            }

        });

    }
    public void emptyFields() {
        etomschrijving.setText("");
        eturen.setText("");
        etdatetime.setText("");
    }
    public void setToken() {
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        token = userDetails.getString("token", "");
    }

    private class PostClass extends AsyncTask<String, Void, Void> {
        String uren,  datetime, omschrijving, token;
        int category;

        private PostClass(String uren, int category, String datetime, String omschrijving, String token) {
            this.uren = uren;
            this.category = category;
            this.datetime = datetime;
            this.omschrijving = omschrijving;
            this.token = token;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                boolean error = false;
                final TextView outputview = (TextView) findViewById(R.id.outputview);
                URL url = new URL("http://shift.beertjebijtje.nl/api/registration");
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                final String urlParameters = "uren=" + uren + "&category=" + category + "&datetime=" + datetime + "&omschrijving=" +omschrijving;
                connection.setRequestMethod("POST");
                connection.setRequestProperty("token", token);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();
                final int responseCode = connection.getResponseCode();
                final StringBuilder output = new StringBuilder("Request URL " + url);
                output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";
                    final StringBuilder responseOutput = new StringBuilder();
                    while((line = br.readLine()) != null ) {
                        responseOutput.append(line);
                    }
                    br.close();
                }catch(Exception e){
                    error = true;
                }

                final boolean finalError = error;
                AddHours.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(finalError == false) {
                            outputview.setText("Uren zijn succesvol toegevoegd.");
                            emptyFields();

                        }
                        else {
                            outputview.setText("Helaas niet gelukt.");
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
            AddHours.this.runOnUiThread(new Runnable() {

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
                        if(items.size() > 0) {
                            Spinner dropdown = (Spinner) findViewById(R.id.spinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items);
                            dropdown.setAdapter(adapter);
                        }
                        else {
                            list.add("Maak eerst een categorie");
                            noCat = true;
                            Spinner dropdown = (Spinner) findViewById(R.id.spinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
                            dropdown.setAdapter(adapter);
                        }
                    }
                });
            return null;
        }




    }


}

