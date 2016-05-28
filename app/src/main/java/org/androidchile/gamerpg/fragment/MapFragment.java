package org.androidchile.gamerpg.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.androidchile.gamerpg.App;
import org.androidchile.gamerpg.R;
import org.androidchile.gamerpg.model.Coordinates;
import org.androidchile.gamerpg.model.Move;
import org.androidchile.gamerpg.model.Character;
import org.androidchile.gamerpg.util.Factory;
import org.androidchile.gamerpg.util.Node;
import org.androidchile.gamerpg.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.socket.emitter.Emitter;

import static io.socket.client.Socket.EVENT_MESSAGE;
import static org.androidchile.gamerpg.App.EVENT_LEAVE;
import static org.androidchile.gamerpg.App.EVENT_MOVE;
import static org.androidchile.gamerpg.App.EVENT_NEW_USER;
import static org.androidchile.gamerpg.App.GRASS_SIZE_PIXEL_X;
import static org.androidchile.gamerpg.App.GRASS_SIZE_PIXEL_Y;
import static org.androidchile.gamerpg.App.GRASS_SIZE_TOTAL_X;
import static org.androidchile.gamerpg.App.GRASS_SIZE_TOTAL_Y;
import static org.androidchile.gamerpg.App.GRASS_SIZE_X_DIVIDER;
import static org.androidchile.gamerpg.App.GRASS_SIZE_Y_DIVIDER;
import static org.androidchile.gamerpg.App.MOVE_SPEED;
import static org.androidchile.gamerpg.App.PARAM_CHARACTERS;
import static org.androidchile.gamerpg.App.PARAM_MESSAGE;
import static org.androidchile.gamerpg.App.PARAM_USER_ID;
import static org.androidchile.gamerpg.App.PARAM_X;
import static org.androidchile.gamerpg.App.PARAM_Y;
import static org.androidchile.gamerpg.util.Node.calculateMoveFromPixel;
import static org.androidchile.gamerpg.util.Util.getRelativeLeft;
import static org.androidchile.gamerpg.util.Util.getRelativeTop;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends Fragment {

    private FrameLayout layoutGrass;
    private FloatingActionButton actionButton;
    private static String userId;
    private static List<Character> characters = new ArrayList<>();
    private Gson gson = new GsonBuilder().create();

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String userId, String characters) {
        MapFragment fragment = new MapFragment();
        Bundle params = new Bundle();
        params.putString(PARAM_USER_ID, userId);
        params.putString(PARAM_CHARACTERS, characters);
        fragment.setArguments(params);
        return fragment;
    }

    private Emitter.Listener socketNewUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Character character = gson.fromJson(args[0].toString(), Character.class);
            characters.add(character);
            addCharacter(getIndexCharacter(character.getUserId()));
        }
    };

    private Emitter.Listener socketMove = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String userId = args[0].toString();
            final int positionX = (int)args[1];
            final int positionY = (int)args[2];
            final Coordinates coordinates = new Coordinates(positionX, positionY);
            moveCharacter(getIndexCharacter(userId), coordinates);
        }
    };

    private Emitter.Listener socketMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final String userId = args[0].toString();
            final String userMessage = args[1].toString();

            int i = getIndexCharacter(userId);
            talkAction(i, userMessage);
        }
    };

    private Emitter.Listener socketLeave = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            /*final int i = getIndexCharacter(args[0].toString());

            if(getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(i > 0) {
                        layoutGrass.removeView(characters.get(i).getImageView());
                        characters.remove(i);
                    }
                }
            });*/
        }
    };

    private void addCharacter(final int i){

        if(getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int id = View.generateViewId();

                final int positionX = characters.get(i).getCurrentPosition().getX();
                final int positionY = characters.get(i).getCurrentPosition().getY();

                characters.get(i).setViewId(id);
                characters.get(i).setImageView(new ImageView(getActivity()));
                characters.get(i).getImageView().setId(id);
                characters.get(i).getImageView().setImageResource(R.drawable.sorcerer);
                characters.get(i).getImageView().setLayoutParams(new ViewGroup.LayoutParams(GRASS_SIZE_PIXEL_X, GRASS_SIZE_PIXEL_Y));

                characters.get(i).getImageView().setOnClickListener(clickCharacter);
                characters.get(i).setAnimator(new AnimatorSet());
                characters.get(i).setTalk(
                        new SimpleTooltip.Builder(getActivity())
                        .anchorView(characters.get(i).getImageView())
                        .gravity(Gravity.TOP)
                        .animated(true)
                        .transparentOverlay(true)
                );

                layoutGrass.addView(characters.get(i).getImageView());
                characters.get(i).getImageView().setTranslationX(positionX * GRASS_SIZE_PIXEL_X);
                characters.get(i).getImageView().setTranslationY(positionY * GRASS_SIZE_PIXEL_Y);
            }
        });

    }

    private int getIndexCharacter(String userId){
        for (int i= 0; i < characters.size(); i++){
            if(characters.get(i).getUserId().equals(userId))
                return i;
        }
        return -1;
    }

    private void updateCharacter(int index, Coordinates coordinates){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(PARAM_USER_ID, characters.get(index).getUserId());
            jsonObject.put(PARAM_X, coordinates.getX());
            jsonObject.put(PARAM_Y, coordinates.getY());
            App.getSocket().emit(EVENT_MOVE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveCharacter(final int i, final Coordinates coordinates){

        if(getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final Map<Node> myMap = new Map<>(GRASS_SIZE_X_DIVIDER, GRASS_SIZE_Y_DIVIDER, new Factory());
                final List<Node> path = myMap.findPath(
                        characters.get(i).getCurrentPosition().getX(),
                        characters.get(i).getCurrentPosition().getY(),
                        coordinates.getX(), coordinates.getY());

                // Animation
                List<Animator> animations = new ArrayList<Animator>();

                for(int index = 0; index < path.size(); index++){

                    Node node = path.get(index);

                    float targetX = (node.getxPosition() * GRASS_SIZE_PIXEL_X);
                    float targetY = (node.getyPosition() * GRASS_SIZE_PIXEL_Y);

                    PropertyValuesHolder x = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, targetX);
                    PropertyValuesHolder y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, targetY);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(characters.get(i).getImageView(), x, y);
                    objectAnimator.setDuration(MOVE_SPEED);
                    objectAnimator.setRepeatCount(0);
                    animations.add(objectAnimator);
                }

                characters.get(i).getAnimator().playSequentially(animations);
                characters.get(i).getAnimator().addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        characters.get(i).setWalking(true);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        characters.get(i).getImageView().setTranslationX(coordinates.getX() * GRASS_SIZE_PIXEL_X);
                        characters.get(i).getImageView().setTranslationY(coordinates.getY() * GRASS_SIZE_PIXEL_Y);
                        characters.get(i).setCurrentPosition(coordinates);
                        characters.get(i).setAnimator(new AnimatorSet());
                        characters.get(i).setWalking(false);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                characters.get(i).getAnimator().start();
            }
        });
    }

    private View.OnTouchListener touchGrassListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int i = getIndexCharacter(userId);

            if(!characters.get(i).isWalking()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        final int pointerIndex = MotionEventCompat.getActionIndex(event);
                        final float x = MotionEventCompat.getX(event, pointerIndex);
                        final float y = MotionEventCompat.getY(event, pointerIndex);

                        if (characters.get(i).getImageView() != null) {
                            Move move = calculateMoveFromPixel(getRelativeLeft(characters.get(i).getImageView()), getRelativeTop(characters.get(i).getImageView()), (int) x, (int) y);

                            if (move.getToXNode() != characters.get(i).getCurrentPosition().getX() || move.getToYNode() != characters.get(i).getCurrentPosition().getY()) {
                                updateCharacter(getIndexCharacter(userId), new Coordinates(move.getToXNode(), move.getToYNode()));
                            }
                        }
                }
            }else{
                Toast.makeText(getActivity(), getString(R.string.text_you_are_walking), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    };

    private View.OnClickListener clickCharacter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener clickActionButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int i = getIndexCharacter(userId);

            if(!characters.get(i).isWalking()) {
                final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_talk, null);
                final TextInputEditText editText = (TextInputEditText)view.findViewById(R.id.editTextMessage);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.title_dialog_message));
                alertDialog.setView(view);
                alertDialog.setPositiveButton(getString(R.string.button_message_send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().isEmpty()) {
                            JSONObject jsonMessage = new JSONObject();
                            try {
                                jsonMessage.put(PARAM_USER_ID, userId);
                                jsonMessage.put(PARAM_MESSAGE, editText.getText().toString());
                                App.getSocket().emit(EVENT_MESSAGE, jsonMessage);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            editText.setError(getString(R.string.text_say_something));
                        }
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.button_message_back), null);
                alertDialog.show();
            }else{
                Toast.makeText(getActivity(), getString(R.string.text_you_are_walking), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void talkAction(final int i, final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                characters.get(i).getTalk().text(message).onDismissListener(new SimpleTooltip.OnDismissListener() {
                    @Override
                    public void onDismiss(SimpleTooltip tooltip) {
                        characters.get(i).setTalk(new SimpleTooltip.Builder(getActivity())
                                .anchorView(characters.get(i).getImageView())
                                .gravity(Gravity.TOP)
                                .animated(true)
                                .transparentOverlay(true));
                    }
                }).build().show();
            }
        });
    }

    private void showMyToolTip(){
        if(userId != null){
            int i = getIndexCharacter(userId);
            new SimpleTooltip.Builder(getActivity())
                    .anchorView(characters.get(i).getImageView())
                    .gravity(Gravity.TOP)
                    .animated(true)
                    .transparentOverlay(false)
                    .text(getString(R.string.text_you_are_here))
                    .build().show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){

            userId = getArguments().getString(PARAM_USER_ID);
            characters = gson.fromJson(getArguments().getString(PARAM_CHARACTERS),
                    new TypeToken<List<Character>>(){}.getType());
        }else{
            getActivity().onBackPressed();
        }

        App.getSocket().on(EVENT_NEW_USER, socketNewUser);
        App.getSocket().on(EVENT_MOVE, socketMove);
        App.getSocket().on(EVENT_MESSAGE, socketMessage);
        App.getSocket().on(EVENT_LEAVE, socketLeave);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        GRASS_SIZE_TOTAL_X = size.x;
        GRASS_SIZE_TOTAL_Y = size.y;
        GRASS_SIZE_PIXEL_X = GRASS_SIZE_TOTAL_X / GRASS_SIZE_X_DIVIDER;
        GRASS_SIZE_PIXEL_Y = GRASS_SIZE_TOTAL_Y / GRASS_SIZE_Y_DIVIDER;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutGrass = (FrameLayout)view.findViewById(R.id.layoutGrass);
        actionButton = (FloatingActionButton)view.findViewById(R.id.fabAction);

        layoutGrass.setOnTouchListener(touchGrassListener);
        actionButton.setOnClickListener(clickActionButton);

        for(int i= 0; i < characters.size(); i++) {
            addCharacter(i);
        }

        showMyToolTip();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onDestroy() {
        App.getSocket().emit(EVENT_LEAVE, userId);
        super.onDestroy();
    }
}
