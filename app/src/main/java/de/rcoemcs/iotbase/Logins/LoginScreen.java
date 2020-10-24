package de.rcoemcs.iotbase.Logins;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

import de.rcoemcs.iotbase.MainActivity;
import de.rcoemcs.iotbase.databinding.ActivityLoginScreenBinding;

public class LoginScreen extends AppCompatActivity {

    private static final String TAG = "Login Screen ";
    ActivityLoginScreenBinding activityLoginScreenBinding;
    RequestQueue requestQueue;
    String _ObjectID = "";
    String _MACID = "";
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginScreenBinding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        View view = activityLoginScreenBinding.getRoot();
        setContentView(view);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        handlePermissions();


        activityLoginScreenBinding.btnLoginGet.setOnClickListener(v -> {
            checkLogin();
        });

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginScreen.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
                System.exit(0);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Not Recognised Please Retry",
                        Toast.LENGTH_SHORT)
                        .show();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Please Authenticate Device Ownership!")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

        biometricPrompt.authenticate(promptInfo);


    }

    private void checkLogin() {
        String username = Objects.requireNonNull(activityLoginScreenBinding.emailInputLogin.getText()).toString().trim();
        String password = Objects.requireNonNull(activityLoginScreenBinding.passwordInputLogin.getText()).toString().trim();

        String mainUsersGetUrl = "http://iotrest.herokuapp.com/api/iot?name=" + username + "&pwd=" + password;
        String mainPostDeviceFetcherUrl = "http://iotrest.herokuapp.com/api/devicefetcher/";
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
                                    if (jsonObject.getString("userName").equals("varun")) {
                                        _MACID = "11:20:0A:BB:28:FC";

                                    } else if (jsonObject.getString("userName").equals("rohit")) {
                                        _MACID = "00:00:0A:BB:28:FC";
                                    } else if (jsonObject.getString("userName").equals("srihari")) {
                                        _MACID = "BA:9E:1D:6E:93:10";
                                    } else if (jsonObject.getString("userName").equals("suraj")) {
                                        _MACID = "AB:0C:05:BF:3B:89";
                                    } else if (jsonObject.getString("userName").equals("pritesh")) {
                                        _MACID = "9C:F4:B5:65:14:C0";
                                    }else  {
                                        _MACID = "00:00:00:00:20";//default
                                    }
                                    //mac end
                                    Snackbar.make(activityLoginScreenBinding.getRoot(), "Valid User .. Getting Inside..", Snackbar.LENGTH_SHORT)
                                            .show();


                                  PostOperation();
                                    Handler handler = new Handler();
                                    handler.postDelayed(() -> startActivity(new Intent(LoginScreen.this, MainActivity.class)),1000);



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

    public String currentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime;


    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void handlePermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.INTERNET,
                        Manifest.permission.FOREGROUND_SERVICE,
                        Manifest.permission.BLUETOOTH_ADMIN
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                //Toast.makeText(LoginScreen.this, "Checked !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();
    }

    public void PostOperation() {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://iotrest.herokuapp.com/api/devicefetcher";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("loginTime", currentDateTime());
            jsonBody.put("macId", _MACID);
            jsonBody.put("authId", _ObjectID);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> Log.i("LOG_VOLLEY", response), new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}