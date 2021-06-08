package com.example.myfarmstory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    int notFruit = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    Runnable repeatChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                checkRowForThree();
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
}