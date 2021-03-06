package in.reweyou.reweyou.classes;

/**
 * Created by Reweyou on 1/23/2016.
 */
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import in.reweyou.reweyou.Feed;
import in.reweyou.reweyou.ImageCapture;
import in.reweyou.reweyou.Welcome;

/**
 * Created by Ravi on 04/04/15.
 */
public class HttpService extends IntentService {
    public static final String URL_VERIFY_OTP = "https://www.reweyou.in/reweyou/verify_otp.php";
    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            verifyOtp(otp);

        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param
     */
    private void verifyOtp(final String otp) {
        UserSessionManager session = new UserSessionManager(getApplicationContext());
        final String number = session.getMobileNumber();
        // final ProgressDialog loading = ProgressDialog.show(this, "Authenticating", "Please wait", false, false);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //if the server response is success
                if(response.equalsIgnoreCase("success")){
                    //dismissing the progressbar
                    //     loading.dismiss();

                    //Starting a new activity
                    openProfile();
                }else{
                    //Displaying a toast if the otp entered is wrong
                    Toast.makeText(getApplicationContext(),"Wrong OTP Please Try Again",Toast.LENGTH_LONG).show();

                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"Something went wrong, Try again",Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("number", number);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void openProfile() {
        //Intent intent = new Intent(this, IconTabs.class);
        //intent.putExtra(KEY_USERNAME, username);
        //startActivity(intent);
        UserSessionManager session = new UserSessionManager(getApplicationContext());
        String place=session.getLoginLocation();
        String number=session.getMobileNumber();
        String name = session.getUsername();
        session.createUserRegisterSession(name,number, place);

        // Starting TokenTest
        Intent i = new Intent(this, Welcome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i); // Call once you redirect to another activity
        this.stopSelf();
    }
}