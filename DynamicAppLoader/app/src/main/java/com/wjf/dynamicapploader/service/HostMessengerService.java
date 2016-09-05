package com.wjf.dynamicapploader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.wjf.dynamicapploader.MyApplicaiton;

/**
 * description
 *
 * @author weijianfeng @Hangzhou Youzan Technology Co.Ltd
 * @date 16/9/4
 */
public class HostMessengerService extends Service {

    private static final int MSG_FROM_PLUGIN = 1001;
    private static final int MSG_FROM_HOST = 1002;

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FROM_PLUGIN:
                    Toast.makeText(MyApplicaiton.getAppContext(),"msg from plugin",Toast.LENGTH_SHORT).show();
                    Messenger client = msg.replyTo;
                    Message reply = Message.obtain(null, MSG_FROM_HOST);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "data from Host by Messenger");
                    reply.setData(bundle);
                    try {
                        client.send(reply);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
