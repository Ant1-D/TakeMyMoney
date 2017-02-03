package com.adelelis.takemymoney;

import android.os.Bundle;
        import android.os.Handler;
        import android.os.ResultReceiver;

public class ConnectionResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public ConnectionResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
