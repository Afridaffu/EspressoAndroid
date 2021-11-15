package com.greenbox.coyni.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.greenbox.coyni.R;

public class FaceIdDisabled_BottomSheet extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static Boolean isTouchId = false, isFaceId = false;

    public FaceIdDisabled_BottomSheet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaceIdNotAvailable_BottomSheet.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceIdDisabled_BottomSheet newInstance(Boolean isTouch, Boolean isFace) {
        FaceIdDisabled_BottomSheet fragment = new FaceIdDisabled_BottomSheet();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        isTouchId = isTouch;
        isFaceId = isFace;
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
        View view = inflater.inflate(R.layout.fragment_face_id_disabled__bottom_sheet, container, false);
        TextView tvHead = view.findViewById(R.id.tvHead);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        CardView cvOK = view.findViewById(R.id.cvOK);
        if (isFaceId) {
            tvHead.setText("Face ID Temporarily disabled");
        } else if (isTouchId) {
            tvHead.setText("Touch ID Temporarily disabled");
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
