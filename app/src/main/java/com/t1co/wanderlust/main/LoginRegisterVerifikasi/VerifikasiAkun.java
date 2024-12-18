package com.t1co.wanderlust.main.LoginRegisterVerifikasi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.koneksi.ApiConfig;
import com.t1co.wanderlust.main.koneksi.VolleyHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerifikasiAkun extends AppCompatActivity {

    private EditText[] otpFields = new EditText[6];
    private String email;
    private VolleyHandler volleyHandler;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi_akun);

        email = getIntent().getStringExtra("email");
        volleyHandler = VolleyHandler.getInstance(this);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        if (email == null) {
            email = sharedPreferences.getString("email", "");
        }
        otpFields[0] = findViewById(R.id.otp1);
        otpFields[1] = findViewById(R.id.otp2);
        otpFields[2] = findViewById(R.id.otp3);
        otpFields[3] = findViewById(R.id.otp4);
        otpFields[4] = findViewById(R.id.otp5);
        otpFields[5] = findViewById(R.id.otp6);
        Button verifyButton = findViewById(R.id.verify_button);

        verifyButton.setOnClickListener(v -> {
            String otp = collectOtp();
            if (otp.length() == 6) {
                sendVerificationOtp(otp);
            } else {
                showCustomErrorDialog("Input Tidak Valid", "OTP harus terdiri dari 6 digit!");
            }
        });


        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;

            otpFields[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable[] drawables = otpFields[index].getCompoundDrawables();
                    if (drawables.length > 2 && drawables[2] != null) {
                        if (event.getRawX() >= (otpFields[index].getRight() - drawables[2].getBounds().width())) {
                            toggleOtpField(otpFields[index]);
                            return true;
                        }
                    }
                }
                return false;
            });


            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private String collectOtp() {
        StringBuilder otp = new StringBuilder();
        for (EditText otpField : otpFields) {
            otp.append(otpField.getText().toString());
        }
        return otp.toString();
    }

    private void sendVerificationOtp(String otp) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Memverifikasi OTP...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("otp", otp);

        volleyHandler.makePostRequest(ApiConfig.VERIFY_URL, params, new VolleyHandler.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                handleVerificationResponse(result);
            }

            @Override
            public void onError(String error) {
                progressDialog.dismiss();
                showCustomErrorDialog("Input Tidak Valid", "OTP harus terdiri dari 6 digit!");

            }
        });
    }
    private void toggleOtpField(EditText otpField) {
    }
    private void handleVerificationResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");


            if ("success".equals(status)) {
                String message = jsonObject.getString("message");
                SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isVerified", true); // Tandai verifikasi berhasil
                editor.apply();
                new android.os.Handler().postDelayed(
                        () -> {
                            showSuccessDialog("Verifikasi Berhasil", message);

                            new android.os.Handler().postDelayed(
                                    () -> {
                                        Intent intent = new Intent(VerifikasiAkun.this, LoginPageActivity.class);
                                        startActivity(intent);
                                        finish();
                                    },
                                    20000
                            );
                        },
                        1
                );
            } else {
                String error = jsonObject.getString("message");
                showCustomErrorDialog("Verifikasi Gagal", error);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showCustomErrorDialog("Error", "Terjadi kesalahan dalam pemrosesan data!");
        }
    }

    private void showSuccessDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_verifotp_berhasil);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_info);
        titleView.setText(title);
        messageView.setText(message);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(VerifikasiAkun.this, LoginPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    private void showCustomErrorDialog(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        dialog.getWindow().setAttributes(layoutParams);

        ImageView iconView = dialog.findViewById(R.id.dialogIcon);
        TextView titleView = dialog.findViewById(R.id.dialogTitle);
        TextView messageView = dialog.findViewById(R.id.dialogMessage);
        Button okButton = dialog.findViewById(R.id.dialogButton);

        iconView.setImageResource(R.drawable.ic_error);
        titleView.setText(title);
        messageView.setText(message);
        okButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private enum DialogType {
        ERROR,
        WARNING,
        INFO
    }
}
