package com.t1co.wanderlust.main;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.Map;

public class ApiConfig {
    private static final String BASE_IP = "192.168.1.17";
    private static final String BASE_URL = "http://" + BASE_IP + "/apiwanderlustmobile/";

    public static final String REGISTER_URL = BASE_URL + "login_register/buatakun.php";
    public static final String LOGIN_URL = BASE_URL + "login_register/login.php";
    public static final String PROFILE_URL = BASE_URL + "profile/profile.php";

    public static void updateBaseIp(String newIp) {
    }
    public static StringRequest createRegisterRequest(Map<String, String> params,
                                                      Response.Listener<String> responseListener,
                                                      Response.ErrorListener errorListener) {
        return new StringRequest(Request.Method.POST, REGISTER_URL, responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
    }
}