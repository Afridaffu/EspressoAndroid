package com.coyni.mapp.view;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.ContactsAdapter;
import com.coyni.mapp.adapters.CoyniUsersAdapter;
import com.coyni.mapp.adapters.RecentUsersAdapter;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.coyniusers.CoyniUsers;
import com.coyni.mapp.model.coyniusers.CoyniUsersData;
import com.coyni.mapp.model.recentusers.RecentUsers;
import com.coyni.mapp.model.recentusers.RecentUsersData;
import com.coyni.mapp.model.reguser.Contacts;
import com.coyni.mapp.model.reguser.RegUsersResponse;
import com.coyni.mapp.model.reguser.RegUsersResponseData;
import com.coyni.mapp.model.reguser.RegisteredUsersRequest;
import com.coyni.mapp.model.templates.TemplateRequest;
import com.coyni.mapp.model.templates.TemplateResponse;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.PayViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AddRecipientActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    public static final int REQUEST_READ_CONTACTS = 79;
    List<Contacts> mobileArray;
    MyApplication objMyApplication;
    PayViewModel payViewModel;
    Dialog dialog;
    RecyclerView rvContacts, rvCoyniUsers, rvRecent;
    List<RecentUsersData> usersList;
    List<CoyniUsersData> listCoyniUsers;
    TextView tvRecentUsers, tvSearchUsers, tvCoyniUsers, tvContactMsg, tvNoContacts;
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
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
            setKeyboardVisibilityListener(AddRecipientActivity.this);
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
            tvNoContacts = findViewById(R.id.tvNoContacts);
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
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(AddRecipientActivity.this);
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
                        boolean sIndex = false, wIndex = false, ecoIndex = false;
                        if (contactsList != null && contactsList.size() > 0) {
                            for (int i = 0; i < contactsList.size(); i++) {
                                wIndex = false;
                                ecoIndex = false;
//                                sIndex = contactsList.get(i).getUserName().toLowerCase().startsWith(search_key.toLowerCase());
                                sIndex = contactsList.get(i).getUserName().toLowerCase().contains(search_key.toLowerCase());
                                if (contactsList.get(i).getWalletAddress() != null) {
//                                    wIndex = contactsList.get(i).getWalletAddress().toLowerCase().startsWith(search_key.toLowerCase());
                                    wIndex = contactsList.get(i).getWalletAddress().toLowerCase().contains(search_key.toLowerCase());
                                }
                                if (contactsList.get(i).getFullName() != null && !contactsList.get(i).getFullName().equals("")) {
                                    ecoIndex = contactsList.get(i).getFullName().toLowerCase().contains(search_key.toLowerCase());
                                }
//                                if (sIndex || wIndex) {
                                if (sIndex || wIndex || ecoIndex) {
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
                                wIndex = false;
                                ecoIndex = false;
//                                sIndex = usersList.get(i).getUserName().toLowerCase().startsWith(search_key.toLowerCase());
                                sIndex = usersList.get(i).getUserName().toLowerCase().contains(search_key.toLowerCase());
                                if (usersList.get(i).getWalletAddress() != null) {
//                                    wIndex = usersList.get(i).getWalletAddress().toLowerCase().startsWith(search_key.toLowerCase());
                                    wIndex = usersList.get(i).getWalletAddress().toLowerCase().contains(search_key.toLowerCase());
                                }
                                if (objMyApplication.getObjPhContacts().containsKey(usersList.get(i).getPhoneNumber().replace("(1)", ""))) {
//                                    ecoIndex = objMyApplication.getObjPhContacts().get(usersList.get(i).getPhoneNumber().replace("(1)", "")).getUserName().toLowerCase().contains(search_key.toLowerCase());
                                    ecoIndex = objMyApplication.getObjPhContacts().get(usersList.get(i).getPhoneNumber().replace("(1)", "")).getFirstName().toLowerCase().contains(search_key.toLowerCase());
                                    if (!ecoIndex) {
                                        ecoIndex = objMyApplication.getObjPhContacts().get(usersList.get(i).getPhoneNumber().replace("(1)", "")).getLastName().toLowerCase().contains(search_key.toLowerCase());
                                    }
                                }
//                                if (sIndex || wIndex) {
                                if (sIndex || wIndex || ecoIndex) {
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
                if (dialog != null) {
                    dialog.dismiss();
                }
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

                Collections.sort(usersList, new Comparator<RecentUsersData>() {
                    @Override
                    public int compare(RecentUsersData o1, RecentUsersData o2) {
                        return o1.getUserName().compareTo(o2.getUserName());
                    }
                });
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
//                    if (!users.get(i).getWalletId().equals(objMyApplication.getGbtWallet().getWalletId())) {
                    if (!users.get(i).getWalletId().equals(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getWalletId())) {
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
        showProgressDialog();
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
//                    objContact.setName(name);
                    String strName = getFLName(id);
                    if (strName.contains(";")) {
                        objContact.setFirstName(strName.split(";")[0]);
                        try {
                            objContact.setLastName(strName.split(";")[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            objContact.setLastName("");
                        }
                    } else {
                        objContact.setFirstName(strName);
                        objContact.setLastName("");
                    }
//                    nameList.add(name);
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
        dismissDialog();
        return listContacts;
    }

    private String getFLName(String contactId) {
        String strName = "";
        try {
            String[] projection = {ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1, ContactsContract.Data.DATA2, ContactsContract.Data.DATA3};
//            String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " +
//                    ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "', '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "')";
            String selection = ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " +
                    ContactsContract.Data.MIMETYPE + " IN ('" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "')";
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);

            while (cur.moveToNext()) {
                String mime = cur.getString(2); // type of data (phone / birthday / email)
                switch (mime) {
                    case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                        strName = cur.getString(4);
                        if (cur.getString(5) != null) {
                            strName = strName + ";" + cur.getString(5);
                        }
                        break;
                }
            }
            cur.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strName;
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
//                        obj.setUserName(mobileArray.get(i).getName());
                        obj.setFirstName(mobileArray.get(i).getFirstName());
                        obj.setLastName(mobileArray.get(i).getLastName());
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
//                            obj.setUserName(mobileArray.get(i).getName());
                            obj.setFirstName(mobileArray.get(i).getFirstName());
                            obj.setLastName(mobileArray.get(i).getLastName());
                            obj.setImagePath(mobileArray.get(i).getPhoto());
                            listUsers.add(obj);
                            objUsers.put(obj.getPhoneNumber(), obj);
                        }
                    }
                }
                objMyApplication.setObjPhContacts(objUsers);
//                payViewModel.recentUsers();
                if (listUsers != null && listUsers.size() > 0) {
                    if (Utils.checkInternet(AddRecipientActivity.this)) {
                        payViewModel.registeredUsers(listUsers);
                    } else {
                        Utils.displayAlert(getString(R.string.internet), AddRecipientActivity.this, "", "");
                    }
                }
            } else {
                rvContacts.setVisibility(View.GONE);
                tvContactMsg.setVisibility(View.GONE);
                tvNoContacts.setVisibility(View.VISIBLE);
            }
            payViewModel.recentUsers();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindContacts(List<RegUsersResponseData> contacts) {
        try {
            contactsList = new ArrayList<>();
            List<RegUsersResponseData> wWAList = new ArrayList<>();
            List<RegUsersResponseData> nWAList = new ArrayList<>();
            String strUserPh = "";
            if (objMyApplication.getMyProfile().getData().getPhoneNumber() != null && !objMyApplication.getMyProfile().getData().getPhoneNumber().equals("")) {
                strUserPh = objMyApplication.getMyProfile().getData().getPhoneNumber().split(" ")[1];
            } else {
                strUserPh = "";
            }
            if (contacts != null && contacts.size() > 0) {
                for (int i = 0; i < contacts.size(); i++) {
                    if (!strUserPh.equals(contacts.get(i).getPhoneNumber())) {
                        if (contacts.get(i).getWalletAddress() != null && !contacts.get(i).getWalletAddress().equals(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getWalletId())) {
                            wWAList.add(contacts.get(i));
                        } else if (contacts.get(i).getWalletAddress() == null) {
                            nWAList.add(contacts.get(i));
                        }
                    }
                }


                Collections.sort(wWAList, new Comparator<RegUsersResponseData>() {
                    @Override
                    public int compare(RegUsersResponseData o1, RegUsersResponseData o2) {
                        return o1.getUserName().compareTo(o2.getUserName());
                    }
                });

                Collections.sort(nWAList, new Comparator<RegUsersResponseData>() {
                    @Override
                    public int compare(RegUsersResponseData o1, RegUsersResponseData o2) {
                        return o1.getUserName().compareTo(o2.getUserName());
                    }
                });

                if (wWAList != null && wWAList.size() > 0) {
                    contactsList.addAll(wWAList);
                }

                if (nWAList != null && nWAList.size() > 0) {
                    contactsList.addAll(nWAList);
                }

                rvContacts.setVisibility(View.VISIBLE);
                tvContactMsg.setVisibility(View.VISIBLE);
                tvNoContacts.setVisibility(View.GONE);

                contactsAdapter = new ContactsAdapter(contactsList, AddRecipientActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AddRecipientActivity.this);
                rvContacts.setLayoutManager(mLayoutManager);
                rvContacts.setItemAnimator(new DefaultItemAnimator());
                rvContacts.setAdapter(contactsAdapter);
            } else {
                rvContacts.setVisibility(View.GONE);
                tvContactMsg.setVisibility(View.GONE);
                tvNoContacts.setVisibility(View.VISIBLE);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

}