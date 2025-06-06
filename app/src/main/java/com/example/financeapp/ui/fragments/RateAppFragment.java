package com.example.financeapp.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.financeapp.R;

public class RateAppFragment extends Fragment {

    private RatingBar ratingBar;
    private Button btnSubmit;
    private ImageView thankYouGif;
    private TextView questionText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rate_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ratingBar = view.findViewById(R.id.ratingBar);
        btnSubmit = view.findViewById(R.id.btn_submit_rating);
        thankYouGif = view.findViewById(R.id.thank_you_gif);
        questionText = view.findViewById(R.id.text_question);

        btnSubmit.setOnClickListener(v -> {
            float rating = ratingBar.getRating();

            if (rating < 1.0f || rating > 5.0f) {
                Toast.makeText(requireContext(), "Wybierz ocenę!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Schowaj elementy i pokaż GIF
            ratingBar.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            questionText.setVisibility(View.GONE);
            thankYouGif.setVisibility(View.VISIBLE);


            int gifResId = 0;
            if (rating == 1.0f) {
                gifResId = R.drawable.fuck;
            } else if (rating == 2.0f) {
                gifResId = R.drawable.najman;
            } else if (rating == 3.0f) {
                gifResId = R.drawable.jasper;
            } else if (rating == 4.0f) {
                gifResId = R.drawable.jasper;
            } else if (rating == 5.0f) {
                gifResId = R.drawable.najman2;
            }

            Glide.with(requireContext())
                    .asGif()
                    .load(gifResId)
                    .into(thankYouGif);

            new Handler().postDelayed(() -> {
                Navigation.findNavController(view).navigate(R.id.navigation_profile);
            }, 2000);
        });
    }
}