package com.greenbox.coyni.view;

import android.graphics.Rect;
import android.location.GnssMeasurementsEvent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;

import java.util.ArrayList;
import java.util.List;

public class GiftCardActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {

    ExpandableHeightRecyclerView brandsGV;
    LinearLayout brandsLL, gcBackbtn, clearTextLL;
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
        setKeyboardVisibilityListener(GiftCardActivity.this);
        brandsGV = findViewById(R.id.brandsGV);
        brandsLL = findViewById(R.id.brandsLL);
        gcBackbtn = findViewById(R.id.gcBackbtn);
        searchET = findViewById(R.id.searchET);
        noBrandsTV = findViewById(R.id.noBrandsTV);
        clearTextLL = findViewById(R.id.clearTextLL);

        searchET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(60)});
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
                if (charSequence.toString().length() > 0) {
                    clearTextLL.setVisibility(View.VISIBLE);
                } else {
                    clearTextLL.setVisibility(View.GONE);
                }
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
                    GridLayoutManager layoutManager = new GridLayoutManager(GiftCardActivity.this, 2);
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

        clearTextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchET.setText("");
                searchET.clearFocus();
                Utils.hideKeypad(GiftCardActivity.this);
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
                                GridLayoutManager layoutManager = new GridLayoutManager(GiftCardActivity.this, 2);
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

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Utils.isKeyboardVisible)
            Utils.hideKeypad(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Utils.isKeyboardVisible)
            Utils.hideKeypad(this);
    }
}