package com.coyni.mapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.coyni.mapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetLimitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class
SetLimitFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    EditText inputEV;
    private String strAmount=new String();
    private TextView keyOne,keyTwo,keyThree,keyFour,keyFive,keySix,keySeven,keyEight,keyNine,keyZero,keyDot,keyActionText;
    private LinearLayout keyBack,keyAction;
    private SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetLimitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetLimitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetLimitFragment newInstance(String param1, String param2) {
        SetLimitFragment fragment = new SetLimitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_limit, container, false);
        try {
            inputEV = view.findViewById(R.id.setAmountET);


            keyOne=view.findViewById(R.id.keyOneTV);
            keyOne.setOnClickListener(this);

            keyTwo=view.findViewById(R.id.keyTwoTV);
            keyTwo.setOnClickListener(this);

            keyThree=view.findViewById(R.id.keyThreeTV);
            keyThree.setOnClickListener(this);

            keyFour=view.findViewById(R.id.keyFourTV);
            keyFour.setOnClickListener(this);

            keyFive=view.findViewById(R.id.keyFiveTV);
            keyFive.setOnClickListener(this);

            keySix=view.findViewById(R.id.keySixTV);
            keySix.setOnClickListener(this);

            keySeven=view.findViewById(R.id.keySevenTV);
            keySeven.setOnClickListener(this);

            keyEight=view.findViewById(R.id.keyEightTV);
            keyEight.setOnClickListener(this);

            keyNine=view.findViewById(R.id.keyNineTV);
            keyNine.setOnClickListener(this);

            keyZero=view.findViewById(R.id.keyZeroTV);
            keyZero.setOnClickListener(this);

            keyDot=view.findViewById(R.id.keyDotTV);
            keyDot.setOnClickListener(this);

            keyBack=view.findViewById(R.id.keyBackLL);
            keyBack.setOnClickListener(this);

            keyAction=view.findViewById(R.id.keyActionLL);
            keyAction.setOnClickListener(this);

            keyActionText=view.findViewById(R.id.keyActionTV);
            keyValues.put(R.id.keyActionLL,"");

            inputEV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length()>=5&&!inputEV.getText().toString().contains(".")){
                            inputEV.setTextSize(50);
//                            inputEV.setMaxHeight(100);
                        }
                        else {
                            inputEV.setTextSize(80);
//                            inputEV.setMaxHeight(150);
                        }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyZeroTV:
                strAmount+=0;
                inputEV.setText(strAmount);
                break;
            case R.id.keyOneTV:
                strAmount+=1;
                inputEV.setText(strAmount);
                break;
            case R.id.keyTwoTV:
                strAmount+=2;
                inputEV.setText(strAmount);
                break;
            case R.id.keyThreeTV:
                strAmount+=3;
                inputEV.setText(strAmount);
                break;
            case R.id.keyFourTV:
                strAmount+=4;
                inputEV.setText(strAmount);
                break;
            case R.id.keyFiveTV:
                strAmount+=5;
                inputEV.setText(strAmount);
                break;
            case R.id.keySixTV:
                strAmount+=6;
                inputEV.setText(strAmount);
                break;
            case R.id.keySevenTV:
                strAmount+=7;
                inputEV.setText(strAmount);
                break;
            case R.id.keyEightTV:
                strAmount+=8;
                inputEV.setText(strAmount);
                break;
            case R.id.keyNineTV:
                strAmount+=9;
                inputEV.setText(strAmount);
                break;
            case R.id.keyDotTV:
                if (!strAmount.contains(".")){
                    strAmount=strAmount+".";
                    inputEV.setText(strAmount);
                }
                break;
            case R.id.keyBackLL:
                if (strAmount.length()>0){
                    strAmount=strAmount.substring(0,strAmount.length()-1);
                    inputEV.setText(strAmount);
                }

                break;

        }

    }



    public void setInputConnection(InputConnection ic){
        inputConnection=ic;
    }

    public void setKeyAction(String actionName){
        keyActionText.setText(actionName);
    }

}