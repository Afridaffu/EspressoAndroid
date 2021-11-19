package com.greenbox.coyni.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaceIdNotAvailable_BottomSheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceIdNotAvailable_BottomSheet extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FaceIdNotAvailable_BottomSheet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaceIdNotAvailable_BottomSheet.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceIdNotAvailable_BottomSheet newInstance() {
        FaceIdNotAvailable_BottomSheet fragment = new FaceIdNotAvailable_BottomSheet();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
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
//        return inflater.inflate(R.layout.fragment_face_id_not_available__bottom_sheet, container, false);
        View view = inflater.inflate(R.layout.fragment_face_id_not_available__bottom_sheet, container, false);
        TextView tvHead = view.findViewById(R.id.tvHead);
        CardView cvOK = view.findViewById(R.id.cvOK);
        if (Utils.getIsTouchEnabled()) {
            tvHead.setText("Touch ID Not available");
        } else if (Utils.getIsFaceEnabled()) {
            tvHead.setText("Face ID Not available");
        }
        cvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

}