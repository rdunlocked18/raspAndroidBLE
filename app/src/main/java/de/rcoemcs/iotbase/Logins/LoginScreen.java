package de.rcoemcs.iotbase.Logins;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.rcoemcs.iotbase.MainActivity;
import de.rcoemcs.iotbase.R;
import de.rcoemcs.iotbase.databinding.ActivityLoginScreenBinding;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "Login Screen ";
    ActivityLoginScreenBinding activityLoginScreenBinding;
    RequestQueue requestQueue;
    String _ObjectID = "";
    String _MACID = "";
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
                        if (response.getJSONObject(0) == null) {
                            Snackbar.make(activityLoginScreenBinding.getRoot(), "Check Username or Password", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                if (jsonObject.getBoolean("userValid")) {
                                    _ObjectID = jsonObject.getString("_id");
                                    //sending macid as static
                                    if(jsonObject.getString("userName").equals("varun")){
                                        _MACID = "11:20:0A:BB:28:FC";
                                    }else  if(jsonObject.getString("userName").equals("rohit")){
                                        _MACID = "00:00:0A:BB:28:FC";
                                    }else  if(jsonObject.getString("userName").equals("srihari")){
                                        _MACID = "BA:9E:1D:6E:93:10";
                                    }else  if(jsonObject.getString("userName").equals("suraj")){
                                        _MACID = "AB:0C:05:BF:3B:89";
                                    }else  if(jsonObject.getString("userName").equals("pritesh")){
                                        _MACID = "9C:F4:B5:65:14:C0";
                                    }
                                    //mac end
                                    Snackbar.make(activityLoginScreenBinding.getRoot(), "Valid User .. Getting Inside..", Snackbar.LENGTH_SHORT)
                                            .show();
                                    jsonPostToDeviceFetcher();

                                } else {
                                    Snackbar.make(activityLoginScreenBinding.getRoot(), "Ask Admins to Validate User", Snackbar.LENGTH_SHORT)
                                            .setAction("Retry", v -> checkLogin())
                                            .show();
                                }

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(activityLoginScreenBinding.getRoot(), "Check Username or Password", Snackbar.LENGTH_SHORT)
                                .setAction("Retry", v -> checkLogin())
                                .show();
                    }

                },
                error -> {
                    Snackbar.make(activityLoginScreenBinding.getRoot(), "Invalid User This App is For Valid Users Only", Snackbar.LENGTH_SHORT)
                            .setAction("Retry", v -> checkLogin())
                            .show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void jsonPostToDeviceFetcher() {


        Log.d(TAG, "jsonPostToDeviceFetcher: " + currentDateTime());

    }

    public String currentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime;
    }
}