package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.cards.CardTypeRequest;
import com.greenbox.coyni.view.AddCardActivity;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.greenbox.coyni.view.RetrieveEmailActivity;

public class CardNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private EditText cnET;
    private ImageView imgCardType;
    boolean isPhoneError = false;

    public String FROM = "";

    public CardNumberEditText(Context context) {
        this(context, null, 0);
    }

    public CardNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardNumberEditText(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.card_number_layout, this, true);
        hintName = findViewById(R.id.hintTV);
        hintHolder = findViewById(R.id.hintdHolderLL);
        cnET = findViewById(R.id.pnET);
        imgCardType = findViewById(R.id.imgCardType);
        cnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
            }
        });

        cnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (i2 > 2) {
                        if (charSequence != null && charSequence.length() >= 15) {
                            AddCardActivity.addCardActivity.getCardype(charSequence.toString());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.length() == 7) {
                        AddCardActivity.addCardActivity.getCardype(s.toString());
                    } else if (s.toString().trim().length() == 0) {
                        imgCardType.setVisibility(View.GONE);
                        AddCardActivity.addCardActivity.clearControls();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void setErrorOutlineBox() {
        isPhoneError = true;
        hintName.setTextColor(getResources().getColor(R.color.error_red));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
    }


    public void setFrom(String fromm) {
        FROM = fromm;
    }


    public void setText(String text) {
        cnET.setText(text);
    }

    public String getText() {
        return cnET.getText().toString().trim();
    }

    public void setImage(String cardBrand) {
        try {
            if (!cardBrand.equals("")) {
                imgCardType.setVisibility(VISIBLE);
                if (cardBrand.toLowerCase().equals("visa")) {
                    imgCardType.setImageResource(R.drawable.ic_visaactive);
                } else if (cardBrand.toLowerCase().contains("master")) {
                    imgCardType.setImageResource(R.drawable.ic_masteractive);
                } else if (cardBrand.toLowerCase().contains("american")) {
                    imgCardType.setImageResource(R.drawable.ic_amexactive);
                } else if (cardBrand.toLowerCase().contains("discover")) {
                    imgCardType.setImageResource(R.drawable.ic_discoveractive);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}