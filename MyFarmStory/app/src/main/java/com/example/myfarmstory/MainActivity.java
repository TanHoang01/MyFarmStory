package com.example.myfarmstory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfarmstory.databinding.ActivityMainBinding;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    Dialog dialog;

    int[] fruits = {
            R.drawable.strawberry,
            R.drawable.blueberry,
            R.drawable.orange,
            R.drawable.grape,
            R.drawable.pear,
            R.drawable.kiwi,
    };
    int withOfBlock, noOfBlocks = 8, widthOfScreen;
    ArrayList<ImageView> fruit = new ArrayList<>();
    int fruitToBeDragged, fruitToBeReplaced;
    int notFruit = R.drawable.transparent;
    Handler mHandler;
    int interval = 100;
    TextView scoreResult;
    int score = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new Dialog(this);
        auth = FirebaseAuth.getInstance();
        scoreResult = findViewById(R.id.score);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        int heightOfScreen = displayMetrics.heightPixels;
        withOfBlock = widthOfScreen / noOfBlocks;
        createBoard();
        for (final ImageView imageView : fruit)
        {
            imageView.setOnTouchListener(new OnSwipeListener(this)
            {
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged - 1;
                    fruitInterchange();
                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged + 1;
                    fruitInterchange();
                }

                @Override
                void onSwipeUp() {
                    super.onSwipeUp();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged - noOfBlocks;
                    fruitInterchange();
                }

                @Override
                void onSwipeDown() {
                    super.onSwipeDown();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged + noOfBlocks;
                    fruitInterchange();
                }
            });
        }
        mHandler = new Handler();
        startRepeat();

        binding.ivsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opensettingdialog();
            }
        });
    }

    private void checkRowForThree()
    {
        for (int i = 0; i < 62; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            Integer[] notValid = {6, 7, 14, 15, 22, 23, 30, 31, 38, 39, 46, 47, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i))
            {
                int x = i;
                if ((int) fruit.get(x++).getTag() == chosenFruit && !isBlank &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x).getTag() == chosenFruit)
                {
                    score = score + 3;
                    scoreResult.setText(String.valueOf(score));
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                    x--;
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                    x--;
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                }
            }
        }
        moveDownFruits();
    }

    private void checkColumnForThree()
    {
        for (int i = 0; i < 47; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            int x = i;
            if ((int) fruit.get(x).getTag() == chosenFruit && !isBlank &&
                    (int) fruit.get(x+noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+2*noOfBlocks).getTag() == chosenFruit)
            {
                score = score + 3;
                scoreResult.setText(String.valueOf(score));
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
                x = x + noOfBlocks;
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
                x = x + noOfBlocks;
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
            }
        }
        moveDownFruits();
    }

    private void moveDownFruits()
    {
        Integer[] firstRow = {0,1,2,3,4,5,6,7};
        List<Integer> list = Arrays.asList(firstRow);
        for (int i=55; i>=0; i--)
        {
            if ((int) fruit.get(i+noOfBlocks).getTag() == notFruit)
            {
                fruit.get(i+noOfBlocks).setImageResource((int) fruit.get(i).getTag());
                fruit.get(i+noOfBlocks).setTag(fruit.get(i).getTag());
                fruit.get(i).setImageResource(notFruit);
                fruit.get(i).setTag(notFruit);

                if(list.contains(i) && (int) fruit.get(i).getTag() == notFruit)
                {
                    int randomColor = (int) Math.floor(Math.random() * fruits.length);
                    fruit.get(i).setImageResource(fruits[randomColor]);
                    fruit.get(i).setTag(fruits[randomColor]);
                }
            }
        }
        for (int i=0; i<8;i++)
        {
            if ((int) fruit.get(i).getTag() == notFruit)
            {
                int randomColor = (int) Math.floor(Math.random() * fruits.length);
                fruit.get(i).setImageResource(fruits[randomColor]);
                fruit.get(i).setTag(fruits[randomColor]);
            }
        }
    }

    Runnable repeatChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                checkRowForThree();
                checkColumnForThree();
                moveDownFruits();
            }
            finally
            {
                mHandler.postDelayed(repeatChecker, interval);
            }
        }
    };

    void startRepeat()
    {
        repeatChecker.run();
    }

    private void fruitInterchange()
    {
        int background = (int) fruit.get(fruitToBeReplaced).getTag();
        int background1 = (int) fruit.get(fruitToBeDragged).getTag();
        fruit.get(fruitToBeDragged).setImageResource(background);
        fruit.get(fruitToBeReplaced).setImageResource(background1);
        fruit.get(fruitToBeDragged).setTag(background);
        fruit.get(fruitToBeReplaced).setTag(background1);
    }

    private void createBoard() {
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;
        for (int i = 0; i < noOfBlocks * noOfBlocks; i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(withOfBlock,withOfBlock));
            imageView.setMaxHeight(withOfBlock);
            imageView.setMaxWidth(withOfBlock);
            int randomAgricultural = (int) Math.floor(Math.random()* fruits.length);
            imageView.setImageResource(fruits[randomAgricultural]);
            imageView.setTag(fruits[randomAgricultural]);
            fruit.add(imageView);
            gridLayout.addView(imageView);
        }
    }
    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("                        Cảnh Báo !                  ");
        builder.setMessage("Bạn có muốn đăng xuất khỏi tài khoản này?");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        builder.create();
        builder.show();
    }
    private void opensettingdialog(){
        dialog.setContentView(R.layout.setting_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button resume = dialog.findViewById(R.id.bt_resume);
        Button profile = dialog.findViewById(R.id.bt_profile);
        Button logout = dialog.findViewById(R.id.bt_logout);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
        dialog.show();
    }
}