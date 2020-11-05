package de.rcoemcs.iotbase;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.rcoemcs.iotbase.Logins.LoginScreen;
import de.rcoemcs.iotbase.databinding.ActivityTwoFaBinding;

public class ActivityTwoFa extends AppCompatActivity {
    private static final String TAG = "Activity2fa";
    ActivityTwoFaBinding activityTwoFaBinding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RequestQueue requestQueue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTwoFaBinding = ActivityTwoFaBinding.inflate(getLayoutInflater());
        View view = activityTwoFaBinding.getRoot();
        setContentView(view);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        preferences = getSharedPreferences("macStore", 0);
        editor = preferences.edit();
        String _MacID = preferences.getString("macId", null);
        activityTwoFaBinding.macSp.setText("Your current MACid :" + _MacID);
        check2faStatus(_MacID);
    }

    public void check2faStatus(String macId) {
        String statusUrl = "http://iotrest.herokuapp.com/api/statusfetcher?macid=" + macId;
        Log.d(TAG, "check2faStatus: " + statusUrl);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                statusUrl,
                null,
                response -> {
                    try {
                        if (response.getJSONObject(0) == null) {
                            Snackbar.make(activityTwoFaBinding.getRoot(), "Invalid MAC : ID No data in DB", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                if (jsonObject.getBoolean("statusCode") ) { //major condition

                                    String macIdGot = jsonObject.getString("macId");
                                    String timeGot = jsonObject.getString("statusTime");
                                    Toast.makeText(this, "" + timeGot, Toast.LENGTH_SHORT).show();

                                    // Handler handler = new Handler();
                                    //handler.postDelayed(() -> startActivity(new Intent(ActivityTwoFa.this, MainActivity.class)), 1500);


                                } else {
//
                                }

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                },
                error -> {
                    Snackbar.make(activityTwoFaBinding.getRoot(), "Invalid User This App is For Valid Users Only", Snackbar.LENGTH_SHORT)
                            .setAction("Retry", v -> check2faStatus(macId))
                            .show();
                }
        );
        requestQueue.add(jsonArrayRequest);

    }
    public String currentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

        String datetime = dateformat.format(c.getTime());
        return datetime;


    }
}
