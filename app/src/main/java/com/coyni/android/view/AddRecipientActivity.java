package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coyni.android.R;
import com.coyni.android.adapters.ContactsAdapter;
import com.coyni.android.adapters.RecentUsersAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.contacts.Contacts;
import com.coyni.android.model.recentuser.RecentUsers;
import com.coyni.android.model.recentuser.RecentUsersData;
import com.coyni.android.model.reguser.RegUsersResponse;
import com.coyni.android.model.reguser.RegUsersResponseData;
import com.coyni.android.model.reguser.RegisteredUsersRequest;
import com.coyni.android.model.templates.TemplateRequest;
import com.coyni.android.model.templates.TemplateResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.PayViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddRecipientActivity extends AppCompatActivity implements TextWatcher {
    public static final int REQUEST_READ_CONTACTS = 79;
    List<Contacts> mobileArray;
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    ProgressDialog dialog;
    EditText etSearch;
    String strWalletId = "";
    NestedScrollView scrollView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 555;
    RecyclerView rvContacts;
    TextView tvPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_recipient);
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
            objMyApplication.userInactive(AddRecipientActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddRecipientActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(AddRecipientActivity.this, this, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            if (count > 2) {
                strWalletId = s.toString().trim().replace("\n", "");
                etSearch.removeTextChangedListener(AddRecipientActivity.this);
                etSearch.setText(strWalletId);
                etSearch.setSelection(strWalletId.length());
                etSearch.addTextChangedListener(AddRecipientActivity.this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_READ_CONTACTS:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        rvContacts.setVisibility(View.VISIBLE);
                        tvPermissions.setVisibility(View.GONE);
                        mobileArray = getAllContacts();
                        objMyApplication.setListContacts(mobileArray);
                        prepareContacts();
                    } else {
                        // permission denied,Disable the
                        // functionality that depends on this permission.
                        rvContacts.setVisibility(View.GONE);
                        tvPermissions.setVisibility(View.VISIBLE);
                    }
                    break;
                case MY_PERMISSIONS_REQUEST_CAMERA:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(AddRecipientActivity.this, ScanActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception ex) {
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
            objMyApplication = (MyApplication) getApplicationContext();
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            RelativeLayout layoutScan = findViewById(R.id.layoutScan);
            etSearch = findViewById(R.id.etSearch);
            scrollView = findViewById(R.id.scrollView);
            rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
            tvPermissions = (TextView) findViewById(R.id.tvPermissions);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                if (objMyApplication.getListContacts() != null && objMyApplication.getListContacts().size() > 0) {
                    mobileArray = objMyApplication.getListContacts();
                } else {
                    mobileArray = getAllContacts();
                }
                prepareContacts();
            } else {
                requestPermission();
            }
            if (Utils.checkInternet(AddRecipientActivity.this)) {
                payViewModel.recentUsers();
                TemplateRequest request = new TemplateRequest();
                request.setBody1("[body1]");
                request.setBody2(objMyApplication.getStrUser());
                request.setBody3("www.coyni.com");
                payViewModel.getTemplate(Utils.inviteId, request);
            } else {
                Utils.displayAlert(getString(R.string.internet), AddRecipientActivity.this);
            }

            etSearch.addTextChangedListener(this);

            layoutScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        if (ContextCompat.checkSelfPermission(AddRecipientActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                            //ask for authorisation
//                            ActivityCompat.requestPermissions(AddRecipientActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
//                        else {
//                            //start your camera
//                            Intent i = new Intent(AddRecipientActivity.this, ScanActivity.class);
//                            startActivity(i);
//                        }
                        Intent i = new Intent(AddRecipientActivity.this, ScanActivity.class);
                        startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        payViewModel.getRegUsersResponseMutableLiveData().observe(this, new Observer<RegUsersResponse>() {
            @Override
            public void onChanged(RegUsersResponse regUsersResponse) {
                dialog.dismiss();
                if (regUsersResponse != null) {
                    bindContacts(regUsersResponse.getData());
                }
            }
        });

        payViewModel.getRecentUsersMutableLiveData().observe(this, new Observer<RecentUsers>() {
            @Override
            public void onChanged(RecentUsers recentUsers) {
                if (recentUsers != null) {
                    if (recentUsers.getData() != null && recentUsers.getData().size() > 0) {
                        bindRecentUsers(recentUsers.getData());
                    }
                }
            }
        });

        payViewModel.getTemplateResponseMutableLiveData().observe(this, new Observer<TemplateResponse>() {
            @Override
            public void onChanged(TemplateResponse templateResponse) {
                if (templateResponse != null) {
                    objMyApplication.setStrInvite(templateResponse.getData().getInviteBody());
                }
            }
        });

        payViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (apiError != null) {

                }
            }
        });
    }

    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                // show UI part if you want here to show some rationale !!!
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_CONTACTS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<Contacts> getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        List<Contacts> listContacts = new ArrayList<>();
        Contacts objContact;
        try {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    objContact = new Contacts();
                    List<String> lstNumbers = new ArrayList<>();
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    objContact.setId(id);
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                    objContact.setName(name);
                    nameList.add(name);
                    if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            objContact.setNumber(phoneNo);
                            if (!lstNumbers.contains(phoneNo.replace(" ", ""))) {
                                lstNumbers.add(phoneNo.replace(" ", ""));
                            }
                        }
                        objContact.setNumber(lstNumbers);
                        listContacts.add(objContact);
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listContacts;
    }

    private void bindContacts(List<RegUsersResponseData> contacts) {
        TextView tvNoRecords;
        ContactsAdapter contactsAdapter;
        try {
            tvNoRecords = (TextView) findViewById(R.id.tvNoRecords);
            if (contacts != null && contacts.size() > 0) {
                contactsAdapter = new ContactsAdapter(contacts, AddRecipientActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddRecipientActivity.this);
                rvContacts.setLayoutManager(mLayoutManager);
                rvContacts.setItemAnimator(new DefaultItemAnimator());
                rvContacts.setAdapter(contactsAdapter);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            String search_key = s.toString();
                            List<RegUsersResponseData> filterList = new ArrayList<>();
                            int sIndex = 0, wIndex = -1;
                            if (contacts != null && contacts.size() > 0) {
                                for (int i = 0; i < contacts.size(); i++) {
                                    wIndex = -1;
                                    sIndex = contacts.get(i).getUserName().toLowerCase().indexOf(search_key.toLowerCase());
                                    if (contacts.get(i).getWalletAddress() != null) {
                                        wIndex = contacts.get(i).getWalletAddress().toLowerCase().indexOf(search_key.toLowerCase());
                                    }
                                    if (sIndex == 0 || wIndex == 0) {
                                        filterList.add(contacts.get(i));
                                    }
                                }
                                if (filterList != null && filterList.size() > 0) {
                                    contactsAdapter.updateList(filterList);
                                    tvNoRecords.setVisibility(View.GONE);
                                    rvContacts.setVisibility(View.VISIBLE);
                                } else {
                                    tvNoRecords.setVisibility(View.VISIBLE);
                                    rvContacts.setVisibility(View.GONE);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void prepareContacts() {
        try {
            if (mobileArray != null && mobileArray.size() > 0) {
                RegisteredUsersRequest obj;
                List<RegisteredUsersRequest> listUsers = new ArrayList<>();
                String strPhone = "", strCCode = "";
                for (int i = 0; i < mobileArray.size(); i++) {
                    if (mobileArray.get(i).getNumber() != null && mobileArray.get(i).getNumber().size() == 1) {
                        obj = new RegisteredUsersRequest();
                        if (mobileArray.get(i).getNumber().get(0).startsWith("+1") || mobileArray.get(i).getNumber().get(0).startsWith("1")) {
                            strCCode = "US";
                        } else if (mobileArray.get(i).getNumber().get(0).startsWith("+91")) {
                            strCCode = "IN";
                        } else if (mobileArray.get(i).getNumber().get(0).startsWith("0")) {
                            strCCode = "IN";
                        } else {
//                            strCCode = "IN";
                            strCCode = "US";
                        }
                        strPhone = mobileArray.get(i).getNumber().get(0).replace("+91", "").replace("+1", "").replace("-", "");
                        if (strPhone.length() > 10 && strPhone.startsWith("0")) {
                            strPhone = strPhone.substring(1, strPhone.length());
                        } else if (strPhone.length() > 10 && strPhone.startsWith("1")) {
                            strPhone = strPhone.substring(1, strPhone.length());
                        }
                        obj.setCountryCode(strCCode);
                        obj.setPhoneNumber(strPhone);
                        obj.setUserName(mobileArray.get(i).getName());
                        if (strPhone.matches("[0-9]+")) {
                            listUsers.add(obj);
                        }
                    } else {
                        for (int j = 0; j < mobileArray.get(i).getNumber().size(); j++) {
                            obj = new RegisteredUsersRequest();
                            if (mobileArray.get(i).getNumber().get(j).startsWith("+1")) {
                                strCCode = "US";
                            } else if (mobileArray.get(i).getNumber().get(j).startsWith("+91")) {
                                strCCode = "IN";
                            } else if (mobileArray.get(i).getNumber().get(0).startsWith("0")) {
                                strCCode = "IN";
                            } else {
//                                strCCode = "IN";
                                strCCode = "US";
                            }
                            strPhone = mobileArray.get(i).getNumber().get(j).replace("+91", "").replace("+1", "").replace("-", "");
                            if (strPhone.length() > 10 && strPhone.startsWith("0")) {
                                strPhone = strPhone.substring(1, strPhone.length());
                            } else if (strPhone.length() > 10 && strPhone.startsWith("1")) {
                                strPhone = strPhone.substring(1, strPhone.length());
                            }
                            obj.setCountryCode(strCCode);
                            obj.setPhoneNumber(strPhone);
                            obj.setUserName(mobileArray.get(i).getName());
                            if (strPhone.matches("[0-9]+")) {
                                listUsers.add(obj);
                            }

                        }
                    }
                }
                //Utils.copyAlert(jsonArray.toString(), AddRecipientActivity.this);
                if (listUsers != null && listUsers.size() > 0) {
                    if (Utils.checkInternet(AddRecipientActivity.this)) {
                        dialog = new ProgressDialog(AddRecipientActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        payViewModel.registeredUsers(listUsers);
                    } else {
                        Utils.displayAlert(getString(R.string.internet), AddRecipientActivity.this);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void bindRecentUsers(List<RecentUsersData> users) {
        RecyclerView rvRecent;
        RecentUsersAdapter recentUsersAdapter;
        try {
            if (users != null && users.size() > 0) {
                rvRecent = (RecyclerView) findViewById(R.id.rvRecent);
                recentUsersAdapter = new RecentUsersAdapter(users, AddRecipientActivity.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(AddRecipientActivity.this, LinearLayoutManager.HORIZONTAL, false);
                rvRecent.setLayoutManager(layoutManager);
                rvRecent.setItemAnimator(new DefaultItemAnimator());
                rvRecent.setAdapter(recentUsersAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}