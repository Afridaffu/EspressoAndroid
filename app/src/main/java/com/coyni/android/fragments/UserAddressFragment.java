package com.coyni.android.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.view.MainActivity;
import com.coyni.android.adapters.StatesListAdapter;
import com.coyni.android.model.States;
import com.coyni.android.model.user.User;
import com.coyni.android.model.user.UserData;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.usertracker.UserTrackerData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;

import java.util.ArrayList;
import java.util.List;

public class UserAddressFragment extends Fragment {
    View view;
    static Context context;
    LinearLayout llUpdateAddress;
    ImageView imgBack;
    TextView addOrupdateAddress, addOrEditMailingAddress;
    MyApplication objMyApplication;
    TextInputEditText etAddress1, etAddress2, etCountry, etCity, etZipCode, etStateRegion;
    DashboardViewModel dashboardViewModel;
    ProgressDialog dialog;
    Dialog popupStates;
    StatesListAdapter statesListAdapter;
    List<States> listStates = new ArrayList<>();

    public UserAddressFragment() {
    }

    public static UserAddressFragment newInstance(Context cxt) {
        UserAddressFragment fragment = new UserAddressFragment();
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
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_address, container, false);
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            llUpdateAddress = (LinearLayout) view.findViewById(R.id.llUpdateAddress);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            etAddress1 = (TextInputEditText) view.findViewById(R.id.etAddress1);
            etAddress2 = (TextInputEditText) view.findViewById(R.id.etAddress2);
            etCountry = (TextInputEditText) view.findViewById(R.id.etCountry);
            etCity = (TextInputEditText) view.findViewById(R.id.etCity);
            etZipCode = (TextInputEditText) view.findViewById(R.id.etZipCode);
            etStateRegion = (TextInputEditText) view.findViewById(R.id.etStateRegion);
            addOrupdateAddress = (TextView) view.findViewById(R.id.addOrupdateAddress);
            addOrEditMailingAddress = (TextView) view.findViewById(R.id.addOrEditMailingAddress);
            LinearLayout layoutStateArrow = (LinearLayout) view.findViewById(R.id.layoutStateArrow);

            llUpdateAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserData obj = new UserData();
                    if (Utils.checkInternet(context)) {
                        if (etAddress1.getText().toString().trim().equals("")) {
                            Utils.displayAlert("Please enter Address Line1", getActivity());
                        } else if (etStateRegion.getText().toString().trim().equals("")) {
                            Utils.displayAlert("State is required", getActivity());
                        } else if (etCity.getText().toString().trim().equals("")) {
                            Utils.displayAlert("City is required", getActivity());
                        } else if (etZipCode.getText().toString().trim().equals("")) {
                            Utils.displayAlert("Please enter Zip Code", getActivity());
                        } else if (!etZipCode.getText().toString().equals("") && etZipCode.getText().toString().length() < 5) {
                            Utils.displayAlert("Zip Code / Postal Code must have at least 5 numbers", getActivity());
                        } else {
                            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.show();
                            if (objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                                obj.setAddressLine1(etAddress1.getText().toString().trim());
                                obj.setAddressLine2(etAddress2.getText().toString().trim());
                                obj.setState(etStateRegion.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipCode.getText().toString().trim());
                                dashboardViewModel.meUpdateAddress(obj);
                                updateAddress();
                            } else {
                                obj.setAddressLine1(etAddress1.getText().toString().trim());
                                obj.setAddressLine2(etAddress2.getText().toString().trim());
                                obj.setAddressType(0);
//                                obj.setCountry(etCountry.getText().toString().trim());
                                obj.setCountry("US");
                                obj.setState(etStateRegion.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipCode.getText().toString().trim());
                                dashboardViewModel.meAddAddress(obj);
                                initObservables();
                            }
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.internet), getActivity());
                    }
                }
            });
            layoutStateArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statesPopup();
                }
            });


            // Checking address availability from UserTracker
            if (objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                addOrupdateAddress.setText("Update");
                addOrEditMailingAddress.setText("Edit Mailing Address");
                if (objMyApplication.getStrAddressLine1() != null && !objMyApplication.getStrAddressLine1().equals("")) {
                    etAddress1.setText(objMyApplication.getStrAddressLine1());
                }
                if (objMyApplication.getStrAddressLine2() != null && !objMyApplication.getStrAddressLine2().equals("")) {
                    etAddress2.setText(objMyApplication.getStrAddressLine2());
                }
                if (objMyApplication.getStrCity() != null && !objMyApplication.getStrCity().equals("")) {
                    etCity.setText(objMyApplication.getStrCity());
                }
                if (objMyApplication.getStrState() != null && !objMyApplication.getStrState().equals("")) {
                    etStateRegion.setText(objMyApplication.getStrState());
                }
                if (objMyApplication.getStrZipCode() != null && !objMyApplication.getStrZipCode().equals("")) {
                    etZipCode.setText(objMyApplication.getStrZipCode());
                }
            } else {
                addOrEditMailingAddress.setText("Add Mailing Address");
                addOrupdateAddress.setText("SAVE");
            }


            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((MainActivity) context).loadProfile();
                    getActivity().onBackPressed();
                }
            });
            etAddress1.requestFocus();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void initObservables() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                dialog.dismiss();
                if (user != null && user.getStatus().toLowerCase().equals("success")) {
                    Utils.displayCloseAlert("Address added Successfully", getActivity());
                    bindUserTracker(objMyApplication.getUserTracker());
                    dashboardViewModel.fiservUser();
                    //((MainActivity) context).loadProfile();
                    getActivity().onBackPressed();
                } else {
                    Utils.displayAlert(user.getError().getErrorDescription(), getActivity());
                }
            }
        });
    }

    private void updateAddress() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                dialog.dismiss();
                if (user != null && user.getStatus().toLowerCase().equals("success")) {
                    Utils.displayCloseAlert("User details successfully updated!", getActivity());
                    bindUserTracker(objMyApplication.getUserTracker());
                    objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                    objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                    objMyApplication.setStrState(user.getData().getState());
                    objMyApplication.setStrCity(user.getData().getCity());
                    objMyApplication.setStrZipCode(user.getData().getZipCode());
                    objMyApplication.setIntUserId(user.getData().getId());
                    ProfileFragment profileFragment = ProfileFragment.newInstance(getActivity());
                    try {
                        openFragment(profileFragment, "profile");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Utils.displayAlert(user.getError().getErrorDescription(), getActivity());
                }
            }
        });
    }

    private void bindUserTracker(UserTracker userTracker) {
        UserTrackerData data = userTracker.getData();
        if (!data.getAddressAvailable()) {
            objMyApplication.getUserTracker().getData().setAddressAvailable(true);
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
//            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void statesPopup() {
        try {
            popupStates = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            popupStates.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupStates.setContentView(R.layout.countrieslist);
            popupStates.setCanceledOnTouchOutside(false);
            popupStates.setCancelable(false);
            popupStates.show();
            bindStates();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStates() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        TextView tvNoResults;
        statesListAdapter = new StatesListAdapter(null, context, "addfrag", null, this);
        try {
            imgBack = popupStates.findViewById(R.id.imgBack);
            etSearch = popupStates.findViewById(R.id.etSearch);
            tvNoResults = popupStates.findViewById(R.id.tvNoResults);
            rvCountries = popupStates.findViewById(R.id.rvCountries);
            listStates = objMyApplication.getListStates();
            if (listStates != null && listStates.size() > 0) {
                statesListAdapter = new StatesListAdapter(listStates, context, "addfrag", null, this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvCountries.setLayoutManager(mLayoutManager);
                rvCountries.setItemAnimator(new DefaultItemAnimator());
                rvCountries.setAdapter(statesListAdapter);
            }
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupStates.dismiss();
                }
            });
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String search_key = s.toString();
                        List<States> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (listStates != null && listStates.size() > 0) {
                            for (int i = 0; i < listStates.size(); i++) {
                                sIndex = listStates.get(i).getName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(listStates.get(i));
                                }
                            }
                            if (filterList != null && filterList.size() > 0) {
                                statesListAdapter.updateList(filterList);
                                rvCountries.setVisibility(View.VISIBLE);
                                tvNoResults.setVisibility(View.GONE);
                            } else {
                                rvCountries.setVisibility(View.GONE);
                                tvNoResults.setVisibility(View.VISIBLE);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateState(String strState) {
        try {
            popupStates.dismiss();
            etStateRegion.setText(strState);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
