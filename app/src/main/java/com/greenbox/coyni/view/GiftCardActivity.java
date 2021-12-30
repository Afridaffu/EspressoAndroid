package com.greenbox.coyni.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.GiftCardsRecyclerAdapter;
import com.greenbox.coyni.adapters.TransactionListPostedNewAdapter;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;

import java.util.ArrayList;
import java.util.List;

public class GiftCardActivity extends AppCompatActivity {

    ExpandableHeightRecyclerView brandsGV;
    LinearLayout brandsLL, gcBackbtn;
    EditText searchET;
    GiftCardsViewModel giftCardsViewModel;
    GiftCardsRecyclerAdapter giftCardsAdapter;
    TextView noBrandsTV;
    public static GiftCardActivity giftCardActivity;
    List<Brand> globalBrands = new ArrayList<>();

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

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search_key = charSequence.toString();
                List<Brand> filterList = new ArrayList<>();
                int pindex = 0, poindex = 0;
                if (globalBrands.size() > 0) {
                    for (int iteration = 0; iteration < globalBrands.size(); iteration++) {
                        pindex = globalBrands.get(iteration).getBrandName().toLowerCase().indexOf(search_key.toLowerCase());
                        if (pindex == 0) {
                            filterList.add(globalBrands.get(iteration));
                        }
                    }
                }

                if (filterList.size() > 0) {
                    brandsLL.setVisibility(View.VISIBLE);
                    noBrandsTV.setVisibility(View.GONE);
                    brandsGV.setExpanded(true);
                    GridLayoutManager layoutManager=new GridLayoutManager(GiftCardActivity.this,2);
                    giftCardsAdapter = new GiftCardsRecyclerAdapter(GiftCardActivity.this, filterList);
                    brandsGV.setLayoutManager(layoutManager);
                    brandsGV.setAdapter(giftCardsAdapter);

                } else {
                    brandsLL.setVisibility(View.GONE);
                    noBrandsTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                                globalBrands = brandsResponse.getData().getBrands();
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