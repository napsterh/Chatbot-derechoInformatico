package com.example.os.asistentevirtual;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ImgActivity extends AppCompatActivity {

    String img;
    ImageView tvImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        tvImg = findViewById(R.id.tvImg);

        if (getIntent() != null) {
            img = getIntent().getStringExtra("img");
            int id = getResources().getIdentifier(img, "drawable",getPackageName());

            tvImg.setImageResource(id);
        }
    }
}
