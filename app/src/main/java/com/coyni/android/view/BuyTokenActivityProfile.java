package com.coyni.android.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.coyni.android.adapters.ViewPagerCardsAdapter;
import com.coyni.android.model.cards.CardDeleteResponse;
import com.coyni.android.model.cards.Cards;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.viewmodel.BuyViewModel;
import com.coyni.android.viewmodel.SendViewModel;

import java.util.ArrayList;
import java.util.List;

public class BuyTokenActivityProfile extends AppCompatActivity {
    MyApplication objMyApplication;
    SendViewModel sendViewModel;
    BuyViewModel buyViewModel;
    String strType = "";
    List<CardsDataItem> listAllCards = new ArrayList<>();
    TextInputLayout etlPay, etlGet;
    TextView tvDebitHead, title, tvExchange, tvHead, tvPFee, tvRemove, tvEdit;
    List<CardsDataItem> listSorted;
    static int position;
    Dialog popupDelete;
    ProgressDialog dialog;
    CardView cvAdd;
    ViewPager mViewPager;
    ViewPagerCardsAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token_profile);
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
            if (mViewPager != null && mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() > 0) {
                mViewPager.getAdapter().notifyDataSetChanged();
            }
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(BuyTokenActivityProfile.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(BuyTokenActivityProfile.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(BuyTokenActivityProfile.this, this, false);
    }

    @Override
    public void onBackPressed() {
        try {
            objMyApplication.setFromWhichFragment("fromProfileDebitCreditCards");
            Intent i = new Intent(BuyTokenActivityProfile.this, MainActivity.class);
            i.putExtra("fromBuyTokenProfile", "fromBuyTokenProfile");
            startActivity(i);
            //super.onBackPressed();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        try {
            if (requestCode == 1) {
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }


    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(BuyTokenActivityProfile.this);
            objMyApplication = (MyApplication) getApplicationContext();
            title = (TextView) toolbar.findViewById(R.id.title);
            tvDebitHead = (TextView) findViewById(R.id.tvDebitHead);
            tvExchange = (TextView) findViewById(R.id.tvExchange);
            tvHead = (TextView) findViewById(R.id.tvHead);

            etlPay = (TextInputLayout) findViewById(R.id.etlPay);
            etlGet = (TextInputLayout) findViewById(R.id.etlGet);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            LinearLayout layoutCard = (LinearLayout) findViewById(R.id.layoutCard);
            LinearLayout layoutBank = (LinearLayout) findViewById(R.id.layoutBank);
            cvAdd = (CardView) findViewById(R.id.cvAdd);
            CardView cvAddBank = (CardView) findViewById(R.id.cvAddBank);
            ImageView imgLogo = (ImageView) findViewById(R.id.imgLogo);
            ImageView imgInfo = (ImageView) findViewById(R.id.imgInfo);
            tvRemove = (TextView) findViewById(R.id.tvRemove);
            tvEdit = (TextView) findViewById(R.id.tvEdit);

            layoutCard.setVisibility(View.VISIBLE);
            layoutBank.setVisibility(View.GONE);
            strType = "card";
            if (Utils.checkInternet(BuyTokenActivityProfile.this)) {
                buyViewModel.meCards();
            } else {
                Toast.makeText(BuyTokenActivityProfile.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
            }
            if (objMyApplication.getListCards() != null && objMyApplication.getListCards().size() > 0) {
                bindCards(objMyApplication.getListCards());
            }

            cvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BuyTokenActivityProfile.this, AddCardActivity.class);
                    i.putExtra("from", "add");
                    i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                    i.putExtra("type", getIntent().getStringExtra("type"));
                    i.putExtra("fromProfilePaymentMethods", "fromProfilePaymentMethods");
                    startActivity(i);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showCardDeletePopup(CardsDataItem objData) {
        try {
            ImageView imgClose;
            TextView tvHead, tvMessage, tvCancel;
            CardView cvRemove;
            popupDelete = new Dialog(BuyTokenActivityProfile.this, R.style.DialogTheme);
            popupDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupDelete.setContentView(R.layout.deletepopup);
            Window window = popupDelete.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupDelete.getWindow().setAttributes(lp);
            popupDelete.show();
            imgClose = (ImageView) popupDelete.findViewById(R.id.imgClose);
            tvHead = (TextView) popupDelete.findViewById(R.id.tvHead);
            tvMessage = (TextView) popupDelete.findViewById(R.id.tvMessage);
            tvCancel = (TextView) popupDelete.findViewById(R.id.tvCancel);
            cvRemove = (CardView) popupDelete.findViewById(R.id.cvRemove);
            tvHead.setText("Remove " + Utils.capitalize(objData.getCardType()) + " Card");
            tvMessage.setText("Are you sure want to remove this " + Utils.capitalize(objData.getCardType()) + " Card");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyViewModel.deleteCards(String.valueOf(objData.getId()));
                    dialog = new ProgressDialog(BuyTokenActivityProfile.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {

        buyViewModel.getCardsMutableLiveData().observe(this, new Observer<Cards>() {
            @Override
            public void onChanged(Cards cards) {
                if (cards != null) {
                    listAllCards = cards.getData().getItems();
                    objMyApplication.setListCards(listAllCards);
                    bindCards(cards.getData().getItems());
                }
            }
        });

        buyViewModel.getDelCardResponseMutableLiveData().observe(this, new Observer<CardDeleteResponse>() {
            @Override
            public void onChanged(CardDeleteResponse cardDeleteResponse) {
                dialog.dismiss();
                popupDelete.dismiss();
                if (cardDeleteResponse != null) {
//                    Toast.makeText(BuyTokenActivityProfile.this, cardDeleteResponse.getData().getMessage(), Toast.LENGTH_LONG).show();
//                    if (Utils.checkInternet(BuyTokenActivityProfile.this)) {
//                        buyViewModel.meCards();
//                    } else {
//                        Toast.makeText(BuyTokenActivityProfile.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
//                    }

                    Context context = new ContextThemeWrapper(BuyTokenActivityProfile.this, R.style.Theme_QuickCard);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                    builder.setTitle(R.string.app_name);
                    builder.setMessage(cardDeleteResponse.getData().getMessage());
                    AlertDialog dialog = builder.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            if (Utils.checkInternet(BuyTokenActivityProfile.this)) {
                                buyViewModel.meCards();
                            } else {
                                Toast.makeText(BuyTokenActivityProfile.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, Integer.parseInt(context.getString(R.string.closealert)));
                }
            }
        });
    }

    private void bindCards(List<CardsDataItem> listCards) {
        try {
            if (listCards != null && listCards.size() > 0) {
                mViewPager = (ViewPager) findViewById(R.id.rvCards);
                mViewPagerAdapter = new ViewPagerCardsAdapter(sortCards(listCards), BuyTokenActivityProfile.this, "buy");
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(mViewPagerAdapter.getCount() / 2, true);


                tvRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCardDeletePopup(listSorted.get(mViewPager.getCurrentItem()));
                    }
                });
                tvEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            position = mViewPager.getCurrentItem();
                            if (listSorted != null && listSorted.size() > 0) {
                                CardsDataItem item = listSorted.get(BuyTokenActivityProfile.position);
                                objMyApplication.setSelectedCard(item);
                                Intent i = new Intent(BuyTokenActivityProfile.this, AddEditCardDetailsActivity.class);
                                i.putExtra("subtype", getIntent().getStringExtra("subtype"));
                                i.putExtra("type", getIntent().getStringExtra("type"));
                                i.putExtra("fromProfilePaymentMethods", "fromProfilePaymentMethods");
                                startActivity(i);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                });
            } else {
                Intent i = new Intent(BuyTokenActivityProfile.this, MainActivity.class);
                i.putExtra("fromBuyTokenProfile", "fromBuyTokenProfile");
                startActivity(i);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void cardType(String type, int position) {
        try {
            CardsDataItem item = listSorted.get(position);
            objMyApplication.setSelectedCard(item);
            BuyTokenActivityProfile.position = position;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<CardsDataItem> sortCards(List<CardsDataItem> lstCards) {
        listSorted = new ArrayList<>();
        try {
            int position = -1;
            if (lstCards != null && lstCards.size() > 0) {
                for (int i = 0; i < lstCards.size(); i++) {
                    if (lstCards.get(i).getDefaultForAllWithDrawals() != null && lstCards.get(i).getDefaultForAllWithDrawals()) {
                        listSorted.add(lstCards.get(i));
                        position = i;
                        break;
                    }
                }
                for (int i = 0; i < lstCards.size(); i++) {
                    if (i != position) {
                        listSorted.add(lstCards.get(i));
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listSorted;
    }
}
