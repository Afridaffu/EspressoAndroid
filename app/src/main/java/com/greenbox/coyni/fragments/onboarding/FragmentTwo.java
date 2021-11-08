package com.greenbox.coyni.fragments.onboarding;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.greenbox.coyni.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTwo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTwo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentTwo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentTwo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTwo newInstance(String param1, String param2) {
        FragmentTwo fragment = new FragmentTwo();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        TextView textOne  = view.findViewById(R.id.oneTV);
        TextView textTwo  = view.findViewById(R.id.twoTV);
        TextView textThree  = view.findViewById(R.id.threeTV);


        makeSpannableString("Stable coin token (CYN) backed by the U.S. dollar on a 1:1 ratio",55,64,textOne);
        makeSpannableString("Send and request payments without fees",26,38,textTwo);
        makeSpannableString("Generate QR codes for specific values to make secure payments",9,17,textThree);

        return view;

    }

    private void makeSpannableString(String text, int start, int end, TextView textView){
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary_color)), start,end,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), start,end,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(spannable);
    }
}