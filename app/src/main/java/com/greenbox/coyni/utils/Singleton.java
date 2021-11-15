package com.greenbox.coyni.utils;

import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;

public class Singleton {


    private static Singleton instance;

    public static CustRegisRequest custRegisRequest = new CustRegisRequest();
    public static CustRegisterResponse custRegisterResponse = new CustRegisterResponse() ;

    private Singleton() {
    }

    public static Singleton Instance() {
        //if no instance is initialized yet then create new instance
        //else return stored instance
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public static Singleton clearSingleTon() {
        instance = null;
        return null;
    }

    public static CustRegisRequest getCustRegisRequest() {
        return custRegisRequest;
    }

    public static void setCustRegisRequest(CustRegisRequest mCustRegisRequest) {
        custRegisRequest = mCustRegisRequest;
    }

    public static CustRegisterResponse getCustRegisterResponse() {
        return custRegisterResponse;
    }

    public static void setCustRegisterResponse(CustRegisterResponse mCustRegisterResponse) {
        custRegisterResponse = mCustRegisterResponse;
    }
}
