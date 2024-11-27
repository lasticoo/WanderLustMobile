package com.t1co.wanderlust.main.koneksi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.Map;

public class ApiConfig {
    private static final String BASE_IP = "172.28.128.1";
    private static final String BASE_URL = "http://" + BASE_IP + "/wanderlust2/apiwanderlustmobile/";

    public static final String REGISTER_URL = BASE_URL + "login_register/buatakun.php";
    public static final String LOGIN_URL = BASE_URL + "login_register/login.php";
    public static final String VERIFY_URL = BASE_URL + "login_register/vertifikasiOTP.php";
    public static final String forgot_password = BASE_URL + "forgot_password/forgotpassword.php";
    public static final String reset_password = BASE_URL + "forgot_password/resetpassword.php";
    public static final String verifikasi_Otp = BASE_URL + "forgot_password/VerifikasiOtp.php";
    public static final String PROFILE_URL = BASE_URL + "profile/tampilprofile.php";
    public static final String DASHBOARD_URL = BASE_URL + "dashboard.php";
    public static final String KRITIK_URL = BASE_URL + "kritikdansaran.php";
    public static final String TAMPILBERITA_URL = BASE_URL + "berita/tampilberita.php";
    public static final String EDITPROFILE_URL = BASE_URL + "profile/editprofile.php";
    public static final String SPINNER_URL= BASE_URL + "jadwal/spinnerjadwal.php";
    public static final String CARIJADWAL_URL = BASE_URL + "jadwal/carijadwal.php";
    public static final String PEMESANAN_URL = BASE_URL + "pemesanan/pemesanan.php";
    public static final String CekStatusPembayaran_URL = BASE_URL + "pemesanan/cekstatuspembayaran.php";

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