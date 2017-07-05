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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mark Dissel on 4-7-2017.
 */

public class EditHours extends AppCompatActivity {
    String id, datum, status, category, omschrijving, uren, token;
    ArrayList<String> items, statusList, items2;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithours);
        setToken();
        getVariables();
        setTextViews();
        setSpinner();
        addListenerOnSave();
        addListenerOnBack();
    }
    public void setToken() {
        SharedPreferences userDetails = context.getSharedPreferences("userdetails", MODE_PRIVATE);
        token = userDetails.getString("token", "");
    }
    public void setTextViews() {
        EditText urenv = (EditText) findViewById(R.id.uren);
        urenv.setText(uren);
        EditText datumv = (EditText) findViewById(R.id.datetime);
        datumv.setText(datum);
        EditText omschrijvingv = (EditText) findViewById(R.id.omschrijving);
        omschrijvingv.setText(omschrijving);

    }

    public void getVariables() {
        Intent myIntent = getIntent();
        id = myIntent.getStringExtra("id");
        datum= myIntent.getStringExtra("datum");
        omschrijving = myIntent.getStringExtra("omschrijving");
        status = myIntent.getStringExtra("status");
        uren = myIntent.getStringExtra("uren");
        items = myIntent.getStringArrayListExtra("items");
        items2 = myIntent.getStringArrayListExtra("items2");
        statusList = new ArrayList<String>();
        statusList.add(0, "Concept");
        statusList.add(1, "Verstuurd");
        statusList.add(2, "Behandeld");
        statusList.add(3, "Afgekeurd");
    }
    public void addListenerOnSave() {

        TextView displayTextView = (TextView)findViewById(R.id.save);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EditText etomschrijving = (EditText) findViewById(R.id.omschrijving);
                String omschrijving = etomschrijving.getText().toString();
                EditText eturen = (EditText) findViewById(R.id.uren);
                String uren = eturen.getText().toString();
                EditText etdatetime = (EditText) findViewById(R.id.datetime);
                String datetime = etdatetime.getText().toString();
                datetime = datetime.replaceAll("\\s+", "");
                Spinner etstatus = (Spinner) findViewById(R.id.status);
                String status = etstatus.getSelectedItem().toString();
                if(status == "Concept") {
                    status = "1";
                }
                if(status == "Verstuurd") {
                    status = "2";
                }
                if(status == "Behandeld") {
                    status = "3";
                }
                if(status == "Afgekeurd") {
                    status = "4";
                }
                new EditHours.editHours(omschrijving, uren, datetime, status, token, id).execute();

            }

        });

    }
    public void setSpinner() {

        Spinner status = (Spinner)findViewById(R.id.status);
        ArrayAdapter<String> statusa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, statusList);
        status.setAdapter(statusa);
    }
    public void addListenerOnBack() {

        TextView displayTextView = (TextView)findViewById(R.id.back);

        displayTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Overview.class);
                startActivity(intent);

            }

        });

    }


    private class editHours extends AsyncTask<String, Void, Void> {
        String uren, id, datetime, omschrijving, token, status;

        private editHours(String omschrijving, String uren, String datetime, String status, String token, String id) {
            this.uren = uren;
            this.datetime = datetime;
            this.omschrijving = omschrijving;
            this.token = token;
            this.status = status;
            this.id = id;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                boolean error = false;
                final TextView outputview = (TextView) findViewById(R.id.outputview);
                URL url = new URL("http://shift.beertjebijtje.nl/api/registration/" + id);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                final String urlParameters = "uren=" + uren + "&datetime=" + datetime + "&omschrijving=" + omschrijving + "&status=" + status;
                connection.setRequestMethod("PUT");
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
                EditHours.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {


                        if(finalError == false){
                            outputview.setText("Uren succesvol aangepast.");
                        }
                        else {
                            outputview.setText(urlParameters);
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

}


