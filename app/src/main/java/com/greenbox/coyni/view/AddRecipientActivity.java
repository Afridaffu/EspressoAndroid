package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.ContactsAdapter;
import com.greenbox.coyni.adapters.CoyniUsersAdapter;
import com.greenbox.coyni.adapters.RecentUsersAdapter;
import com.greenbox.coyni.model.coyniusers.CoyniUsers;
import com.greenbox.coyni.model.coyniusers.CoyniUsersData;
import com.greenbox.coyni.model.recentusers.RecentUsers;
import com.greenbox.coyni.model.recentusers.RecentUsersData;
import com.greenbox.coyni.model.reguser.Contacts;
import com.greenbox.coyni.model.reguser.RegUsersResponse;
import com.greenbox.coyni.model.reguser.RegUsersResponseData;
import com.greenbox.coyni.model.reguser.RegisteredUsersRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.PayViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddRecipientActivity extends AppCompatActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    List<Contacts> mobileArray;
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    ProgressDialog dialog;
    RecyclerView rvContacts, rvCoyniUsers, rvRecent;
    List<RecentUsersData> usersList;
    List<CoyniUsersData> listCoyniUsers;
    TextView tvRecentUsers, tvSearchUsers, tvCoyniUsers, tvContactMsg;
    RecentUsersAdapter recentUsersAdapter;
    CoyniUsersAdapter coyniUsersAdapter;
    ContactsAdapter contactsAdapter;
    List<RegisteredUsersRequest> listUsers;
    List<RegUsersResponseData> contactsList;
    LinearLayout lyPayClose, lyCoyniUsers, lyRecentUsers, lyContacts;
    Long mLastClickTime = 0L;
    ImageView imgScan;
    EditText etSearch;
    String strWalletId = "", search_text = "";

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

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            rvRecent = findViewById(R.id.rvRecent);
            rvContacts = findViewById(R.id.rvContacts);
            rvCoyniUsers = findViewById(R.id.rvCoyniUsers);
            tvRecentUsers = findViewById(R.id.tvRecentUsers);
            tvSearchUsers = findViewById(R.id.tvSearchUsers);
            tvCoyniUsers = findViewById(R.id.tvCoyniUsers);
            lyPayClose = findViewById(R.id.lyPayClose);
            lyRecentUsers = findViewById(R.id.lyRecentUsers);
            lyContacts = findViewById(R.id.lyContacts);
            lyCoyniUsers = findViewById(R.id.lyCoyniUsers);
            tvContactMsg = findViewById(R.id.tvContactMsg);
            imgScan = findViewById(R.id.imgScan);
            etSearch = findViewById(R.id.etSearch);
            if (Utils.checkInternet(AddRecipientActivity.this)) {
//                payViewModel.recentUsers();
                TemplateRequest request = new TemplateRequest();
                request.setBody1("[body1]");
                request.setBody2(objMyApplication.getStrUserName());
                request.setBody3("www.coyni.com");
                payViewModel.getTemplate(Utils.inviteId, request);
            } else {
                Utils.displayAlert(getString(R.string.internet), AddRecipientActivity.this, "", "");
            }
            hideContactsSection();

            lyPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            imgScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(AddRecipientActivity.this, ScanActivity.class));
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
                        List<RegUsersResponseData> filterList = new ArrayList<>();
                        List<RecentUsersData> filtersRecentList = new ArrayList<>();
                        int sIndex = 0, wIndex = -1;
                        if (contactsList != null && contactsList.size() > 0) {
                            for (int i = 0; i < contactsList.size(); i++) {
                                wIndex = -1;
                                sIndex = contactsList.get(i).getUserName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (contactsList.get(i).getWalletAddress() != null) {
                                    wIndex = contactsList.get(i).getWalletAddress().toLowerCase().indexOf(search_key.toLowerCase());
                                }
                                if (sIndex >= 0 || wIndex == 0) {
                                    filterList.add(contactsList.get(i));
                                }
                            }
                            if (filterList != null && filterList.size() > 0) {
                                contactsAdapter.updateList(filterList);
                                lyContacts.setVisibility(View.VISIBLE);
                                tvSearchUsers.setVisibility(View.GONE);
                            } else {
                                lyContacts.setVisibility(View.GONE);
                            }
                        }
                        if (usersList != null && usersList.size() > 0) {
                            for (int i = 0; i < usersList.size(); i++) {
                                wIndex = -1;
                                sIndex = usersList.get(i).getUserName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (usersList.get(i).getWalletAddress() != null) {
                                    wIndex = usersList.get(i).getWalletAddress().toLowerCase().indexOf(search_key.toLowerCase());
                                }
                                if (sIndex >= 0 || wIndex == 0) {
                                    filtersRecentList.add(usersList.get(i));
                                }
                            }

                            if (filtersRecentList != null && filtersRecentList.size() > 0) {
                                recentUsersAdapter.updateList(filtersRecentList);
                                tvRecentUsers.setVisibility(View.GONE);
                                rvRecent.setVisibility(View.VISIBLE);
                                lyRecentUsers.setVisibility(View.VISIBLE);
                                tvSearchUsers.setVisibility(View.GONE);
                            } else {
                                tvRecentUsers.setVisibility(View.VISIBLE);
                                rvRecent.setVisibility(View.GONE);
                                lyRecentUsers.setVisibility(View.GONE);
                            }
                        }
                        search_text = search_key;
                        if (!search_key.trim().equals("") && search_key.trim().length() > 2) {
                            //lyCoyniUsers.setVisibility(View.VISIBLE);
                            tvSearchUsers.setVisibility(View.GONE);
                            payViewModel.getCoyniUsers(search_key.toLowerCase());
                        } else {
                            lyCoyniUsers.setVisibility(View.GONE);
                            if (lyRecentUsers.getVisibility() == View.GONE
                                    && lyContacts.getVisibility() == View.GONE && lyCoyniUsers.getVisibility() == View.GONE) {
                                tvSearchUsers.setVisibility(View.VISIBLE);
                            } else {
                                tvSearchUsers.setVisibility(View.GONE);
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

        payViewModel.getTemplateResponseMutableLiveData().observe(this, new Observer<TemplateResponse>() {
            @Override
            public void onChanged(TemplateResponse templateResponse) {
                if (templateResponse != null) {
                    objMyApplication.setStrInvite(templateResponse.getData().getInviteBody());
                }
            }
        });

        payViewModel.getRecentUsersMutableLiveData().observe(this, new Observer<RecentUsers>() {
            @Override
            public void onChanged(RecentUsers recentUsers) {
                if (recentUsers != null) {
                    if (recentUsers.getData() != null && recentUsers.getData().size() > 0) {
                        lyRecentUsers.setVisibility(View.VISIBLE);
                        tvRecentUsers.setVisibility(View.GONE);
                        rvRecent.setVisibility(View.VISIBLE);
                        bindRecentUsers(recentUsers.getData());
                    } else {
                        lyRecentUsers.setVisibility(View.GONE);
//                        tvRecentUsers.setVisibility(View.VISIBLE);
//                        rvRecent.setVisibility(View.GONE);
                    }
                } else {
                    tvRecentUsers.setVisibility(View.VISIBLE);
                    rvRecent.setVisibility(View.GONE);
                }
            }
        });

        payViewModel.getCoyniUsersMutableLiveData().observe(this, new Observer<CoyniUsers>() {
            @Override
            public void onChanged(CoyniUsers coyniUsers) {
                if (coyniUsers != null) {
                    if (coyniUsers.getData() != null && coyniUsers.getData().size() > 0) {
                        if (!search_text.equals("")) {
                            lyCoyniUsers.setVisibility(View.VISIBLE);
                            rvCoyniUsers.setVisibility(View.VISIBLE);
                            tvCoyniUsers.setVisibility(View.GONE);
                            bindCoyniUsers(coyniUsers.getData());
                        }
                    } else {
                        lyCoyniUsers.setVisibility(View.GONE);
                    }
                    if (lyRecentUsers.getVisibility() == View.GONE
                            && lyContacts.getVisibility() == View.GONE
                            && lyCoyniUsers.getVisibility() == View.GONE) {
                        tvSearchUsers.setVisibility(View.VISIBLE);
                    } else {
                        tvSearchUsers.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void hideContactsSection() {
        try {
            if (objMyApplication.getContactPermission()) {
                lyContacts.setVisibility(View.VISIBLE);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    dialog = Utils.showProgressDialog(AddRecipientActivity.this);
                    if (objMyApplication.getListContacts() != null && objMyApplication.getListContacts().size() > 0) {
                        mobileArray = objMyApplication.getListContacts();
                    } else {
                        mobileArray = getAllContacts();
                        objMyApplication.setListContacts(mobileArray);
                    }
                    prepareContacts();
                }
            } else {
                lyContacts.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindRecentUsers(List<RecentUsersData> users) {
        usersList = new ArrayList<>();
        try {
            if (users != null && users.size() > 0) {
                if (users.size() > 5) {
                    for (int i = 0; i < users.size(); i++) {
                        if (i < 5) {
                            usersList.add(users.get(i));
                        }
                    }
                } else {
                    usersList.addAll(users);
                }

                recentUsersAdapter = new RecentUsersAdapter(usersList, AddRecipientActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddRecipientActivity.this);
                rvRecent.setLayoutManager(mLayoutManager);
                rvRecent.setItemAnimator(new DefaultItemAnimator());
                rvRecent.setAdapter(recentUsersAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindCoyniUsers(List<CoyniUsersData> users) {
        listCoyniUsers = new ArrayList<>();
        try {
            if (users != null && users.size() > 0) {
                for (int i = 0; i < users.size(); i++) {
                    if (!users.get(i).getWalletId().equals(objMyApplication.getGbtWallet().getWalletId())) {
                        listCoyniUsers.add(users.get(i));
                    }
                }

                coyniUsersAdapter = new CoyniUsersAdapter(listCoyniUsers, AddRecipientActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddRecipientActivity.this);
                rvCoyniUsers.setLayoutManager(mLayoutManager);
                rvCoyniUsers.setItemAnimator(new DefaultItemAnimator());
                rvCoyniUsers.setAdapter(coyniUsersAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("Range")
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
                    @SuppressLint("Range") String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    objContact.setId(id);
                    @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex(
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
                            if (!lstNumbers.contains(phoneNo.replace(" ", "").replace("-", ""))) {
                                lstNumbers.add(phoneNo.replace(" ", "").replace("-", ""));
                            }
                        }
                        objContact.setNumber(lstNumbers);
                        pCur.close();
                    }
                    String image_uri = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    objContact.setPhoto(image_uri);
                    listContacts.add(objContact);
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

    private void prepareContacts() {
        try {
            if (mobileArray != null && mobileArray.size() > 0) {
                RegisteredUsersRequest obj;
                listUsers = new ArrayList<>();
                String strPhone = "", strCCode = "";
                HashMap<String, RegisteredUsersRequest> objUsers = new HashMap<>();
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
                            strCCode = "US";
                        }
                        strPhone = mobileArray.get(i).getNumber().get(0).replace("-", "");
                        obj.setCountryCode(strCCode);
                        obj.setPhoneNumber(strPhone);
                        obj.setUserName(mobileArray.get(i).getName());
                        obj.setImagePath(mobileArray.get(i).getPhoto());
                        listUsers.add(obj);
                        objUsers.put(obj.getPhoneNumber(), obj);
                    } else if (mobileArray.get(i).getNumber() != null) {
                        for (int j = 0; j < mobileArray.get(i).getNumber().size(); j++) {
                            obj = new RegisteredUsersRequest();
                            if (mobileArray.get(i).getNumber().get(j).startsWith("+1")) {
                                strCCode = "US";
                            } else if (mobileArray.get(i).getNumber().get(j).startsWith("+91")) {
                                strCCode = "IN";
                            } else if (mobileArray.get(i).getNumber().get(0).startsWith("0")) {
                                strCCode = "IN";
                            } else {
                                strCCode = "US";
                            }
                            strPhone = mobileArray.get(i).getNumber().get(j).replace("-", "");
                            obj.setCountryCode(strCCode);
                            obj.setPhoneNumber(strPhone);
                            obj.setUserName(mobileArray.get(i).getName());
                            obj.setImagePath(mobileArray.get(i).getPhoto());
                            listUsers.add(obj);
                            objUsers.put(obj.getPhoneNumber(), obj);
                        }
                    }
                }
                objMyApplication.setObjPhContacts(objUsers);
                payViewModel.recentUsers();
                if (listUsers != null && listUsers.size() > 0) {
                    if (Utils.checkInternet(AddRecipientActivity.this)) {
                        payViewModel.registeredUsers(listUsers);
                    } else {
                        Utils.displayAlert(getString(R.string.internet), AddRecipientActivity.this, "", "");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindContacts(List<RegUsersResponseData> contacts) {
        try {
            contactsList = new ArrayList<>();
            List<RegUsersResponseData> wWAList = new ArrayList<>();
            List<RegUsersResponseData> nWAList = new ArrayList<>();
            if (contacts != null && contacts.size() > 0) {
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getWalletAddress() != null && !contacts.get(i).getWalletAddress().equals(objMyApplication.getGbtWallet().getWalletId())) {
//                        contactsList.add(contacts.get(i));
                        wWAList.add(contacts.get(i));
                    } else if (contacts.get(i).getWalletAddress() == null) {
//                        contactsList.add(contacts.get(i));
                        nWAList.add(contacts.get(i));
                    }
                }
                if (wWAList != null && wWAList.size() > 0) {
                    contactsList.addAll(wWAList);
                }
                if (nWAList != null && nWAList.size() > 0) {
                    contactsList.addAll(nWAList);
                }

                contactsAdapter = new ContactsAdapter(contactsList, AddRecipientActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddRecipientActivity.this);
                rvContacts.setLayoutManager(mLayoutManager);
                rvContacts.setItemAnimator(new DefaultItemAnimator());
                rvContacts.setAdapter(contactsAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}