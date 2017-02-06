package com.adelelis.takemymoney;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

class RequestAPIResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    RequestAPIResultReceiver(Handler handler) {
        super(handler);
    }

    void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
