package com.t1co.wanderlust.main.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.t1co.wanderlust.R;
import com.t1co.wanderlust.main.History.HistoryPageActivity;
import com.t1co.wanderlust.main.Jadwal.JadwalCariActivity;
import com.t1co.wanderlust.main.KritikDanSaran.KritikDanSaranPageActivity;

public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private ViewFlipper viewFlipper;
    private DrawerLayout drawerLayout;
    private ImageView menuButton;
    private float lastX;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard_page, container, false);

        viewFlipper = view.findViewById(R.id.viewFlipper);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getX();
                        return true;

                    case MotionEvent.ACTION_UP:
                        float currentX = event.getX();
                        float deltaX = lastX - currentX;

                        // Swipe threshold
                        if (Math.abs(deltaX) > 150) {
                            if (deltaX > 0) {
                                viewFlipper.showNext();
                            } else {
                                viewFlipper.showPrevious();
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        drawerLayout = view.findViewById(R.id.drawer_layout);
        menuButton = view.findViewById(R.id.menu_button);

        View historyButton = view.findViewById(R.id.button_image);
        historyButton.setOnClickListener(this::onHistoryButtonClick);

        ImageButton buttonKritik = view.findViewById(R.id.button_kritik);
        buttonKritik.setOnClickListener(this::onKritikButtonClick);

        ImageButton buttonpemesanan = view.findViewById(R.id.button_pemesanan);
        buttonpemesanan.setOnClickListener(this::onPemesananButtonClick);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewFlipper != null) {
            viewFlipper.stopFlipping();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewFlipper != null) {
            viewFlipper.startFlipping();
        }
    }

    public void onHistoryButtonClick(View view) {
        Log.d(TAG, "History button clicked");
        Intent intent = new Intent(getActivity(), HistoryPageActivity.class);
        startActivity(intent);
    }

    public void onKritikButtonClick(View view) {
        Log.d(TAG, "Kritik dan Saran button clicked");
        Intent intent = new Intent(getActivity(), KritikDanSaranPageActivity.class);
        startActivity(intent);
    }

    public void onPemesananButtonClick(View view) {
        Log.d(TAG, "Pemesanan button clicked");
        Intent intent = new Intent(getActivity(), JadwalCariActivity.class);
        startActivity(intent);
    }
}