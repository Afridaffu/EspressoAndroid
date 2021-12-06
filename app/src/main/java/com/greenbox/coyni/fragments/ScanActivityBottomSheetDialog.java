package com.greenbox.coyni.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.greenbox.coyni.R;
import com.greenbox.coyni.view.PayRequestTransactionActivity;

public class ScanActivityBottomSheetDialog extends BottomSheetDialogFragment {

    TextInputEditText addnoteET;
    CardView doneBtn;
    LinearLayout closeBtn;
    public ScanActivityBottomSheetDialog(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.add_note_layout,container,false);
        addnoteET=view.findViewById(R.id.addNoteET);
        doneBtn=view.findViewById(R.id.doneBtn);
        closeBtn=view.findViewById(R.id.cancelBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String addN=addnoteET.getText().toString();
               PayRequestTransactionActivity tra=(PayRequestTransactionActivity) getActivity();
               tra.getValue(addN);
               dismiss();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        return view;
    }
}
