package org.androidchile.gamerpg;

import android.app.Application;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by pablo on 5/25/16.
 */

public class App extends Application {

    public static int GRASS_SIZE_PIXEL_X;
    public static int GRASS_SIZE_PIXEL_Y;
    public static int GRASS_SIZE_TOTAL_X;
    public static int GRASS_SIZE_TOTAL_Y;
    public final static int GRASS_SIZE_X_DIVIDER = 10;
    public final static int GRASS_SIZE_Y_DIVIDER = 20;
    public final static int MOVE_SPEED = 400;

    public final static String PARAM_USER_ID = "userId";
    public final static String PARAM_COORDINATES = "coordinates";
    public final static String PARAM_CHARACTERS = "characters";
    public final static String PARAM_MESSAGE = "message";
    public final static String EVENT_LOGIN_USER = "login user";
    public final static String PARAM_USER_NAME = "userName";
    public final static String PARAM_USER_LAST_POSITION = "lastPosition";
    public final static String PARAM_USER_CURRENT_POSITION = "currentPosition";
    public final static String PARAM_X = "x";
    public final static String PARAM_Y = "y";
    public final static String PARAM_EMPTY = "";

    public final static String EVENT_NEW_USER = "new user";
    public final static String EVENT_MOVE = "move";
    public final static String EVENT_LEAVE = "leave";

    public static Socket socket;
    public final static String SOCKET_URL = "http://10.170.202.55/";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            socket = IO.socket(SOCKET_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static Socket getSocket(){
        return socket;
    }

    public static void socketConnect(){
        socket.connect();
    }

    public static void socketDisconnect(){
        socket.disconnect();
    }

    public static boolean socketStatus(){
        return socket.connected();
    }
}
