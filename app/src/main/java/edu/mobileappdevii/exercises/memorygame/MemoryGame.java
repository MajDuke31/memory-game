package edu.mobileappdevii.exercises.memorygame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends AppCompatActivity {
    // Holds the values of all of the squares
    private ArrayList<Integer> squareValues;

    // Holds the list of values that were successfully matched
    private ArrayList<Integer> matchedItems;

    private GridView squaresGridView; // The grid of squares
    private Integer firstPick; // Holds the value of the first picked square
    private Integer secondPick; // Holds the value of the second picked square
    private TextView firstPickTextView; // Holds a reference to the user's first picked square
    private TextView secondPickTextView; // Holds a reference to the user's second picked square
    private TextView squareTextView; // Holds a reference to the square that was clicked on
    private int correctGuesses; // Keeps track of the total number of correct guesses
    private int incorrectGuesses; // Keeps track of the total number of incorrect guesses
    private TextView correctGuessesTextView; // Holds reference to the correct guesses label
    private TextView incorrectGuessesTextView; // Holds reference to the incorrect guesses label

    // The flip animation requires two animation objects, one for shrinking and one for growing
    private Animation startFlipAnimation;
    private Animation finishFlipAnimation;

    // Holds the position in the ArrayList of the value of the square the user clicked on
    private int currentPosition;

    // AnimationListener for revealing the value behind a square
    private Animation.AnimationListener revealSquareAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            // Ignored
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // Reveal the value of the square and kick off the second half of the animation
            squareTextView.setText(squareValues.get(currentPosition).toString());
            squareTextView.setBackgroundResource(R.drawable.square_face_up);
            squareTextView.startAnimation(finishFlipAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // Ignored
        }
    };

    // AnimationListener for "removing" a square from the UI.
    private Animation.AnimationListener removeSquaresAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            // Ignored
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // "Remove" the squares with matching values and
            // kick off the second half of the animation
            firstPickTextView.setText("");
            secondPickTextView.setText("");
            firstPickTextView.setBackgroundResource(R.drawable.removed_square);
            secondPickTextView.setBackgroundResource(R.drawable.removed_square);
            firstPick = null;
            secondPick = null;
            firstPickTextView.startAnimation(finishFlipAnimation);
            secondPickTextView.startAnimation(finishFlipAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // Ignored
        }
    };

    // AnimationListener for flipping two squares face down
    private Animation.AnimationListener turnSquaresFaceDownAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            // Ignored
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // Flip the two squares face down and kick off the second half of the animation
            firstPickTextView.setText("");
            secondPickTextView.setText("");
            firstPickTextView.setBackgroundResource(R.drawable.square_face_down);
            secondPickTextView.setBackgroundResource(R.drawable.square_face_down);
            firstPick = null;
            secondPick = null;
            firstPickTextView.startAnimation(finishFlipAnimation);
            secondPickTextView.startAnimation(finishFlipAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // Ignored
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        // Fetch references to the TextView labels
        correctGuessesTextView = (TextView) findViewById(R.id.correctGuessesTextView);
        incorrectGuessesTextView = (TextView) findViewById(R.id.incorrectGuessesTextView);

        // Start the scoreboard at zero
        displayScores();

        // Set up the grid of squares
        setupGrid();

        // Load the animations for flipping squares
        startFlipAnimation = AnimationUtils.loadAnimation(this, R.anim.shrink);
        finishFlipAnimation = AnimationUtils.loadAnimation(this, R.anim.grow);
    }

    // Used to set up the grid of squares
    private void setupGrid() {
        // Initialize both ArrayLists
        squareValues = new ArrayList<>();
        matchedItems = new ArrayList<>();

        // Fill the grid twice with values from 0-9.
        // This will guarantee duplicate numbers
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                squareValues.add(j);
            }
        }
        // Shuffle the array of values
        Collections.shuffle(squareValues);

        // Set up the GridView with squares
        squaresGridView = (GridView) findViewById(R.id.squaresGridView);
        squaresGridView.setAdapter(new SquaresAdapter(this, squareValues));

        squaresGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (matchedItems.contains(squareValues.get(position))) {
                    // The user clicked on a square that has already been matched, do nothing
                    return;
                }

                // Grab the square that was clicked on
                currentPosition = position;
                squareTextView = (TextView) view.findViewById(R.id.squareTextView);
                startFlipAnimation.setAnimationListener(revealSquareAnimationListener);

                if (firstPick == null) {
                    // This square is the first square the user has clicked on
                    squareTextView.startAnimation(startFlipAnimation);
                    firstPickTextView = squareTextView;
                    firstPick = squareValues.get(position);
                } else {
                    // This square is the second square the user has clicked on
                    secondPickTextView = squareTextView;
                    if (secondPickTextView == firstPickTextView) {
                        // The user clicked on the same square twice, so do nothing
                        return;
                    }

                    // The first picked and second picked squares are valid, proceed
                    squareTextView.startAnimation(startFlipAnimation);
                    secondPick = squareValues.get(position);

                    if (firstPick.intValue() == secondPick.intValue()) {
                        // We have a match!
                        correctGuesses++;
                        // Notify the user of a match
                        Toast.makeText(getApplicationContext(), firstPick.intValue() + " and " + secondPick.intValue() + " match!", Toast.LENGTH_SHORT).show();

                        // We use a postDelayed() method call here to pause the current thread
                        // while we wait for the flip animation to catch up and reveal the second
                        // square's value to the user.
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeCards(firstPickTextView, secondPickTextView);
                                if (correctGuesses >= 10) {
                                    // All cards have been successfully matched so we start new game
                                    correctGuesses = 0;
                                    incorrectGuesses = 0;
                                    firstPick = null;
                                    secondPick = null;

                                    // We use a postDelayed() method call here to pause the current thread
                                    // while we wait for the flip animation to catch up and flip both
                                    // squares face down before resetting the game.
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setupGrid();
                                            displayScores();
                                        }
                                    }, 1000);
                                } else {
                                    matchedItems.add(firstPick);
                                }
                            }
                        }, 1000);
                    } else {
                        // We do not have a match
                        incorrectGuesses++;
                        // Notify the user that the cards did not match
                        Toast.makeText(getApplicationContext(), firstPick.intValue() + " and " + secondPick.intValue() + " do not match!", Toast.LENGTH_SHORT).show();

                        // We use a postDelayed() method call here to pause the current thread
                        // while we wait for the flip animation to catch up and flip the second
                        // square face up revealing its value to the user before flipping both
                        // squares face down again.
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                turnCardsFaceDown(firstPickTextView, secondPickTextView);
                            }
                        }, 1000);
                    }
                }

                // Refresh the scoreboard
                displayScores();
            }
        });
    }

    // Refreshes the scoreboard
    private void displayScores() {
        correctGuessesTextView.setText("Correct: " + correctGuesses);
        incorrectGuessesTextView.setText("Incorrect: " + incorrectGuesses);
    }

    // "Removes" square cards from the GridView
    private void removeCards(TextView firstCard, TextView secondCard) {
        startFlipAnimation.setAnimationListener(removeSquaresAnimationListener);
        firstPickTextView.startAnimation(startFlipAnimation);
        secondPickTextView.startAnimation(startFlipAnimation);
    }

    // Turns both square cards face down in the event they do not match
    private void turnCardsFaceDown(TextView firstCard, TextView secondCard) {
        startFlipAnimation.setAnimationListener(turnSquaresFaceDownAnimationListener);
        firstPickTextView.startAnimation(startFlipAnimation);
        secondPickTextView.startAnimation(startFlipAnimation);
    }

    // Used to programmatically add options to the options menu. Not being used in this exercise.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory_game, menu);
        return true;
    }

    // Handles when the user clicks on an option from the options menu. Not being used in this exercise.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
