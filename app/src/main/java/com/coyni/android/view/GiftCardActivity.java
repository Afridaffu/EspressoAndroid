package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.adapters.GiftCardsAdapter;
import com.coyni.android.model.giftcard.Brand;
import com.coyni.android.model.giftcard.BrandRequirements;
import com.coyni.android.model.giftcard.GiftCard;
import com.coyni.android.model.giftcard.Items;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;

import java.util.ArrayList;
import java.util.List;

public class GiftCardActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    BuyViewModel buyViewModel;
    ProgressDialog dialog;
    EditText etSearch;
    GiftCardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive( GiftCardActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive( GiftCardActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive( GiftCardActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#0F1827"));
            }
            objMyApplication = (MyApplication) getApplicationContext();
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            etSearch = (EditText) findViewById(R.id.etSearch);
            if (Utils.checkInternet(GiftCardActivity.this)) {
                dialog = new ProgressDialog(GiftCardActivity.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                buyViewModel.meGiftCards();
            } else {
                Utils.displayAlert(getString(R.string.internet), GiftCardActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyViewModel.getGiftCardMutableLiveData().observe(this, new Observer<GiftCard>() {
            @Override
            public void onChanged(GiftCard giftCard) {
                try {
                    if (giftCard != null) {
                        if (giftCard.getBrands() != null && giftCard.getBrands().size() > 0) {
                            prepareBrands(giftCard.getBrands());
                        }
                    } else {
                        dialog.dismiss();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private List<Brand> prepareBrands(List<Brand> listBrands) {
        List<Brand> brands = new ArrayList<>();
        GridView gvAllBrands = (GridView) findViewById(R.id.gvAllBrands);
        TextView tvNoRecords = (TextView) findViewById(R.id.tvNoRecords);
        try {
            List<Items> listItems = new ArrayList<>();
            if (listBrands != null && listBrands.size() > 0) {
                Brand objBrand;
                for (int i = 0; i < listBrands.size(); i++) {
                    objBrand = new Brand();
                    listItems = new ArrayList<>();
                    if (listBrands.get(i).getItems() != null && listBrands.get(i).getItems().size() > 0) {
                        for (int j = 0; j < listBrands.get(i).getItems().size(); j++) {
                            Items items = listBrands.get(i).getItems().get(j);
                            if (items != null && items.getStatus().toLowerCase().equals("active") && items.getCurrencyCode().toUpperCase().equals("USD")) {
                                listItems.add(items);
                            }
                        }
                    }
                    if (listItems != null && listItems.size() > 0) {
                        BrandRequirements obj = new BrandRequirements();
                        objBrand.setBrandKey(listBrands.get(i).getBrandKey());
                        objBrand.setBrandName(listBrands.get(i).getBrandName());
                        objBrand.setDisclaimer(listBrands.get(i).getDisclaimer());
                        objBrand.setDescription(listBrands.get(i).getDescription());
                        objBrand.setShortDescription(listBrands.get(i).getShortDescription());
                        objBrand.setCreatedDate(listBrands.get(i).getCreatedDate());
                        objBrand.setLastUpdateDate(listBrands.get(i).getLastUpdateDate());
                        obj.setDisplayInstructions(listBrands.get(i).getBrandRequirements().getDisplayInstructions());
                        obj.setTermsAndConditionsInstructions(listBrands.get(i).getBrandRequirements().getTermsAndConditionsInstructions());
                        obj.setDisclaimerInstructions(listBrands.get(i).getBrandRequirements().getDisclaimerInstructions());
                        obj.setAlwaysShowDisclaimer(listBrands.get(i).getBrandRequirements().getAlwaysShowDisclaimer());
                        objBrand.setBrandRequirements(obj);
                        objBrand.setImageUrls(listBrands.get(i).getImageUrls());
                        objBrand.setStatus(listBrands.get(i).getStatus());
                        objBrand.setItems(listItems);
                        brands.add(objBrand);
                    }
                }

                if (brands != null && brands.size() > 0) {
                    adapter = new GiftCardsAdapter(GiftCardActivity.this, brands);
                    gvAllBrands.setAdapter(adapter);

                    etSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try {
                                String search_key = s.toString();
                                List<Brand> filterList = new ArrayList<>();
                                int sIndex = 0, wIndex = -1;
                                if (brands != null && brands.size() > 0) {
                                    for (int i = 0; i < brands.size(); i++) {
                                        sIndex = brands.get(i).getBrandName().toLowerCase().indexOf(search_key.toLowerCase());
                                        if (sIndex == 0) {
                                            filterList.add(brands.get(i));
                                        }
                                    }
                                    if (filterList != null && filterList.size() > 0) {
                                        adapter = new GiftCardsAdapter(GiftCardActivity.this, filterList);
                                        gvAllBrands.invalidateViews();
                                        gvAllBrands.setAdapter(adapter);
                                        gvAllBrands.setVisibility(View.VISIBLE);
                                        tvNoRecords.setVisibility(View.GONE);
                                    } else {
                                        tvNoRecords.setVisibility(View.VISIBLE);
                                        gvAllBrands.setVisibility(View.GONE);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }
            }
            dialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return brands;
    }

}