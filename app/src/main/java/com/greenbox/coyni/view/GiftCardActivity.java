package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.GiftCardsAdapter;
import com.greenbox.coyni.adapters.GiftCardsRecyclerAdapter;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.utils.ExpandableHeightGridView;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.AccountLimitsViewModel;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;

public class GiftCardActivity extends AppCompatActivity {

    ExpandableHeightRecyclerView brandsGV;
    LinearLayout brandsLL, gcBackbtn;
    EditText searchET;
    GiftCardsViewModel giftCardsViewModel;
    GiftCardsRecyclerAdapter giftCardsAdapter;
    TextView noBrandsTV;
    public static GiftCardActivity giftCardActivity;

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
        giftCardActivity = this;
        brandsGV = findViewById(R.id.brandsGV);
        brandsLL = findViewById(R.id.brandsLL);
        gcBackbtn = findViewById(R.id.gcBackbtn);
        searchET = findViewById(R.id.searchET);
        noBrandsTV = findViewById(R.id.noBrandsTV);


        giftCardsViewModel = new ViewModelProvider(this).get(GiftCardsViewModel.class);
        giftCardsViewModel.getGiftCards();

        gcBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                                brandsGV.setExpanded(true);
                                GridLayoutManager layoutManager=new GridLayoutManager(GiftCardActivity.this,2);
                                giftCardsAdapter = new GiftCardsRecyclerAdapter(GiftCardActivity.this, brandsResponse.getData().getBrands());
                                brandsGV.setLayoutManager(layoutManager);
                                brandsGV.setAdapter(giftCardsAdapter);

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