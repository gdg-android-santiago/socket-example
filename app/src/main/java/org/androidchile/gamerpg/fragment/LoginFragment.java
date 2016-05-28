package org.androidchile.gamerpg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.androidchile.gamerpg.App;
import org.androidchile.gamerpg.R;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

import static org.androidchile.gamerpg.App.EVENT_LOGIN_USER;
import static org.androidchile.gamerpg.App.EVENT_NEW_USER;
import static org.androidchile.gamerpg.App.PARAM_EMPTY;
import static org.androidchile.gamerpg.App.PARAM_USER_CURRENT_POSITION;
import static org.androidchile.gamerpg.App.PARAM_USER_ID;
import static org.androidchile.gamerpg.App.PARAM_USER_LAST_POSITION;
import static org.androidchile.gamerpg.App.PARAM_USER_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private TextInputEditText editTextNickname;
    private AppCompatButton buttonEnter;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextNickname = (TextInputEditText)view.findViewById(R.id.editTextName);
        buttonEnter = (AppCompatButton)view.findViewById(R.id.buttonEnter);
        buttonEnter.setOnClickListener(clickListenerEnter);

        App.getSocket().on(EVENT_LOGIN_USER, socketLoginUser);
    }

    private View.OnClickListener clickListenerEnter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!editTextNickname.getText().toString().isEmpty()) {
                JSONObject jsonData = new JSONObject();
                try {
                    jsonData.put(PARAM_USER_ID, PARAM_EMPTY);
                    jsonData.put(PARAM_USER_NAME, editTextNickname.getText().toString());
                    jsonData.put(PARAM_USER_LAST_POSITION, PARAM_EMPTY);
                    jsonData.put(PARAM_USER_CURRENT_POSITION, PARAM_EMPTY);

                    if(App.socketStatus()) {
                        App.getSocket().emit(EVENT_NEW_USER, jsonData);
                    }else{
                        Log.e("SOCKET", "SOCKET NOT CONNECTED");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Emitter.Listener socketLoginUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(!args[0].toString().isEmpty()) {

                final String userId = args[0].toString();
                final String characters = args[1].toString();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, MapFragment.newInstance(userId, characters))
                        .addToBackStack("map")
                        .commit();
            }
        }
    };
}