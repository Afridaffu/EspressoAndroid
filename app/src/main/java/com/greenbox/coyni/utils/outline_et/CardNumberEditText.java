package com.greenbox.coyni.utils.outline_et;

import android.content.Context;
import android.graphics.Color;
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
import com.greenbox.coyni.view.AddCardActivity;

public class CardNumberEditText extends ConstraintLayout {

    private TextView hintName;
    private LinearLayout hintHolder;
    private EditText cnET;
    private ImageView imgCardType, readCardIV;
    boolean isPhoneError = false;
    public String cardType = "";
    public String from = "";

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

                if (from.equals("ADD_CARD")) {
                    try {
                        if (b) {
                            cnET.setHint("");
                            hintName.setVisibility(VISIBLE);
                            hintName.setTextColor(getResources().getColor(R.color.primary_color));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                            AddCardActivity.addCardActivity.cardErrorLL.setVisibility(GONE);
                        } else {
                            if ((cnET.getText().length() > 0)) {
                                hintName.setVisibility(VISIBLE);
                                cardValidation(cnET.getText().toString());
                                cnET.setHint("");
                            } else if ((cnET.getText().length() == 0)) {
                                hintName.setVisibility(GONE);
                                cnET.setHint("Card Number");
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                                AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                                AddCardActivity.addCardActivity.cardErrorTV.setText("Field Required");
                            } else {
                                cnET.setHint("");
                                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
                            }
                            AddCardActivity.addCardActivity.enableOrDisableNext();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        cnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (from.equals("ADD_CARD")) {
                    try {
                        if (i2 > 2) {
                            AddCardActivity.addCardActivity.getCardype(charSequence.toString());
                            if ((cardType.equals("american") && charSequence.toString().trim().length() != 18) || (!cardType.equals("american") && charSequence.toString().trim().length() < 19)) {
                                AddCardActivity.addCardActivity.isCard = false;
                            } else {
                                AddCardActivity.addCardActivity.isCard = true;
                            }
                        } else if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 20) {
                            AddCardActivity.addCardActivity.isCard = true;
                            AddCardActivity.addCardActivity.cardErrorLL.setVisibility(GONE);
                            hintName.setTextColor(getResources().getColor(R.color.primary_color));
                            hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
                        } else {
                            imgCardType.setImageResource(R.drawable.ic_issue_card_inactive);
                            AddCardActivity.addCardActivity.isCard = false;

                        }
                        AddCardActivity.addCardActivity.enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (from.equals("ADD_CARD")) {
                    try {
                        if (s.length() == 7) {
                            AddCardActivity.addCardActivity.getCardype(s.toString());
                        } else if (s.toString().trim().length() == 0) {
                            imgCardType.setImageResource(R.drawable.ic_issue_card_inactive);
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
            }
        });

    }

    public void setText(String text) {
        cnET.setText(text);
        if (text.equals("")) {
            hintName.setVisibility(GONE);
            if (!cnET.isFocusable())
                cnET.setHint("Card Number");
        }
    }

    public void setSelection() {
        cnET.setSelection(cnET.getText().length());
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
            } else {
                imgCardType.setVisibility(VISIBLE);
                imgCardType.setImageResource(R.drawable.ic_issue_card_inactive);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ImageView getCardReaderIVRef() {
        return readCardIV;
    }

    public void cardValidation(String strCard) {
        try {
            if (!cardType.toLowerCase().contains("american") && !strCard.equals("") && strCard.length() < 19) {
                AddCardActivity.addCardActivity.isCard = false;
                hintName.setTextColor(getResources().getColor(R.color.error_red));
                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                AddCardActivity.addCardActivity.cardErrorTV.setText("Invalid Card Number");
            } else if (cardType.toLowerCase().contains("american") && !strCard.equals("") && strCard.length() != 18) {
                AddCardActivity.addCardActivity.isCard = false;
                hintName.setTextColor(getResources().getColor(R.color.error_red));
                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_error));
                AddCardActivity.addCardActivity.cardErrorLL.setVisibility(VISIBLE);
                AddCardActivity.addCardActivity.cardErrorTV.setText("Invalid Card Number");
            } else {
                AddCardActivity.addCardActivity.isCard = true;
                hintName.setTextColor(getResources().getColor(R.color.primary_black));
                hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void disableEditText() {
        cnET.setEnabled(false);
        cnET.setTextColor(getResources().getColor(R.color.xdark_gray));
        hintName.setTextColor(getResources().getColor(R.color.xdark_gray));
        hintName.setBackgroundColor(Color.TRANSPARENT);
        hintHolder.setBackground(null);
    }

    public void hideCamera() {
        readCardIV.setVisibility(GONE);
    }

    public void setFrom(String strFrom) {
        from = strFrom;
    }

    public void removeError() {
        hintName.setTextColor(getResources().getColor(R.color.primary_color));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
    }

    public void requestCNETFocus() {
        cnET.requestFocus();
        hintName.setTextColor(getResources().getColor(R.color.primary_color));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_focused));
        hintName.setVisibility(VISIBLE);
    }

    public void hideBrandIcon() {
//        imgCardType.setVisibility(INVISIBLE);
        imgCardType.setImageResource(R.drawable.ic_issue_card_inactive);
    }

    public void enableHint() {
        hintName.setVisibility(VISIBLE);
        hintName.setBackgroundColor(getResources().getColor(R.color.white));
        hintHolder.setBackground(getResources().getDrawable(R.drawable.outline_box_unfocused));
    }
}