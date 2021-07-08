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
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myfarmstory.Models.Users;
import com.example.myfarmstory.databinding.ActivityMainBinding;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    Dialog dialogsetting,dialogprfile,dialogstart,dialognewhighscore,dialogyourscore,dialoglogout;
    private FirebaseDatabase database;
    private DatabaseReference refdatabase;
    private static final String USERS ="Users";
    private static final long START_TIME_IN_MILLIS = 600000;
    private CountDownTimer countDownTimer;
    private long timeLeftInMills = START_TIME_IN_MILLIS;


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
    int interval = 200;
    TextView scoreResult;
    int score = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogsetting = new Dialog(this);
        dialogprfile = new Dialog(this);
        dialogstart = new Dialog(this);
        dialognewhighscore = new Dialog(this);
        dialogyourscore = new Dialog(this);
        dialoglogout = new Dialog(this);
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
                pauseTimer();
            }
        });
        startDialog();
        updateCountDownText();
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
        for (int i = 0; i < 48; i++)
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

    private void checkRowForFour()
    {
        for (int i = 0; i < 62; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            Integer[] notValid = {5, 6, 7, 13, 14, 15, 21, 22, 23, 29, 30, 31, 37, 38, 39, 45, 46, 47, 53, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i))
            {
                int x = i;
                if ((int) fruit.get(x++).getTag() == chosenFruit && !isBlank &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x).getTag() == chosenFruit)
                {
                    score = score + 6;
                    scoreResult.setText(String.valueOf(score));
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                    x--;
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

    private void checkColumnForFour()
    {
        for (int i = 0; i < 40; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            int x = i;
            if ((int) fruit.get(x).getTag() == chosenFruit && !isBlank &&
                    (int) fruit.get(x+noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+2*noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+3*noOfBlocks).getTag() == chosenFruit)
            {
                score = score + 6;
                scoreResult.setText(String.valueOf(score));
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
                x = x + noOfBlocks;
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

    private void checkRowForFive()
    {
        for (int i = 0; i < 62; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            Integer[] notValid = {4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23, 28, 29, 30, 31, 36, 37, 38, 39, 44, 45, 46, 47, 52, 53, 54, 55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i))
            {
                int x = i;
                if ((int) fruit.get(x++).getTag() == chosenFruit && !isBlank &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x++).getTag() == chosenFruit &&
                        (int) fruit.get(x).getTag() == chosenFruit)
                {
                    score = score + 10;
                    scoreResult.setText(String.valueOf(score));
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                    x--;
                    fruit.get(x).setImageResource(notFruit);
                    fruit.get(x).setTag(notFruit);
                    x--;
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

    private void checkColumnForFive()
    {
        for (int i = 0; i < 32; i++)
        {
            int chosenFruit = (int) fruit.get(i).getTag();
            boolean isBlank = (int) fruit.get(i).getTag() == notFruit;
            int x = i;
            if ((int) fruit.get(x).getTag() == chosenFruit && !isBlank &&
                    (int) fruit.get(x+noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+2*noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+3*noOfBlocks).getTag() == chosenFruit &&
                    (int) fruit.get(x+4*noOfBlocks).getTag() == chosenFruit)
            {
                score = score + 10;
                scoreResult.setText(String.valueOf(score));
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
                x = x + noOfBlocks;
                fruit.get(x).setImageResource(notFruit);
                fruit.get(x).setTag(notFruit);
                x = x + noOfBlocks;
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
                checkRowForFive();
                checkColumnForFive();
                checkRowForFour();
                checkColumnForFour();
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
    private void opensettingdialog(){
        dialogsetting.setContentView(R.layout.setting_layout);
        dialogsetting.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogsetting.setCanceledOnTouchOutside(false);

        Button resume = dialogsetting.findViewById(R.id.bt_resume);
        Button restart = dialogsetting.findViewById(R.id.bt_restart);
        Button profile = dialogsetting.findViewById(R.id.bt_profile);
        Button logout = dialogsetting.findViewById(R.id.bt_logout);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsetting.dismiss();
                startTimer();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogsetting.dismiss();
                restartTimer();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogprfile.setContentView(R.layout.user_layout);
                dialogprfile.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogprfile.setCanceledOnTouchOutside(false);
                ImageView back = dialogprfile.findViewById(R.id.iv_back);
                TextView username = dialogprfile.findViewById(R.id.iv_un);
                TextView email = dialogprfile.findViewById(R.id.iv_e);
                TextView highpoint = dialogprfile.findViewById(R.id.iv_hp);
                if (auth.getCurrentUser() != null) {
                    database = FirebaseDatabase.getInstance();
                    refdatabase = database.getReference();
                    refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user = snapshot.getValue(Users.class);
                            if (user == null) {
                                return;
                            }
                            username.setText(user.getUserName());
                            email.setText(user.getMail());
                            highpoint.setText(user.getLastpoint());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                } else {
                    username.setText("Guest");
                    email.setText("Guest");
                    highpoint.setText("Unknow");
                }
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogprfile.dismiss();
                    }
                });
                dialogprfile.show();
        }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
        dialogsetting.show();
    }
    private void startDialog() {
        dialogstart.setContentView(R.layout.start_dialog);
        dialogstart.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogstart.setCanceledOnTouchOutside(false);
        Button start = dialogstart.findViewById(R.id.bt_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogstart.dismiss();
                startTimer();
                score = 0;
                binding.score.setText("0");
            }
        });
        dialogstart.show();
    }
    private void newhighscoreDialog(){
        dialognewhighscore.setContentView(R.layout.new_best_score);
        dialognewhighscore.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialognewhighscore.setCanceledOnTouchOutside(false);
        Button good = dialognewhighscore.findViewById(R.id.bt_good);
        TextView newscore = dialognewhighscore.findViewById(R.id.iv_newscore);
        newscore.setText(String.valueOf(score));
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialognewhighscore.dismiss();
                restartTimer();
            }
        });
        dialognewhighscore.show();
    }
    private void yourscoreDialog(){
        dialogyourscore.setContentView(R.layout.your_score);
        dialogyourscore.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogyourscore.setCanceledOnTouchOutside(false);
        Button good = dialogyourscore.findViewById(R.id.bt_good);
        TextView uscore = dialogyourscore.findViewById(R.id.iv_newscore);
        uscore.setText(String.valueOf(score));
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogyourscore.dismiss();
                restartTimer();
            }
        });
        dialogyourscore.show();
    }
    private void logoutDialog(){
        dialoglogout.setContentView(R.layout.logout_dialog);
        dialoglogout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialoglogout.setCanceledOnTouchOutside(false);
        Button out = dialoglogout.findViewById(R.id.bt_thoat);
        Button cancel = dialoglogout.findViewById(R.id.bt_huy);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglogout.dismiss();
            }
        });
        dialoglogout.show();
    }
    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMills,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMills = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
    private void pauseTimer(){
        countDownTimer.cancel();
    }
    private void restartTimer(){
        timeLeftInMills = START_TIME_IN_MILLIS;
        updateCountDownText();
        startTimer();
        score = 0;
        binding.score.setText("0");
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMills / 10000) / 60;
        int seconds = (int) (timeLeftInMills / 1000) % 60;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.countdown.setText(timeLeft);
        if(minutes == 0 && seconds == 0){
            pauseTimer();
            if(auth.getCurrentUser() != null)
            {
                database = FirebaseDatabase.getInstance();
                refdatabase = database.getReference();
                refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        if(user == null){
                        user = new Users(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getEmail(), null);
                        refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).setValue(user);
                        }
                        String lastpoint = user.getLastpoint();
                        String username = user.getUserName();
                        String password = user.getPassword();
                        if(lastpoint == null && password != null){
                            Users users = new Users();
                            users.setPassword(password);
                            users.setMail(auth.getCurrentUser().getEmail());
                            users.setUserName(username);
                            users.setLastpoint(String.valueOf(score));
                            refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).setValue(users);
                            yourscoreDialog();
                        }
                        else if(lastpoint != null && password != null){
                            if(Integer.parseInt(lastpoint) < score){
                                Users users = new Users();
                                users.setPassword(password);
                                users.setMail(auth.getCurrentUser().getEmail());
                                users.setUserName(username);
                                users.setLastpoint(String.valueOf(score));
                                refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).setValue(users);
                                newhighscoreDialog();
                            }
                            else if(Integer.parseInt(lastpoint) >= score){
                                yourscoreDialog();
                            }
                        }
                        else if(Integer.parseInt(lastpoint) == 0 && password == null)
                        {
                            Users users = new Users();
                            users.setMail(auth.getCurrentUser().getEmail());
                            users.setUserName(auth.getCurrentUser().getDisplayName());
                            users.setLastpoint(String.valueOf(score));
                            refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).setValue(users);
                            yourscoreDialog();
                        }
                        else if(lastpoint != null && password == null)
                        {
                            if(Integer.parseInt(lastpoint) < score){ Users users = new Users();
                                users.setMail(auth.getCurrentUser().getEmail());
                                users.setUserName(auth.getCurrentUser().getDisplayName());
                                users.setLastpoint(String.valueOf(score));
                                refdatabase.child(USERS).child(auth.getCurrentUser().getUid()).setValue(users);
                                newhighscoreDialog();
                            }
                            else if(Integer.parseInt(lastpoint) >= score){
                                yourscoreDialog();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            else{
                yourscoreDialog();
            }

        }
    }
}
