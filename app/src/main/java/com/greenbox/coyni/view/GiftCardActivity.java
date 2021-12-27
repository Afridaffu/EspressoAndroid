package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.GiftCardsAdapter;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.utils.ExpandableHeightGridView;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;

public class GiftCardActivity extends AppCompatActivity {

    ExpandableHeightGridView brandsGV;
    LinearLayout brandsLL, gcBackbtn;
    EditText searchET;
    GiftCardsViewModel giftCardsViewModel;
    GiftCardsAdapter giftCardsAdapter;
    TextView noBrandsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_gift_card);
        initilization();
        initObservers();
    }

    public void initilization() {
        brandsGV = findViewById(R.id.brandsGV);
        brandsLL = findViewById(R.id.brandsLL);
        gcBackbtn = findViewById(R.id.gcBackbtn);
        searchET = findViewById(R.id.searchET);
        noBrandsTV = findViewById(R.id.noBrandsTV);

        giftCardsViewModel = new ViewModelProvider(this).get(GiftCardsViewModel.class);
        giftCardsViewModel.getGiftCards();
    }

    public void initObservers() {
        try {
            giftCardsViewModel.getGiftCardsMutableLiveData().observe(this, new Observer<BrandsResponse>() {
                @Override
                public void onChanged(BrandsResponse brandsResponse) {
                    if (brandsResponse != null) {
                        if (brandsResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                            if (brandsResponse.getData().getBrands().size() > 0) {
                                brandsLL.setVisibility(View.VISIBLE);
                                noBrandsTV.setVisibility(View.GONE);
                                giftCardsAdapter = new GiftCardsAdapter(GiftCardActivity.this, brandsResponse.getData().getBrands());
                                brandsGV.setAdapter(giftCardsAdapter);
                                brandsGV.setExpanded(true);
                            } else {
                                brandsLL.setVisibility(View.GONE);
                                noBrandsTV.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Utils.displayAlert(brandsResponse.getError().getErrorDescription(), GiftCardActivity.this, "", brandsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}