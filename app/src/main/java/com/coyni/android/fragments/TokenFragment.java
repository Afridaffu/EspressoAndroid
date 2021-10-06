package com.coyni.android.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coyni.android.listener.OnLoadMoreListener;
import com.coyni.android.listener.RecyclerViewLoadMoreScroll;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.notification.Notifications;
import com.coyni.android.model.receiverequests.ReceiveRequests;
import com.coyni.android.model.transactions.TokenTransactionsData;
import com.coyni.android.model.transactions.TokenTransactionsItem;
import com.coyni.android.view.BuyTokenActivity;
import com.coyni.android.view.LoginActivity;
import com.coyni.android.viewmodel.NotificationsViewModel;
import com.coyni.android.viewmodel.PayViewModel;
import com.coyni.android.adapters.ColumnSelectionAdapter;
import com.coyni.android.adapters.DateSelectionAdapter;
import com.coyni.android.adapters.StatusAdapter;
import com.coyni.android.adapters.TokenTransactionsAdapter;
import com.coyni.android.adapters.TypesAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.States;
import com.coyni.android.model.Status;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.export.ExportColumns;
import com.coyni.android.model.export.ExportColumnsData;
import com.coyni.android.model.export.ExportRequest;
import com.coyni.android.model.export.ExportResponse;
import com.coyni.android.model.export.FilterColumns;
import com.coyni.android.model.preferences.Preferences;
import com.coyni.android.model.transactions.TokenTransactions;
import com.coyni.android.model.user.User;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.usertracker.UserTrackerData;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.model.wallet.WalletResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.R;
import com.coyni.android.viewmodel.BuyViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenFragment extends Fragment implements View.OnClickListener, TextWatcher {
    View view;
    static Context context;
    DashboardViewModel dashboardViewModel;
    NotificationsViewModel notificationsViewModel;
    BuyViewModel buyViewModel;
    PayViewModel payViewModel;
    ProgressDialog dialog;
    TextView tvName, tvUserInfo, tvBalance, tvClear, tvFromDate, tvToDate;
    MyApplication objMyApplication;
    CardView cvProfile, cvFilter, cvExport, cvBuyToken, cvProfilePic;
    Dialog popupFilter, popupExport, popupNoData, popupExportMsg;
    TextInputEditText etTransactionId;
    RelativeLayout lyFromDate, lyToDate, lyCuloumns, lyCustDate, layoutDateHead, lyEFromDate, lyEToDate;
    EditText etFromAmt, etToAmt;
    RecyclerView rvStatus, rvTypes, rvColumns, rvDateRange;
    ImageView imgFClose, imgEClose, imgDateArrow, imgColArrow, imgPaste, imgTypeArrow, imgStatusArrow, imgProfile;
    int mYear, mMonth, mDay;
    String strFrom = "", strTo = "", strEFrom = "", strETo = "", strTransId = "";
    StatusAdapter statusAdapter;
    TypesAdapter typesAdapter;
    DateSelectionAdapter dateAdapter;
    ColumnSelectionAdapter columnsAdapter;
    Map<String, String> objMap = new HashMap<>();
    LinearLayout layoutDone, lyMainColumns, layoutDates, layoutExport, layoutTypeArrow, layoutStatusArrow;
    TextView tvDefault, tvCustom, tvColumnHead, tvEFromDate, tvEToDate, tvEClear;
    Switch switchCustom;
    ScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean isFilters = false, isRefresh = false, isNoData = false, isAPICalled = false;
    RecyclerView rvTransactions;
    View viewDisableLayout;
    FilterColumns objFilters;
    int totalItemCount, currentPage = 0, total = 0;
    ProgressBar pbLoader;
    //    NestedScrollView nvScroll;
    List<TokenTransactionsItem> items = new ArrayList<>();
    private RecyclerViewLoadMoreScroll scrollListener;
    TokenTransactionsAdapter tokenTransactionsAdapter;

    public TokenFragment() {
    }

    public static TokenFragment newInstance(Context cxt) {
        TokenFragment fragment = new TokenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cvFilter:
                showFiltersPopUp();
                break;
            case R.id.cvExport:
                showExportsPopUp();
                break;
            case R.id.cvBuyToken:
                loadBuyTokens();
                break;
            case R.id.imgFClose:
                popupFilter.dismiss();
                break;
            case R.id.imgEClose:
                try {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.removeRule(RelativeLayout.ABOVE);
                    scrollView.setLayoutParams(params);
                    imgColArrow.setImageResource(R.drawable.ic_down_arrow);
                    popupExport.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.lyFromDate:
                setFromDate(tvFromDate);
                break;
            case R.id.lyToDate:
                setToDate(tvToDate);
                break;
            case R.id.tvClear:
                objMyApplication.setStatusId(-1);
                objMyApplication.setTypeId(-1);
                statusAdapter.notifyDataSetChanged();
                typesAdapter.notifyDataSetChanged();
                clearData();
                break;
            case R.id.layoutDone:
                prepareFilters();
                break;
            case R.id.imgPaste:
                if (!getCopiedText().trim().equals("")) {
                    if (!getCopiedText().equals("") && getCopiedText().length() > 30) {
                        etTransactionId.setText(getCopiedText().substring(0, 30) + "...");
                    } else {
                        etTransactionId.setText(getCopiedText());
                    }
                    strTransId = getCopiedText();
                }
                break;
            case R.id.lyCuloumns:
                openColumnOptions();
                break;
            case R.id.tvDefault:
                loadColumnsList(tvDefault.getText().toString());
                break;
            case R.id.tvCustom:
                loadColumnsList(tvCustom.getText().toString());
                break;
            case R.id.layoutDateHead:
                if (!switchCustom.isChecked()) {
                    if (rvDateRange.getVisibility() == View.VISIBLE) {
                        rvDateRange.setVisibility(View.GONE);
                        imgDateArrow.setImageResource(R.drawable.ic_down_arrow);
                    } else {
                        rvDateRange.setVisibility(View.VISIBLE);
                        imgDateArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
                break;
            case R.id.tvEClear:
                switchCustom.setChecked(false);
                clearExportData();
                break;
            case R.id.layoutExport:
                prepareExportJSONObject();
                break;
            case R.id.lyEFromDate:
                setFromDate(tvEFromDate);
                break;
            case R.id.lyEToDate:
                setToDate(tvEToDate);
                break;
            case R.id.layoutTypeArrow:
                if (rvTypes.getVisibility() == View.VISIBLE) {
                    rvTypes.setVisibility(View.GONE);
                    imgTypeArrow.setImageResource(R.drawable.ic_down_arrow_2);
                } else {
                    rvTypes.setVisibility(View.VISIBLE);
                    imgTypeArrow.setImageResource(R.drawable.ic_up_arrow);
                }
                break;
            case R.id.layoutStatusArrow:
                if (rvStatus.getVisibility() == View.VISIBLE) {
                    rvStatus.setVisibility(View.GONE);
                    imgStatusArrow.setImageResource(R.drawable.ic_down_arrow_2);
                } else {
                    rvStatus.setVisibility(View.VISIBLE);
                    imgStatusArrow.setImageResource(R.drawable.ic_up_arrow);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == etFromAmt.getEditableText()) {
            if (s.toString().equals(".")) {
                etFromAmt.setText("");
            }
        } else if (s == etToAmt.getEditableText()) {
            if (s.toString().equals(".")) {
                etToAmt.setText("");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        try {
//            LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver),
//                    new IntentFilter(PushNotificationService.REQUEST_ACCEPT)
//            );
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            if (objMyApplication.getFromWhichFragment().equals("transdetails")) {
                objMyApplication.setFromWhichFragment("");
                if (objMap != null && objMap.size() == 0) {
                    objMap.put("walletCategory", Utils.walletCategory);
                }
            } else {
                objMap = new HashMap<>();
                objMap.put("walletCategory", Utils.walletCategory);
            }
            if (Utils.checkInternet(context)) {
                dashboardViewModel.meTransactions(objMap);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tokenlayout, container, false);
        try {
            objMyApplication = (MyApplication) context.getApplicationContext();
            objMyApplication.setFromWhichFragment("fromSignetToken");
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.VISIBLE);
            ((MainActivity) context).enableToken();
            tvName = view.findViewById(R.id.tvName);
            tvUserInfo = view.findViewById(R.id.tvUserInfo);
            tvBalance = view.findViewById(R.id.tvBalance);
            cvProfile = view.findViewById(R.id.cvProfile);
            cvFilter = view.findViewById(R.id.cvFilter);
            cvExport = view.findViewById(R.id.cvExport);
            cvBuyToken = view.findViewById(R.id.cvBuyToken);
            cvProfilePic = view.findViewById(R.id.cvProfilePic);
            imgProfile = view.findViewById(R.id.imgProfile);
            pbLoader = view.findViewById(R.id.pbLoader);
//            nvScroll = view.findViewById(R.id.nvScroll);
            swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
            popupFilter = new Dialog(context, R.style.DialogTheme);
            popupFilter.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupFilter.setContentView(R.layout.filterslayout);

            popupExport = new Dialog(context, R.style.DialogTheme);
            popupExport.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupExport.setContentView(R.layout.exportlayout);
            cvFilter.setOnClickListener(this);
            cvExport.setOnClickListener(this);
            cvBuyToken.setOnClickListener(this);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
//            if (Utils.checkInternet(context)) {
            if (Utils.checkInternet(context)) {
                if (!objMyApplication.getToken()) {
                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    objMap.put("walletCategory", Utils.walletCategory);
//                    objMap.put("pageSize", String.valueOf(Utils.pageSize));
//                    objMap.put("pageNo", String.valueOf(currentPage));
                    dashboardViewModel.meTransactions(objMap);
                    dashboardViewModel.meWallet();

                    if (objMyApplication.getStrUser().equals("")) {
                        dashboardViewModel.meProfile();
                    } else {
                        tvName.setText(Utils.capitalize(objMyApplication.getStrUser()));
                        loadProfile();
                    }
                    dashboardViewModel.mePreferences();
                } else {
                    objMyApplication.setToken(false);
                    if (objMyApplication.getTokenTransactions() != null) {
                        bindTransactions(objMyApplication.getTokenTransactions());
                    }
                    if (objMyApplication.getWalletResponse() != null) {
                        getBalance(objMyApplication.getWalletResponse());
                    }
                    if (!objMyApplication.getStrUser().equals("")) {
                        tvName.setText(Utils.capitalize(objMyApplication.getStrUser()));
                        loadProfile();
                    }
                }
                if (!objMyApplication.getTrackerComplete()) {
                    cvProfile.setVisibility(View.VISIBLE);
//                    dashboardViewModel.meTracker();
                } else {
                    cvProfile.setVisibility(View.GONE);
                }
                dashboardViewModel.meTracker();
            } else {
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
            initObservables();
            getStates();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (Utils.checkInternet(context)) {
                        isRefresh = true;
                        if (isNoData) {
                            isNoData = false;
                            currentPage = 0;
                            objMap = new HashMap<>();
                            objMap.put("walletCategory", Utils.walletCategory);
//                            objMap.put("pageSize", String.valueOf(Utils.pageSize));
//                            objMap.put("pageNo", String.valueOf(currentPage));
                        }
                        if (objMap != null && objMap.size() == 0) {
                            objMap = new HashMap<>();
                            objMap.put("walletCategory", Utils.walletCategory);
                        }
                        dashboardViewModel.meTransactions(objMap);
                        dashboardViewModel.meWallet();
                        dashboardViewModel.meProfile();
                        dashboardViewModel.meTracker();
                        dashboardViewModel.mePreferences();
                    } else {
                        Utils.displayAlert(getString(R.string.internet), getActivity());
                    }
                }
            });
            swipeRefreshLayout.setColorSchemeColors(context.getResources().getColor(R.color.status));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void initObservables() {
        dashboardViewModel.getTokenTransactionsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<TokenTransactions>() {
            @Override
            public void onChanged(TokenTransactions tokenTransactions) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
//                objMyApplication.setTokenTransactions(tokenTransactions);
                if (tokenTransactions != null) {
//                    if (currentPage == 0) {
//                        bindTransactions(tokenTransactions);
//                    } else {
//                        loadMore(tokenTransactions.getData().getItems());
//                    }
                    bindTransactions(tokenTransactions);
                }
//                new FetchData(getActivity()).execute();
            }
        });

        dashboardViewModel.getWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<WalletResponse>() {
            @Override
            public void onChanged(WalletResponse walletResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (walletResponse != null) {
                    objMyApplication.setWalletResponse(walletResponse);
                    getBalance(walletResponse);
                }
            }
        });

        dashboardViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
//                    tvHead.setText(Utils.capitalize("Welcome, " + user.getData().getFirstName() + " " + user.getData().getLastName()));
                    tvName.setText(Utils.capitalize(user.getData().getFirstName() + " " + user.getData().getLastName()));
                    tvUserInfo.setText(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());
                    objMyApplication.setStrUser(Utils.capitalize(user.getData().getFirstName() + " " + user.getData().getLastName()));
                    objMyApplication.setStrUserCode(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());

                    objMyApplication.setStrEmail(user.getData().getEmail());
                    objMyApplication.setStrPhoneNum(user.getData().getPhoneNumber());
                    objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                    objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                    objMyApplication.setStrState(user.getData().getState());
                    objMyApplication.setStrCity(user.getData().getCity());
                    objMyApplication.setStrCountry(user.getData().getCountry());
                    objMyApplication.setStrZipCode(user.getData().getZipCode());
                    objMyApplication.setIntUserId(user.getData().getId());
                    objMyApplication.setStrProfileImg(user.getData().getImage());
                    loadProfile();

                }
            }
        });

        dashboardViewModel.getUserTrackerMutableLiveData().observe(getViewLifecycleOwner(), new Observer<UserTracker>() {
            @Override
            public void onChanged(UserTracker userTracker) {
                if (userTracker != null) {
                    objMyApplication.setUserTracker(userTracker);
                    bindUserTracker(userTracker);
                }
                new FetchData(getActivity()).execute();
            }
        });

        dashboardViewModel.getExportColumnsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ExportColumns>() {
            @Override
            public void onChanged(ExportColumns exportColumns) {
                if (exportColumns != null) {
                    objMyApplication.setExportColumns(exportColumns);
                    bindColumns(exportColumns);
                }
            }
        });

        dashboardViewModel.getExportResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ExportResponse>() {
            @Override
            public void onChanged(ExportResponse exportResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (exportResponse != null && exportResponse.getData() && exportResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                        exportSuccess();
                        notificationsViewModel.meNotifications();
                    }
                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(getViewLifecycleOwner(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                    }
                }
            }
        });

        buyViewModel.getBanksMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                try {
                    if (banks != null) {
                        List<BanksDataItem> listBanks = new ArrayList<>();
                        List<BanksDataItem> listSignetBanks = new ArrayList<>();
                        List<BanksDataItem> listItems = banks.getData().getItems();
                        for (int i = 0; i < listItems.size(); i++) {
                            if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("bank") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                listBanks.add(banks.getData().getItems().get(i));
                            } else {
                                if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("signet") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                    listSignetBanks.add(banks.getData().getItems().get(i));
                                }
                            }
                        }
                        objMyApplication.setListBanks(listBanks);
                        objMyApplication.setSignetBanks(listSignetBanks);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dashboardViewModel.getPreferencesResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences user) {
                dialog.dismiss();
                if (user.getData().getTimeZone() == 0) {
                    objMyApplication.setStrPreference(context.getString(R.string.isPST));
                } else if (user.getData().getTimeZone() == 1) {
                    objMyApplication.setStrPreference(context.getString(R.string.isMST));
                } else if (user.getData().getTimeZone() == 2) {
                    objMyApplication.setStrPreference(context.getString(R.string.isCST));
                } else if (user.getData().getTimeZone() == 3) {
                    objMyApplication.setStrPreference(context.getString(R.string.isEST));
                } else if (user.getData().getTimeZone() == 4) {
                    objMyApplication.setStrPreference(context.getString(R.string.isHST));
                }
            }
        });

        notificationsViewModel.getNotificationsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (notifications != null && notifications.getStatus().toUpperCase().equals("SUCCESS")) {
                    objMyApplication.setNotificationsDataItems(notifications.getData().getItems());
                    if (notifications.getData().getItems() != null && notifications.getData().getItems().size() > 0) {
                        for (int i = 0; i < notifications.getData().getItems().size(); i++) {
                            if (!notifications.getData().getItems().get(i).getRead()) {
                                objMyApplication.setNotiAvailable(true);
                                break;
                            } else {
                                objMyApplication.setNotiAvailable(false);
                            }
                        }
                    } else {
                        objMyApplication.setNotiAvailable(false);
                    }
                    ((MainActivity) context).enableMenuIcon();
                }
            }
        });

        payViewModel.getReceiveRequestsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ReceiveRequests>() {
            @Override
            public void onChanged(ReceiveRequests receiveRequests) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (receiveRequests != null) {
                    if (receiveRequests.getStatus().toLowerCase().equals("success")) {
                        if (receiveRequests.getData().getItems() != null && receiveRequests.getData().getItems().size() > 0) {
                            String strStaus = "";
                            for (int i = 0; i < receiveRequests.getData().getItems().size(); i++) {
                                strStaus = Utils.capitalize(receiveRequests.getData().getItems().get(i).getStatus().replace(" ", ""));
                                if (strStaus.equals(Utils.requested) || strStaus.equals(Utils.remind)) {
                                    objMyApplication.setReqAvailable(true);
                                    break;
                                } else {
                                    objMyApplication.setReqAvailable(false);
                                }
                            }
                        } else {
                            objMyApplication.setReqAvailable(false);
                        }
                        ((MainActivity) context).enableMenuIcon();
                    }
                }
            }
        });

        buyViewModel.getSignOnMutableLiveData().observe(getActivity(), new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                dialog.dismiss();
                if (signOn != null) {
                    if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                        objMyApplication.setSignOnData(signOn.getData());
                        objMyApplication.setStrSignOnError("");

                    } else {
                        objMyApplication.setSignOnData(null);
                        objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                    }
                }
            }
        });
    }

    private void bindTransactions(TokenTransactions tokenTransactions) {
        RelativeLayout layoutTransactions, layoutFirst;
        try {
            rvTransactions = (RecyclerView) view.findViewById(R.id.rvTransactions);
            layoutTransactions = (RelativeLayout) view.findViewById(R.id.layoutTransactions);
            layoutFirst = (RelativeLayout) view.findViewById(R.id.layoutFirst);
            totalItemCount = tokenTransactions.getData().getTotalItems();
            if (tokenTransactions.getData() != null && tokenTransactions.getData().getItems() != null && tokenTransactions.getData().getItems().size() > 0) {
                layoutFirst.setVisibility(View.GONE);
                layoutTransactions.setVisibility(View.VISIBLE);
                total = tokenTransactions.getData().getTotalPages();
                objMyApplication.setTokenTransactions(tokenTransactions);
                tokenTransactionsAdapter = new TokenTransactionsAdapter(tokenTransactions.getData().getItems(), context);
//                items.addAll(tokenTransactions.getData().getItems());
//                TokenTransactions obj = new TokenTransactions();
//                TokenTransactionsData objData = new TokenTransactionsData();
//                objData.setCurrentPageNo(currentPage);
//                objData.setPageSize(tokenTransactions.getData().getPageSize());
//                objData.setItems(items);
//                objData.setTotalItems(tokenTransactions.getData().getTotalItems());
//                objData.setTotalPages(total);
//                obj.setData(objData);
//                objMyApplication.setTokenTransactions(obj);
//                tokenTransactionsAdapter = new TokenTransactionsAdapter(items, context);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvTransactions.setLayoutManager(mLayoutManager);
                rvTransactions.setItemAnimator(new DefaultItemAnimator());
                rvTransactions.setAdapter(tokenTransactionsAdapter);
                pbLoader.setVisibility(View.GONE);
                rvTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                        try {
                            super.onScrolled(recyclerView, dx, dy);
                            //swipeRefreshLayout.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
                            LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                            int totalItemCount = layoutManager.getItemCount();
                            int lastVisible = layoutManager.findLastVisibleItemPosition();

                            boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
//                            if (totalItemCount > 0 && endHasBeenReached) {
//                                //you have reached to the bottom of your recycler view
//                                if (!isAPICalled) {
//                                    isAPICalled = true;
//                                    if (currentPage <= (total - 1)) {
//                                        pbLoader.setVisibility(View.VISIBLE);
//                                        currentPage = currentPage + 1;
//                                        objMap = new HashMap<>();
//                                        objMap.put("walletCategory", Utils.walletCategory);
//                                        objMap.put("pageSize", String.valueOf(tokenTransactions.getData().getPageSize()));
//                                        objMap.put("pageNo", String.valueOf(currentPage));
//                                        dashboardViewModel.meTransactions(objMap);
//                                    } else {
//                                        pbLoader.setVisibility(View.GONE);
//                                    }
//                                }
//                            } else {
//                                pbLoader.setVisibility(View.GONE);
//                                isAPICalled = false;
//                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            } else {
                if (!isFilters && !isRefresh) {
                    layoutFirst.setVisibility(View.VISIBLE);
                    layoutTransactions.setVisibility(View.GONE);
                } else if (isFilters) {
                    isFilters = false;
                    transactionsNoData();
                } else {
                    isRefresh = false;
                    rvTransactions.setVisibility(View.GONE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadMore(List<TokenTransactionsItem> listItems) {
        try {
            tokenTransactionsAdapter.addLoadingView();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        tokenTransactionsAdapter.removeLoadingView();
                        tokenTransactionsAdapter.addData(listItems);
                        tokenTransactionsAdapter.notifyDataSetChanged();
                        pbLoader.setVisibility(View.GONE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }, 2000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getBalance(WalletResponse walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getData().getWalletInfo();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
                        objMyApplication.setGbtWallet(walletInfo.get(i));
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getExchangeAmount()));
                        tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)) + " " + walletInfo.get(i).getWalletType());
                        objMyApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindUserTracker(UserTracker userTracker) {
        try {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            int progress = 0;
            UserTrackerData data = userTracker.getData();
            if (data.getProfileVerified()) {
                progress += 1;
            }
            if (data.getAddressAvailable()) {
                progress += 1;
            }
            if (data.getPaymentModeAdded()) {
                progress += 1;
            }
            if (data.getPersonIdentified()) {
                progress += 1;
            }
            if (progress == 4) {
                objMyApplication.setTrackerComplete(true);
                cvProfile.setVisibility(View.GONE);
            } else {
                objMyApplication.setTrackerComplete(false);
                cvProfile.setVisibility(View.VISIBLE);
                progressBar.setProgress((progress * 25));
                cvProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserTracker userTracker = objMyApplication.getUserTracker();
                        if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                                && userTracker.getData().getPersonIdentified() && userTracker.getData().getPaymentModeAdded()) {

                        } else {
                            AccountActivatedFragment acctFragment = AccountActivatedFragment.newInstance(getActivity());
                            Bundle b = new Bundle();
                            b.putString("screen", "dashboard");
                            acctFragment.setArguments(b);
                            try {
                                openFragment(acctFragment, "accActivated");
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clickListeners() {
        try {
            tvClear.setOnClickListener(this);
            imgFClose.setOnClickListener(this);
            lyFromDate.setOnClickListener(this);
            lyToDate.setOnClickListener(this);
            layoutDone.setOnClickListener(this);
            imgPaste.setOnClickListener(this);
            layoutTypeArrow.setOnClickListener(this);
            layoutStatusArrow.setOnClickListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clickExportListeners() {
        try {
            imgEClose.setOnClickListener(this);
            lyCuloumns.setOnClickListener(this);
            layoutDateHead.setOnClickListener(this);
            tvDefault.setOnClickListener(this);
            tvCustom.setOnClickListener(this);
            tvEClear.setOnClickListener(this);
            layoutExport.setOnClickListener(this);
            lyEFromDate.setOnClickListener(this);
            lyEToDate.setOnClickListener(this);
            switchCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        clearExportData();
                        rvDateRange.setEnabled(false);
                        rvDateRange.setClickable(false);
                        viewDisableLayout.setVisibility(View.VISIBLE);
                        imgDateArrow.setImageResource(R.drawable.ic_down_arrow);
                        lyCuloumns.setEnabled(false);
                        lyMainColumns.setVisibility(View.GONE);
                        rvColumns.setVisibility(View.GONE);
                        layoutDates.setVisibility(View.GONE);
                        lyCustDate.setVisibility(View.GONE);
                        tvColumnHead.setText("Columns");
                        imgColArrow.setImageResource(R.drawable.ic_down_arrow);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.removeRule(RelativeLayout.ABOVE);
                        scrollView.setLayoutParams(params);
                        viewDisableLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    } else {
                        rvDateRange.setEnabled(true);
                        rvDateRange.setClickable(true);
                        viewDisableLayout.setVisibility(View.GONE);
                        imgDateArrow.setImageResource(R.drawable.ic_up_arrow);
                        lyCuloumns.setEnabled(true);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showFiltersPopUp() {
        try {
            imgFClose = (ImageView) popupFilter.findViewById(R.id.imgFClose);
            imgTypeArrow = (ImageView) popupFilter.findViewById(R.id.imgTypeArrow);
            imgStatusArrow = (ImageView) popupFilter.findViewById(R.id.imgStatusArrow);
            tvClear = (TextView) popupFilter.findViewById(R.id.tvClear);
            tvFromDate = (TextView) popupFilter.findViewById(R.id.tvFromDate);
            tvToDate = (TextView) popupFilter.findViewById(R.id.tvToDate);
            imgPaste = (ImageView) popupFilter.findViewById(R.id.imgPaste);
            etTransactionId = (TextInputEditText) popupFilter.findViewById(R.id.etTransactionId);
            lyFromDate = (RelativeLayout) popupFilter.findViewById(R.id.lyFromDate);
            lyToDate = (RelativeLayout) popupFilter.findViewById(R.id.lyToDate);
            etFromAmt = (EditText) popupFilter.findViewById(R.id.etFromAmt);
            etToAmt = (EditText) popupFilter.findViewById(R.id.etToAmt);
            rvStatus = (RecyclerView) popupFilter.findViewById(R.id.rvStatus);
            rvTypes = (RecyclerView) popupFilter.findViewById(R.id.rvTypes);
            layoutTypeArrow = (LinearLayout) popupFilter.findViewById(R.id.layoutTypeArrow);
            layoutStatusArrow = (LinearLayout) popupFilter.findViewById(R.id.layoutStatusArrow);
            layoutDone = (LinearLayout) popupFilter.findViewById(R.id.layoutDone);
            popupFilter.show();
            etFromAmt.addTextChangedListener(this);
            etToAmt.addTextChangedListener(this);
            etTransactionId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strTransId = etTransactionId.getText().toString().trim();
                    }
                }
            });
            //clearData();
            bindStatus();
            bindTypes();
            clickListeners();
            Map<String, String> objColumns = new HashMap<>();
            objColumns.put("eventTypeId", Utils.eventTypeId);
            objColumns.put("eventSubTypeId", Utils.eventSubTypeId);
            dashboardViewModel.getExportColumns(objColumns);
            etFromAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        USFormat(etFromAmt);
                    }
                }
            });
            etToAmt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        USFormat(etToAmt);
                    }
                }
            });
            etToAmt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        USFormat(etToAmt);
                    }
                    return false;
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setFromDate(TextView tvText) {
        try {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.CalendarDialogTheme,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            tvText.setText(Utils.changeFormat((monthOfYear + 1)) + "/" + Utils.changeFormat(dayOfMonth) + "/" + year);
                            strFrom = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " 00:00:00.00";
                            strEFrom = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " 00:00:00.00";
                        }
                    }, mYear, mMonth, mDay);
            if (!strTo.equals("")) {
                Date startDate = Utils.convertStringToDate(strTo);
                datePickerDialog.getDatePicker().setMaxDate(startDate.getTime());
            }
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setToDate(TextView tvText) {
        try {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.CalendarDialogTheme,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            try {
                                tvText.setText(Utils.changeFormat((monthOfYear + 1)) + "/" + Utils.changeFormat(dayOfMonth) + "/" + year);
                                if (dayOfMonth == mDay && monthOfYear == mMonth && year == mYear) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                                    String str = sdf.format(new Date());
                                    strTo = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " " + str;
                                    strETo = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " " + str;
                                } else {
                                    strTo = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " 23:59:59.00";
                                    strETo = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth) + " 23:59:59.00";
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);
            if (!strFrom.equals("")) {
                Date startDate = Utils.convertStringToDate(strFrom);
                datePickerDialog.getDatePicker().setMinDate(startDate.getTime());
            }
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStatus() {
        try {
            List<Status> listStatus = new ArrayList<>();
            listStatus = Utils.getStatus();
            statusAdapter = new StatusAdapter(listStatus, context, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            rvStatus.setLayoutManager(mLayoutManager);
            rvStatus.setItemAnimator(new DefaultItemAnimator());
            rvStatus.setAdapter(statusAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindTypes() {
        try {
            List<Status> listTypes = new ArrayList<>();
            listTypes = Utils.getTypes();
            typesAdapter = new TypesAdapter(listTypes, context);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            rvTypes.setLayoutManager(mLayoutManager);
            rvTypes.setItemAnimator(new DefaultItemAnimator());
            rvTypes.setAdapter(typesAdapter);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearData() {
        try {
            etTransactionId.setText("");
            tvFromDate.setText("");
            tvToDate.setText("");
            etFromAmt.setText("");
            etToAmt.setText("");
            strFrom = "";
            strTo = "";
            strTransId = "";
            objMap = new HashMap<>();
            currentPage = 0;
            objMap.put("walletCategory", Utils.walletCategory);
//            objMap.put("pageSize", String.valueOf(Utils.pageSize));
//            objMap.put("pageNo", String.valueOf(currentPage));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareFilters() {
        try {
            if (objMap != null && objMap.size() == 0) {
                objMap.put("walletCategory", Utils.walletCategory);
            }
            if (!etTransactionId.getText().toString().equals("") && !strTransId.equals("")) {
                if (etTransactionId.getText().toString().length() > 30) {
                    if (etTransactionId.getText().toString().substring(0, 30).equals(strTransId.substring(0, 30))) {
                        objMap.put("gbxTransactionId", strTransId);
                    } else {
                        objMap.put("gbxTransactionId", etTransactionId.getText().toString());
                    }
                } else {
                    if (etTransactionId.getText().toString().equals(strTransId)) {
                        objMap.put("gbxTransactionId", strTransId);
                    } else {
                        objMap.put("gbxTransactionId", etTransactionId.getText().toString());
                    }
                }
            }
//            if (!strFrom.equals("")) {
//                objMap.put("fromDate", objMyApplication.exportDate(strFrom));
//                objMap.put("fromDateOperator", ">=");
//            }
//            if (!strTo.equals("")) {
//                objMap.put("toDate", objMyApplication.exportDate(strTo));
//                objMap.put("toDateOperator", "<=");
//            }

            if (!strFrom.equals("")) {
                objMap.put("updatedFromDate", objMyApplication.exportDate(strFrom));
                objMap.put("updatedFromDateOperator", ">=");
            }
            if (!strTo.equals("")) {
                objMap.put("updatedToDate", objMyApplication.exportDate(strTo));
                objMap.put("updatedToDateOperator", "<=");
            }
            if (!etFromAmt.getText().toString().trim().equals("")) {
                objMap.put("fromAmount", etFromAmt.getText().toString().replace(",", ""));
                objMap.put("fromAmountOperator", ">=");
            }
            if (!etToAmt.getText().toString().trim().equals("")) {
                objMap.put("toAmount", etToAmt.getText().toString().replace(",", ""));
                objMap.put("toAmountOperator", "<=");
            }
            if (objMyApplication.getStatusId() != -1) {
                objMap.put("txnStatus", String.valueOf(objMyApplication.getStatusId()));
                objMap.put("txnStatusOperator", "=");
            }
            if (objMyApplication.getTypeId() != -1) {
                objMap.put("transactionType", String.valueOf(objMyApplication.getTypeId()));
            }
//            currentPage = 0;
//            if (Build.VERSION.SDK_INT >= 24) {
//                objMap.replace("pageNo", String.valueOf(currentPage));
//            }
            objMyApplication.setFiltersMap(objMap);
            popupFilter.dismiss();
            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            isFilters = true;
            dashboardViewModel.meTransactions(objMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getCopiedText() {
        String strText = "";
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard == null) return "";
            ClipData clip = clipboard.getPrimaryClip();
            if (clip == null) return "";
            ClipData.Item item = clip.getItemAt(0);
            if (item == null) return "";
            CharSequence textToPaste = item.getText();
            if (textToPaste == null) {
                return "";
            } else {
                strText = textToPaste.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strText;
    }

    private void showExportsPopUp() {
        try {
            imgEClose = (ImageView) popupExport.findViewById(R.id.imgEClose);
            imgDateArrow = (ImageView) popupExport.findViewById(R.id.imgDateArrow);
            imgColArrow = (ImageView) popupExport.findViewById(R.id.imgColArrow);
            lyCuloumns = (RelativeLayout) popupExport.findViewById(R.id.lyCuloumns);
            lyCustDate = (RelativeLayout) popupExport.findViewById(R.id.lyCustDate);
            layoutDateHead = (RelativeLayout) popupExport.findViewById(R.id.layoutDateHead);
            lyEFromDate = (RelativeLayout) popupExport.findViewById(R.id.lyEFromDate);
            lyEToDate = (RelativeLayout) popupExport.findViewById(R.id.lyEToDate);
            lyMainColumns = (LinearLayout) popupExport.findViewById(R.id.lyMainColumns);
            layoutDates = (LinearLayout) popupExport.findViewById(R.id.layoutDates);
            layoutExport = (LinearLayout) popupExport.findViewById(R.id.layoutExport);
            tvDefault = (TextView) popupExport.findViewById(R.id.tvDefault);
            tvCustom = (TextView) popupExport.findViewById(R.id.tvCustom);
            tvColumnHead = (TextView) popupExport.findViewById(R.id.tvColumnHead);
            tvEFromDate = (TextView) popupExport.findViewById(R.id.tvEFromDate);
            tvEToDate = (TextView) popupExport.findViewById(R.id.tvEToDate);
            tvEClear = (TextView) popupExport.findViewById(R.id.tvEClear);
            rvColumns = (RecyclerView) popupExport.findViewById(R.id.rvColumns);
            rvDateRange = (RecyclerView) popupExport.findViewById(R.id.rvDateRange);
            viewDisableLayout = (View) popupExport.findViewById(R.id.viewDisableLayout);
//            switchCustom = (SwitchCompat) popupExport.findViewById(R.id.switchCustom);
            switchCustom = (Switch) popupExport.findViewById(R.id.switchCustom);
            scrollView = (ScrollView) popupExport.findViewById(R.id.scrollView);
            popupExport.show();
            switchCustom.setChecked(false);
            clearExportData();
            bindDateRange();
            clickExportListeners();
            enableCalendar("today");
//            Map<String, String> objColumns = new HashMap<>();
//            objColumns.put("eventTypeId", Utils.eventTypeId);
//            objColumns.put("eventSubTypeId", Utils.eventSubTypeId);
//            dashboardViewModel.getExportColumns(objColumns);
            prepareColumns();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareColumns() {
        List<ExportColumnsData> data = new ArrayList<>();
        try {
            ExportColumns objExportColumns = new ExportColumns();
            objExportColumns.setError(null);
            objExportColumns.setStatus("");
            objExportColumns.setTimestamp("");
            ExportColumnsData obj = new ExportColumnsData();

            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Created Date");
            obj.setExportUIName("Created Date");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Reference ID");
            obj.setExportUIName("Reference ID");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Type");
            obj.setExportUIName("TransactionType");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Sub Type");
            obj.setExportUIName("SubType");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Description");
            obj.setExportUIName("Description");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Amount");
            obj.setExportUIName("Amount");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Balance");
            obj.setExportUIName("Balance");
            obj.setFrefix(null);
            data.add(obj);

            obj = new ExportColumnsData();
            obj.setEventTypeId(Integer.parseInt(Utils.eventTypeId));
            obj.setEventTypeName("EXPORT");
            obj.setEventSubTypeId(Integer.parseInt(Utils.eventSubTypeId));
            obj.setExportColumnName("Status");
            obj.setExportUIName("Status");
            obj.setFrefix(null);
            data.add(obj);

            objExportColumns.setData(data);
            objMyApplication.setExportColumns(objExportColumns);
            bindColumns(objExportColumns);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindColumns(ExportColumns exportColumns) {
        try {
            objMyApplication.initializeListColumns();
            if (exportColumns.getData() != null && exportColumns.getData().size() > 0) {
                columnsAdapter = new ColumnSelectionAdapter(exportColumns.getData(), context);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvColumns.setLayoutManager(mLayoutManager);
                rvColumns.setItemAnimator(new DefaultItemAnimator());
                rvColumns.setAdapter(columnsAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindDateRange() {
        try {
            List<Status> listDates = new ArrayList<>();
            listDates = Utils.getDateRange();
            dateAdapter = new DateSelectionAdapter(listDates, context, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            rvDateRange.setLayoutManager(mLayoutManager);
            rvDateRange.setItemAnimator(new DefaultItemAnimator());
            rvDateRange.setAdapter(dateAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openColumnOptions() {
        try {
            tvColumnHead.setText("Columns");
            if (lyMainColumns.getVisibility() == View.VISIBLE) {
                lyMainColumns.setVisibility(View.GONE);
                imgColArrow.setImageResource(R.drawable.ic_down_arrow);
            } else {
                lyMainColumns.setVisibility(View.VISIBLE);
                rvColumns.setVisibility(View.GONE);
                imgColArrow.setImageResource(R.drawable.ic_up_arrow);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.removeRule(RelativeLayout.ABOVE);
                scrollView.setLayoutParams(params);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadColumnsList(String columnHead) {
        try {
            tvColumnHead.setText(columnHead);
            lyMainColumns.setVisibility(View.GONE);
            columnsAdapter.updateData(columnHead);
            rvColumns.setVisibility(View.VISIBLE);
            if (rvDateRange.getVisibility() == View.VISIBLE && rvColumns.getVisibility() == View.VISIBLE && !switchCustom.isChecked()) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ABOVE, R.id.layoutExport);
                scrollView.setLayoutParams(params);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearExportData() {
        try {
            objMyApplication.initializeListColumns();
            tvColumnHead.setText("Columns");
            tvEFromDate.setText("");
            tvEToDate.setText("");
            rvColumns.setVisibility(View.GONE);
            lyMainColumns.setVisibility(View.GONE);
            columnsAdapter.notifyDataSetChanged();
            objMyApplication.setDateId(0);
            strEFrom = "";
            strETo = "";
            enableCalendar("today");
            dateAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableCalendar(String value) {
        try {
            lyCustDate.setVisibility(View.GONE);
            layoutDates.setVisibility(View.GONE);
            strEFrom = "";
            strETo = "";
            switch (value.toLowerCase()) {
                case "custom":
                    lyCustDate.setVisibility(View.VISIBLE);
                    layoutDates.setVisibility(View.VISIBLE);
                    break;
                case "today":
                    strEFrom = Utils.getCurrentDate("today");
                    strETo = Utils.getCurrentDate("");
                    break;
                case "last seven days":
                    strEFrom = Utils.getFromDate(7);
                    strETo = Utils.getCurrentDate("");
                    break;
                case "current month":
                    strEFrom = Utils.getCurrentDate("current");
                    strETo = Utils.getCurrentDate("");
                    break;
                case "previous month":
                    String strDate = Utils.getCurrentDate("previous");
                    strEFrom = strDate.split(";")[0];
                    strETo = strDate.split(";")[1];
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareExportJSONObject() {
        try {
            ExportRequest request = new ExportRequest();
            FilterColumns objFilterColumns = new FilterColumns();

            request.setEventTypeId(Utils.eventTypeId);
            request.setEventSubTypeId(Utils.eventSubTypeId);
            List<String> listColumns = new ArrayList<>();
            if (objMyApplication.getSelectedListColumns() != null && objMyApplication.getSelectedListColumns().size() > 0) {
                for (int i = 0; i < objMyApplication.getSelectedListColumns().size(); i++) {
                    listColumns.add(objMyApplication.getSelectedListColumns().get(i));
                }
            } else {
                List<ExportColumnsData> list = objMyApplication.getExportColumns().getData();
                for (int i = 0; i < list.size(); i++) {
                    listColumns.add(list.get(i).getExportColumnName());
                }
            }
            request.setExportColumns(listColumns);
            if (switchCustom.isChecked() && objMyApplication.getFiltersMap() != null && objMyApplication.getFiltersMap().size() > 0) {
                objFilters = new FilterColumns();
                for (Map.Entry<String, String> entry : objMyApplication.getFiltersMap().entrySet()) {
                    objFilterColumns = prepareFilterData(entry.getKey(), entry.getValue());
                }
                objFilterColumns.setWalletCategory(Integer.parseInt(Utils.walletCategory));
            } else if (!switchCustom.isChecked()) {
                objFilterColumns = prepareFilterObject();
            }
            request.setFilterColumns(objFilterColumns);
            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            dashboardViewModel.exportData(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private FilterColumns prepareFilterObject() {
        FilterColumns objFilterColumns = new FilterColumns();
        try {
            objFilterColumns.setWalletCategory(Integer.parseInt(Utils.walletCategory));
            if (!strEFrom.equals("") && !strETo.equals("")) {
                String strFDate = objMyApplication.exportDate(strEFrom);
                String strTDate = objMyApplication.exportDate(strETo);
                objFilterColumns.setFromDate(strFDate);
                objFilterColumns.setFromDateOperator(">=");
                objFilterColumns.setToDate(strTDate);
                objFilterColumns.setToDateOperator("<=");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objFilterColumns;
    }

    private FilterColumns prepareFilterData(String strKey, String strValue) {
        try {
            switch (strKey) {
                case "gbxTransactionId":
                    objFilters.setGbxTransactionId(strValue);
                    break;
                case "transactionType":
                    objFilters.setTransactionType(Integer.parseInt(strValue));
                    break;
                case "txnStatus":
                    objFilters.setTxnStatus(Integer.parseInt(strValue));
                    break;
                case "txnStatusOperator":
                    objFilters.setTxnStatusOperator(strValue);
                    break;
//                case "fromDate":
//                    objFilters.setFromDate(strValue);
//                    break;
//                case "fromDateOperator":
//                    objFilters.setFromDateOperator(strValue);
//                    break;
//                case "toDate":
//                    objFilters.setToDate(strValue);
//                    break;
//                case "toDateOperator":
//                    objFilters.setToDateOperator(strValue);
//                    break;
                case "updatedFromDate":
                    objFilters.setFromDate(strValue);
                    break;
                case "updatedFromDateOperator":
                    objFilters.setFromDateOperator(strValue);
                    break;
                case "updatedToDate":
                    objFilters.setToDate(strValue);
                    break;
                case "updatedToDateOperator":
                    objFilters.setToDateOperator(strValue);
                    break;
                case "fromAmount":
                    objFilters.setFromAmount(strValue);
                    break;
                case "fromAmountOperator":
                    objFilters.setFromAmountOperator(strValue);
                    break;
                case "toAmount":
                    objFilters.setToAmount(strValue);
                    break;
                case "toAmountOperator":
                    objFilters.setToAmountOperator(strValue);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objFilters;
    }

    private void loadBuyTokens() {
        try {
            ((MainActivity) context).loadBuyTokens();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getStates() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            objMyApplication.setListStates(listStates);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void transactionsNoData() {
        try {
            isNoData = true;
            ImageView imgClose;
            popupNoData = new Dialog(context, R.style.DialogTheme);
            popupNoData.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupNoData.setContentView(R.layout.notransactionsdata);
            Window window = popupNoData.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupNoData.getWindow().setAttributes(lp);
            popupNoData.show();
            imgClose = (ImageView) popupNoData.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupNoData.dismiss();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void USFormat(EditText etAmount) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeFocus() {
        try {
            etToAmt.clearFocus();
            etFromAmt.clearFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadProfile() {
        try {
            if (objMyApplication.getStrProfileImg() != null && !objMyApplication.getStrProfileImg().trim().equals("")) {
                tvUserInfo.setVisibility(View.GONE);
                cvProfilePic.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(objMyApplication.getStrProfileImg())
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(imgProfile);
            } else {
                tvUserInfo.setText(objMyApplication.getStrUserCode());
                tvUserInfo.setVisibility(View.VISIBLE);
                cvProfilePic.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void exportSuccess() {
        try {
            popupExportMsg = new Dialog(context, R.style.DialogTheme);
            popupExportMsg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupExportMsg.setContentView(R.layout.exportmsgpopup);
            Window window = popupExportMsg.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupExportMsg.getWindow().setAttributes(lp);
            popupExportMsg.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    popupExportMsg.dismiss();
                    popupExport.dismiss();
                }
            }, 5000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class FetchData extends AsyncTask<Void, Void, Boolean> {

        public FetchData(Context context) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                buyViewModel.meBanks();
                //notificationsViewModel.meNotifications();
                //payViewModel.getReceiveRequests();
                buyViewModel.meSignOn();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean list) {
            super.onPostExecute(list);

        }
    }

}
