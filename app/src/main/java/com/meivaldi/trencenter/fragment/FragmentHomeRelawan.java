package com.meivaldi.trencenter.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.meivaldi.trencenter.R;
import com.meivaldi.trencenter.activity.DetailPenghargaan;
import com.meivaldi.trencenter.activity.DetailPlatform;
import com.meivaldi.trencenter.activity.DetailVisiMisi;
import com.meivaldi.trencenter.activity.Penghargaan;
import com.meivaldi.trencenter.activity.Platform;
import com.meivaldi.trencenter.activity.ProgramKerja;
import com.meivaldi.trencenter.activity.VisiMisi;
import com.meivaldi.trencenter.activity.caleg.DataCaleg;
import com.meivaldi.trencenter.activity.caleg.DetailCaleg;
import com.meivaldi.trencenter.activity.pendukung.InputPendukung;
import com.meivaldi.trencenter.adapter.ViewPagerAdapter;
import com.meivaldi.trencenter.helper.SliderUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHomeRelawan extends Fragment {

    private TextView hari, detik, menit, jam;
    private RelativeLayout programKerja, profilCaleg, platform, penghargaan, visiMisi;

    private FloatingActionButton createPendukung;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private RequestQueue rq;
    private List<SliderUtils> sliderImg;

    String request_url = "http://156.67.221.225/trencenter/voting/android/debug.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home_relawan, container, false);

        rootView.setAlpha(0f);
        rootView.setVisibility(View.GONE);

        hari = (TextView) rootView.findViewById(R.id.hari);
        jam = (TextView) rootView.findViewById(R.id.jam);
        menit = (TextView) rootView.findViewById(R.id.menit);
        detik = (TextView) rootView.findViewById(R.id.detik);

        programKerja = (RelativeLayout) rootView.findViewById(R.id.programKerja);
        profilCaleg = (RelativeLayout) rootView.findViewById(R.id.profil_caleg);
        platform = (RelativeLayout) rootView.findViewById(R.id.platform);
        penghargaan = (RelativeLayout) rootView.findViewById(R.id.penghargaan);
        visiMisi = (RelativeLayout) rootView.findViewById(R.id.visi_misi);

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        rq = Volley.newRequestQueue(getContext());
        sliderImg = new ArrayList<>();

        sendRequest();

        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(), 2000, 4000);

        programKerja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProgramKerja.class));
            }
        });

        profilCaleg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailCaleg.class));
            }
        });

        penghargaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailPenghargaan.class));
            }
        });

        platform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailPlatform.class));
            }
        });

        visiMisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DetailVisiMisi.class));
            }
        });

        createPendukung = (FloatingActionButton) rootView.findViewById(R.id.createPendukung);
        createPendukung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), InputPendukung.class));
            }
        });

        countDown();

        rootView.setVisibility(View.VISIBLE);
        rootView.animate()
                .alpha(1f)
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(null);

        return rootView;
    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            if(getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    } else if(viewPager.getCurrentItem() == 2){
                        viewPager.setCurrentItem(3);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public void sendRequest(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(request_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                String url = "http://156.67.221.225/trencenter/voting/dashboard/save/foto_berita/";
                List<String> headlineList = new ArrayList<>();

                for (int i=0; i<response.length(); i++){
                    SliderUtils sliderUtils = new SliderUtils();
                    try {
                        JSONArray array = response.getJSONArray(i);
                        String image = url + array.getString(0);
                        headlineList.add(array.getString(1));

                        sliderUtils.setSliderImageUrl(image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);
                }

                adapter = new ViewPagerAdapter(sliderImg, headlineList, getContext());
                viewPager.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(jsonArrayRequest);
    }

    private void countDown(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String dateStart = format.format(c);
        String dateEnd = "04/17/2019 06:00:00";

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        hari.setText("" + diffDays);
        jam.setText("" + diffHours);
        menit.setText("" + diffMinutes);
        detik.setText("" + diffSeconds);

        new CountDownTimer(diffSeconds, 1000){
            @Override
            public void onTick(long l) {
                detik.setText("" + l / 1000);
            }

            @Override
            public void onFinish() {
                countDown();
            }
        }.start();

    }

}
