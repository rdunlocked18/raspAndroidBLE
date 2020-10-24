package de.rcoemcs.iotbase.Logins;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Objects;

import de.rcoemcs.iotbase.MainActivity;
import de.rcoemcs.iotbase.R;
import de.rcoemcs.iotbase.databinding.ActivityLoginScreenBinding;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "Login Screen ";
    ActivityLoginScreenBinding activityLoginScreenBinding;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginScreenBinding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        View view = activityLoginScreenBinding.getRoot();
        setContentView(view);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        activityLoginScreenBinding.btnLoginGet.setOnClickListener(v -> {
            checkLogin();
        });


    }

    private void checkLogin() {
        String username = Objects.requireNonNull(activityLoginScreenBinding.emailInputLogin.getText()).toString().trim();
        String password = Objects.requireNonNull(activityLoginScreenBinding.passwordInputLogin.getText()).toString().trim();

        String mainUsersGetUrl = "http://iotrest.herokuapp.com/api/iot?name=" + username + "&pwd=" + password;
        Log.d(TAG, "checkLogin: " + mainUsersGetUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                mainUsersGetUrl,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject student = response.getJSONObject(i);
                            String firstName = student.getString("userName");
                            String lastName = student.getString("password");
                            Toast.makeText(LoginScreen.this, "" + firstName, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(LoginScreen.this, ""+error, Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}