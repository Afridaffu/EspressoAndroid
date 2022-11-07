package com.coyni.mapp.dialogs;

import com.coyni.mapp.model.SignAgreementsResp;

public interface OnAgreementsAPIListener {

    void onAgreementsAPIResponse(SignAgreementsResp signAgreementsResp, boolean isMerchantHide);
}
