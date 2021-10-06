package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.model.APIError;
import com.coyni.android.model.Pagination;
import com.coyni.android.model.receiverequests.ReceiveRequests;
import com.coyni.android.model.receiverequests.ReceiveRequestsItems;
import com.coyni.android.model.sentrequests.SentRequests;
import com.coyni.android.model.sentrequests.SentRequestsItems;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.model.userrequest.UserReqPatchResponse;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.PayViewModel;
import com.google.android.material.tabs.TabLayout;
import com.coyni.android.R;
import com.coyni.android.adapters.PostedAdapter;
import com.coyni.android.adapters.ReceivedAdapter;
import com.coyni.android.model.sendtransfer.TransferSendRequest;
import com.coyni.android.model.sendtransfer.TransferSendResponse;
import com.coyni.android.model.userrequest.UserRequestPatch;
import com.coyni.android.viewmodel.SendViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsFragment extends Fragment {
    View view;
    static Context context;
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    SendViewModel sendViewModel;
    ProgressDialog dialog;
    RecyclerView rvPosted, rvReceived;
    TabLayout tabLayout;
    WalletInfo cynWallet;
    PostedAdapter postedAdapter;
    ReceivedAdapter receivedAdapter;
    String strScreen = "";
    Toolbar toolbar;
    int payId;
    TextView tvNoPostRecords, tvNoReceRecords;
    List<ReceiveRequestsItems> listReceItems;
    List<SentRequestsItems> listSentItems;
//    int currentHolidayCount, totalHolidayCount, pageSize = 20, pageNo = 0, currentPage;
//    Map<String, String> objMap = new HashMap<>();
//    NestedScrollView nvScroll;
//    ProgressBar pbPosted;

    public RequestsFragment() {
    }

    public static RequestsFragment newInstance(Context cxt) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        ((MainActivity) context).setToolbar(toolbar, true);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.requestslayout, container, false);
        try {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar_sent);
            objMyApplication = (MyApplication) context.getApplicationContext();
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            cynWallet = objMyApplication.getGbtWallet();
            tabLayout = view.findViewById(R.id.tabLayout);
            rvPosted = (RecyclerView) view.findViewById(R.id.rvPosted);
            rvReceived = (RecyclerView) view.findViewById(R.id.rvReceived);
            tvNoPostRecords = (TextView) view.findViewById(R.id.tvNoPostRecords);
            tvNoReceRecords = (TextView) view.findViewById(R.id.tvNoReceRecords);
