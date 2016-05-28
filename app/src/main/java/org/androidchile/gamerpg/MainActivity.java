package org.androidchile.gamerpg;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static org.androidchile.gamerpg.App.EVENT_LEAVE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getSocket()
                .on(Socket.EVENT_CONNECT, socketConnect)
                .on(Socket.EVENT_DISCONNECT, socketDisconnect);

    }

    Emitter.Listener socketConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args){
            Log.i("SOCKET", "USER CONNECTED");
        }
    };

    Emitter.Listener socketDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args){
            Log.e("SOCKET", "USER DISCONNECTED");
        }
    };

    Emitter.Listener socketLeave = new Emitter.Listener() {
        @Override
        public void call(Object... args){
            Log.e("SOCKET", "USER LEAVE");
        }
    };

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {

            App.getSocket().emit(EVENT_LEAVE, new JSONObject());
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.socketConnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.socketDisconnect();
    }
}
