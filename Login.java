package shift.shift;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import org.json.JSONException;
        import org.json.JSONObject;

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
public class Login extends AppCompatActivity {

    TextView view;
    Button b;
    EditText etemail, etpassword;
    String user, password;
    String token;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addListenerOnText();
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        final Context context = this;
        Button b1 = (Button) findViewById(R.id.login_button);

        b1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                etemail = (EditText) findViewById(R.id.email);
                etpassword = (EditText) findViewById(R.id.password);
                user = etemail.getText().toString();
                password = etpassword.getText().toString();
                if (!user.isEmpty() && !password.isEmpty()) {
                    new Login.PostClass(user, password).execute();
                }
                else {
                    CreateAlert.createAlert("Login", context);

                }

            }

        });

    }

    public void addListenerOnText() {

        final Context context = this;

        TextView displayTextView = (TextView) findViewById(R.id.registerclick);

        displayTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, Register.class);
                startActivity(intent);

            }

        });

    }

    // Store data in SharedPreferences
    private void storeData(String category, String data) {
        SharedPreferences userDetails = this.getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.clear();
        edit.putString(category, data);
        edit.commit();
    }

    // Prevents to press back and get logged in again
    public void onBackPressed() {
        //do nothing
    }


    private class PostClass extends AsyncTask<String, Void, Void> {
        String user, password;

        private PostClass(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                final TextView outputView = (TextView) findViewById(R.id.details);
                URL url = new URL("http://shift.beertjebijtje.nl/api/token");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                String urlParameters = "user=" + user + "&password=" + password;
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
                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    responseOutput.append(line);
                }
                br.close();
                Login.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObj = new JSONObject(responseOutput.toString());
                            token = jsonObj.getString("token");
                            if (token != null && !token.isEmpty()) {
                                storeData("token", token);
                                Intent intent = new Intent(context, Landing.class);
                                startActivity(intent);
                            }
                            else {
                                CreateAlert.createAlert("Login", context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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