//            nvScroll = (NestedScrollView) view.findViewById(R.id.nvScroll);
//            pbPosted = (ProgressBar) view.findViewById(R.id.pbPosted);
            if (Utils.checkInternet(context)) {
                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
//                objMap.put("pageSize", String.valueOf(pageSize));
//                objMap.put("pageNo", String.valueOf(pageNo));
//                payViewModel.getSentRequests(objMap);
                payViewModel.getSentRequests();
//                payViewModel.getReceiveRequests();
                new FetchData(getActivity()).execute();
            } else {
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    try {
                        if (tab.getPosition() == 0) {
                            if (listSentItems != null && listSentItems.size() > 0) {
                                rvPosted.setVisibility(View.VISIBLE);
                                rvReceived.setVisibility(View.GONE);
                                tvNoPostRecords.setVisibility(View.GONE);
                                tvNoReceRecords.setVisibility(View.GONE);
                            } else {
                                rvPosted.setVisibility(View.GONE);
                                rvReceived.setVisibility(View.GONE);
                                tvNoPostRecords.setVisibility(View.VISIBLE);
                                tvNoReceRecords.setVisibility(View.GONE);
                            }
                        } else {
                            if (listReceItems != null && listReceItems.size() > 0) {
                                rvPosted.setVisibility(View.GONE);
                                rvReceived.setVisibility(View.VISIBLE);
                                tvNoPostRecords.setVisibility(View.GONE);
                                tvNoReceRecords.setVisibility(View.GONE);
                            } else {
                                rvPosted.setVisibility(View.GONE);
                                rvReceived.setVisibility(View.GONE);
                                tvNoPostRecords.setVisibility(View.GONE);
                                tvNoReceRecords.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            initObservables();

//            nvScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    try {
//                        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
//                            // in this method we are incrementing page number,
//                            // making progress bar visible and calling get data method.
//                            //page++;
//                            pbPosted.setVisibility(View.VISIBLE);
//                            //getDataFromAPI(page, limit);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void initObservables() {
        payViewModel.getSentRequestsMutableLiveData().observe(getActivity(), new Observer<SentRequests>() {
            @Override
            public void onChanged(SentRequests sentRequests) {
                if (sentRequests != null) {
                    if (sentRequests.getStatus().toLowerCase().equals("success")) {
                        if (sentRequests.getData().getItems() != null && sentRequests.getData().getItems().size() > 0) {
                            bindSentRequests(sentRequests.getData().getItems());
                        }
                    } else {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

        payViewModel.getReceiveRequestsMutableLiveData().observe(getActivity(), new Observer<ReceiveRequests>() {
            @Override
            public void onChanged(ReceiveRequests receiveRequests) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (receiveRequests != null) {
                    if (receiveRequests.getStatus().toLowerCase().equals("success")) {
                        if (receiveRequests.getData().getItems() != null && receiveRequests.getData().getItems().size() > 0) {
                            bindReceiveRequests(receiveRequests.getData().getItems());
                        }
                    }
                }
            }
        });

        payViewModel.getApiErrorMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
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

        sendViewModel.getSendTokenMutableLiveData().observe(getActivity(), new Observer<TransferSendResponse>() {
            @Override
            public void onChanged(TransferSendResponse transferSendResponse) {
                dialog.dismiss();
                if (transferSendResponse != null && transferSendResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                    UserRequestPatch requestPatch = new UserRequestPatch();
                    requestPatch.setId(payId);
                    requestPatch.setStatus(Utils.complete);
                    payViewModel.updateUserRequests(requestPatch);
                }
            }
        });

        payViewModel.getUserReqPatchResponseMutableLiveData().observe(getActivity(), new Observer<UserReqPatchResponse>() {
            @Override
            public void onChanged(UserReqPatchResponse userReqPatchResponse) {
                dialog.dismiss();
                if (userReqPatchResponse != null && userReqPatchResponse.getStatus().toUpperCase().equals("SUCCESS")) {
//                    Utils.displayAlert(userReqPatchResponse.getData().getMessage(), getActivity());
                    Utils.displayCloseAlert(userReqPatchResponse.getData().getMessage(), getActivity());
                    if (strScreen.equals(Utils.remind) || strScreen.equals(Utils.cancel)) {
                        dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
//                        pageSize = 20;
//                        pageNo = 0;
//                        objMap.put("pageSize", String.valueOf(pageSize));
//                        objMap.put("pageNo", String.valueOf(pageNo));
//                        payViewModel.getSentRequests(objMap);
                        payViewModel.getSentRequests();
                    } else if (strScreen.equals(Utils.decline) || strScreen.equals(Utils.complete)) {
                        dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        payViewModel.getReceiveRequests();
                    }
                }
            }
        });

        sendViewModel.getTransferFeeMutableLiveData().observe(getActivity(), new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                if (transferFeeResponse != null && transferFeeResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                    ((ReceivedAdapter) receivedAdapter).setTransferFeeResponse(transferFeeResponse);
                }
            }
        });

        sendViewModel.getApiErrorMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
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

        payViewModel.getPostNoDataMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                        } else {
                            if (apiError.getError().getErrorDescription().toLowerCase().contains("not found")) {
                                rvPosted.setVisibility(View.GONE);
                                rvReceived.setVisibility(View.GONE);
                                tvNoPostRecords.setVisibility(View.VISIBLE);
                                tvNoPostRecords.setText(apiError.getError().getErrorDescription());
                                listSentItems = new ArrayList<>();
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                            }
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                    }
                }
            }
        });

        payViewModel.getReceNoDataMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                        } else {
                            if (apiError.getError().getErrorDescription().toLowerCase().contains("not found")) {
                                rvReceived.setVisibility(View.GONE);
                                rvPosted.setVisibility(View.GONE);
                                tvNoReceRecords.setVisibility(View.VISIBLE);
                                tvNoReceRecords.setText(apiError.getError().getErrorDescription());
                                listReceItems = new ArrayList<>();
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                            }
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                    }
                }
            }
        });
    }

    private void bindSentRequests(List<SentRequestsItems> listItems) {
        try {
            if (listItems != null && listItems.size() > 0) {
                listSentItems = listItems;
                if (tabLayout.getSelectedTabPosition() == 0) {
                    rvPosted.setVisibility(View.VISIBLE);
                    tvNoPostRecords.setVisibility(View.GONE);
                    tvNoReceRecords.setVisibility(View.GONE);
                }
//                Collections.sort(listItems, new Comparator<SentRequestsItems>() {
//                    public int compare(SentRequestsItems obj1, SentRequestsItems obj2) {
//                        // ## Descending order
//                        return Integer.valueOf(obj2.getId()).compareTo(Integer.valueOf(obj1.getId())); // To compare integer values
//                    }
//                });
                postedAdapter = new PostedAdapter(listItems, getActivity(), RequestsFragment.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvPosted.setLayoutManager(mLayoutManager);
                rvPosted.setItemAnimator(new DefaultItemAnimator());
                rvPosted.setHasFixedSize(true);
                rvPosted.setItemViewCacheSize(20);
                rvPosted.setAdapter(postedAdapter);

            } else {
                rvPosted.setVisibility(View.GONE);
                listSentItems = new ArrayList<>();
            }
            dialog.dismiss();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindReceiveRequests(List<ReceiveRequestsItems> listItems) {
        try {
            if (listItems != null && listItems.size() > 0) {
                if (tabLayout.getSelectedTabPosition() == 1) {
                    rvReceived.setVisibility(View.VISIBLE);
                    tvNoReceRecords.setVisibility(View.GONE);
                    tvNoPostRecords.setVisibility(View.GONE);
                }
                List<ReceiveRequestsItems> listRequests = new ArrayList<>();
                for (int i = 0; i < listItems.size(); i++) {
                    if (!listItems.get(i).getStatus().toLowerCase().equals("completed") && !listItems.get(i).getStatus().toLowerCase().equals("declined")) {
                        listRequests.add(listItems.get(i));
                    }
                }
                if (listRequests != null && listRequests.size() > 0) {
                    listReceItems = listRequests;
//                    Collections.sort(listRequests, new Comparator<ReceiveRequestsItems>() {
//                        public int compare(ReceiveRequestsItems obj1, ReceiveRequestsItems obj2) {
//                            // ## Descending order
//                            return Integer.valueOf(obj2.getId()).compareTo(Integer.valueOf(obj1.getId())); // To compare integer values
//                        }
//                    });
                    receivedAdapter = new ReceivedAdapter(listRequests, getActivity(), RequestsFragment.this);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    rvReceived.setLayoutManager(mLayoutManager);
                    rvReceived.setItemAnimator(new DefaultItemAnimator());
                    rvReceived.setAdapter(receivedAdapter);
                } else {
                    tvNoReceRecords.setText("No Pending requests found.");
                    if (tabLayout.getSelectedTabPosition() == 1) {
                        rvReceived.setVisibility(View.GONE);
                        listReceItems = new ArrayList<>();
                        tvNoReceRecords.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                rvReceived.setVisibility(View.GONE);
                listReceItems = new ArrayList<>();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initiateRemind(int Id, String strStatus, String strMsg) {
        try {
            strScreen = strStatus;
            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

            UserRequestPatch requestPatch = new UserRequestPatch();
            requestPatch.setId(Id);
            requestPatch.setStatus(strStatus);
            requestPatch.setRemarks(strMsg);
            payViewModel.updateUserRequests(requestPatch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initiatePayment(String strAmount, String strWalleId, int Id) {
        try {
            payId = Id;
            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            strScreen = Utils.complete;
            TransferSendRequest request = new TransferSendRequest();
            request.setTokens(strAmount);
            request.setRemarks("Amount sent");
            request.setRecipientWalletId(strWalleId);
            if (Utils.checkInternet(context)) {
                sendViewModel.sendTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void calculateFee(String strAmount) {
        try {
            TransferFeeRequest request = new TransferFeeRequest();
            request.setTokens(strAmount.replace(",", ""));
            request.setTxnType(Utils.payType);
            request.setTxnSubType(Utils.paySubType);
            if (Utils.checkInternet(context)) {
                sendViewModel.transferFee(request);
            }
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
                payViewModel.getReceiveRequests();
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
