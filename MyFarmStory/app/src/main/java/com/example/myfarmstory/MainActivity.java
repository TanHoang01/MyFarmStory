package com.example.myfarmstory;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

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
        for (ImageView imageView : fruit)
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
                void onSwipeTop() {
                    super.onSwipeTop();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged - noOfBlocks;
                    fruitInterchange();
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    fruitToBeDragged = imageView.getId();
                    fruitToBeReplaced = fruitToBeDragged + noOfBlocks;
                    fruitInterchange();
                }
            });
        }
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