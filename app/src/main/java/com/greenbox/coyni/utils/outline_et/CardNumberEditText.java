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
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AddCardActivity;
import com.greenbox.coyni.view.CreateAccountActivity;
import com.greenbox.coyni.view.RetrieveEmailActivity;

public class CardNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private EditText cnET;
    private ImageView imgCardType, readCardIV;
    boolean isPhoneError = false;

    public String cardType = "";

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
        readCardIV = findViewById(R.id.readCardIV);
        cnET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try {
                    if (b) {
                        hintName.setTextColor(getResources().getColor(R.color.primary_color));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        if ((cnET.getText().length() > 0)) {
                            if (!cardType.toLowerCase().contains("american") && !cnET.getText().toString().equals("") && cnET.getText().toString().length() < 19) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                                AddCardActivity.addCardActivity.cardErrorTV.setText("Invalid Card Number");
                            } else if (cardType.toLowerCase().contains("american") && !cnET.getText().toString().equals("") && cnET.getText().toString().length() != 18) {
                                hintName.setTextColor(getResources().getColor(R.color.error_red));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                                AddCardActivity.addCardActivity.cardErrorTV.setText("Invalid Card Number");
                            }
                        } else if ((cnET.getText().length() == 0)) {
                            hintName.setTextColor(getResources().getColor(R.color.error_red));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                            AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                            AddCardActivity.addCardActivity.cardErrorTV.setText("Field Required");
                        } else {
                            hintName.setTextColor(getResources().getColor(R.color.primary_black));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                            AddCardActivity.addCardActivity.isCard = true;
                        }
                    } else if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 20) {
                        AddCardActivity.addCardActivity.isCard = true;
                        AddCardActivity.addCardActivity.cardErrorLL.setVisibility(GONE);
                        hintName.setTextColor(getResources().getColor(R.color.primary_color));
                        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                    } else {
                        AddCardActivity.addCardActivity.isCard = false;
                    }
                    AddCardActivity.addCardActivity.enableOrDisableNext();
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
                        imgCardType.setImageResource(R.drawable.ic_visa_inactive);
                        AddCardActivity.addCardActivity.clearControls();
                    }
                    String str = cnET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        cnET.setText("");
                        cnET.setSelection(cnET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        cnET.setText(cnET.getText().toString().replaceAll("\\.", ""));
                        cnET.setSelection(cnET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        cnET.setText("");
                        cnET.setSelection(cnET.getText().length());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

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
                cardType = cardBrand;
                imgCardType.setVisibility(VISIBLE);
                if (cardBrand.toLowerCase().equals("visa")) {
                    imgCardType.setImageResource(R.drawable.ic_visa1);
                } else if (cardBrand.toLowerCase().contains("master")) {
                    imgCardType.setImageResource(R.drawable.ic_master);
                } else if (cardBrand.toLowerCase().contains("american")) {
                    imgCardType.setImageResource(R.drawable.ic_amex);
                } else if (cardBrand.toLowerCase().contains("discover")) {
                    imgCardType.setImageResource(R.drawable.ic_discover);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ImageView getCardReaderIVRef() {
        return readCardIV;
    }

    public void disableEditText() {
        cnET.setEnabled(false);
    }
}