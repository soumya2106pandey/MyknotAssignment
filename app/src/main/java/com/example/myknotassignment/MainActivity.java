package com.example.myknotassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout rootLayout;
    ProgressBar pb;
    Button refreshButton;
    final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        postData();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshButton.setEnabled(false);
                refreshButton.animate().scaleX(0).scaleY(0).setInterpolator(interpolator).setDuration(100).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.VISIBLE);
                        postData();
                    }
                },100);

            }
        });
    }

    void init(){
        rootLayout = findViewById(R.id.rootLayout);
        pb = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
    }

    void postData() {
        String url = "https://backend-test-zypher.herokuapp.com/testData";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,null,
                response -> {
                    try {
                        CustomDialog postDataDialog = new CustomDialog(MainActivity.this, rootLayout);
                        postDataDialog.builder();
                        postDataDialog.setData(response.getString("title"), response.getString("imageURL"), response.getString("success_url"));
                        pb.setVisibility(View.GONE);
                        postDataDialog.show();
                        refreshButton.setEnabled(true);
                        refreshButton.setScaleX(1);
                        refreshButton.setScaleY(1);
                        refreshButton.setVisibility(View.VISIBLE);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Snackbar.make(rootLayout,"Error fetching data",Snackbar.LENGTH_SHORT).show()
        );
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}