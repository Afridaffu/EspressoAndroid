package com.greenbox.coyni.view;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.GiftCardsAdapter;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;

public class GiftCardDetails extends AppCompatActivity {

    TextInputEditText firstNameET, lastNameET, emailET;
    TextInputLayout firstNameTIL, lastNameTIL, emailTIL;
    public LinearLayout emailErrorLL, firstNameErrorLL, lastNameErrorLL,giftCardDetailsLL;
    public TextView emailErrorTV, firstNameErrorTV, lastNameErrorTV, brandNameTV, brandDescTV, viewAllTV;
    ImageView brandIV;
    boolean isView = false;
    GiftCardsViewModel giftCardsViewModel;
    BrandsResponse brandsResponseObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card_details);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {

        try {
            firstNameErrorLL = findViewById(R.id.firstNameErrorLL);
            lastNameErrorLL = findViewById(R.id.lastNameErrorLL);
            emailErrorLL = findViewById(R.id.emailErrorLL);

            firstNameErrorTV = findViewById(R.id.firstNameErrorTV);
            lastNameErrorTV = findViewById(R.id.lastNameErrorTV);
            emailErrorTV = findViewById(R.id.emailErrorTV);

            firstNameET = findViewById(R.id.firstNameET);
            firstNameTIL = findViewById(R.id.firstNameTIL);
            lastNameET = findViewById(R.id.lastNameET);
            lastNameTIL = findViewById(R.id.lastNameTIL);
            emailET = findViewById(R.id.emailET);
            emailTIL = findViewById(R.id.emailTIL);

            brandIV = findViewById(R.id.brandIV);
            brandNameTV = findViewById(R.id.brandNameTV);
            brandDescTV = findViewById(R.id.brandDescTV);
            viewAllTV = findViewById(R.id.viewAllTV);
            giftCardDetailsLL = findViewById(R.id.giftCardDetailsLL);

            giftCardsViewModel = new ViewModelProvider(this).get(GiftCardsViewModel.class);
            giftCardsViewModel.getGiftCardDetails(getIntent().getStringExtra("BRAND_KEY"));
            viewAllTV.setPaintFlags(viewAllTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            viewAllTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (brandDescTV.getMaxLines() == 3) {
                        brandDescTV.setMaxLines(Integer.MAX_VALUE);
                        viewAllTV.setText(getResources().getString(R.string.view_less));
                    } else {
                        brandDescTV.setMaxLines(3);
                        viewAllTV.setText(getResources().getString(R.string.view_all));
                    }
                }
            });

            giftCardDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {
        try {
            giftCardsViewModel.getGiftCardDetailsMutableLiveData().observe(this, new Observer<BrandsResponse>() {
                @Override
                public void onChanged(BrandsResponse brandsResponse) {
                    if (brandsResponse != null) {
                        if (brandsResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                            brandsResponseObj = brandsResponse;
                            brandNameTV.setText(brandsResponse.getData().getBrands().get(0).getBrandName());
                            brandDescTV.setText(Html.fromHtml(brandsResponse.getData().getBrands().get(0).getDescription()));
                            brandDescTV.setTextColor(getResources().getColor(R.color.primary_black));
                            Glide.with(GiftCardDetails.this).load(brandsResponse.getData().getBrands().get(0).getImageUrls().get_1200w326ppi().trim()).into(brandIV);
                        } else {
                            Utils.displayAlert(brandsResponse.getError().getErrorDescription(), GiftCardDetails.this, "", brandsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}