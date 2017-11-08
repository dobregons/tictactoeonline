package com.dobregons.android.tictactoe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.EditText;
import 	android.view.LayoutInflater;

import static java.lang.String.*;


public class AndroidTicTacToeActivity extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    //Represent if wins, tie or loss
    private int winner;
    //Button for the next game
    private Button mBtnNextGame;
    //Label for count Human wins
    private TextView lblHuman;
    //Label for count Ties
    private TextView lblTies;
    //Label for count Android wins
    private TextView lblAndroid;
    //Bool GameOver
    private boolean gameOver;
    private boolean turn;
    //Id Dialog Boxes Menu
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private BoardView mBoardView;
    //Saving persistent information
    private SharedPreferences mPrefs;
    //level MediaPlayer variables for the sound effects
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;
    //bool sound on or off
    private boolean mSoundOn;
    private int set_volume;

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.humansound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computersound);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    //To save the state of the game. This method is called by Android before it pauses the application (before onPause() is triggered), and it passes the method a Bundle object which can be used to save key/value pairs that will be given back to the application (via another call to onCreate()) even if the Activity is dropped from memory.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", gameOver);
        outState.putString("mHumanWins", lblHuman.getText().toString());
        outState.putString("mComputerWins", lblAndroid.getText().toString());
        outState.putString("mTies", lblTies.getText().toString());
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mGoFirst", turn);
    }


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!gameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos))	{

                // If no winner yet, let the computer make a move
                winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1)
                {
                    lblTies.setText(valueOf(Integer.parseInt(lblTies.getText().toString()) + 1));
                    mInfoTextView.setText(R.string.result_tie);
                    gameOver = true;
                }

                else if (winner == 2)
                {
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    gameOver = true;
                }
                else
                {
                    lblAndroid.setText(valueOf(Integer.parseInt(lblAndroid.getText().toString()) + 1));
                    mInfoTextView.setText(R.string.result_computer_wins);
                    gameOver = true;
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize mPrefs to save persistent information
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        mHumanMediaPlayer = new MediaPlayer();
        mComputerMediaPlayer = new MediaPlayer();

        setContentView(R.layout.activity_tictactoe);

        //Attach each button in main.xml with a slot in the array
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
//        mBoardButtons[0] = (Button) findViewById(R.id.one);
//        mBoardButtons[1] = (Button) findViewById(R.id.two);
//        mBoardButtons[2] = (Button) findViewById(R.id.three);
//        mBoardButtons[3] = (Button) findViewById(R.id.four);
//        mBoardButtons[4] = (Button) findViewById(R.id.five);
//        mBoardButtons[5] = (Button) findViewById(R.id.six);
//        mBoardButtons[6] = (Button) findViewById(R.id.seven);
//        mBoardButtons[7] = (Button) findViewById(R.id.eight);
//        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        lblAndroid = (TextView) findViewById(R.id.txtResAndroid);
        lblHuman = (TextView) findViewById(R.id.txtResHuman);
        lblTies = (TextView) findViewById(R.id.txtResTies);

        mGame = new TicTacToeGame();

        mBtnNextGame = (Button) findViewById(R.id.btnNextGame);

        mBtnNextGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startNewGame(false);
            }
        });

        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        // Restore the scores
        String humanLbl = mPrefs.getString("mHumanWins", "0") != null ? mPrefs.getString("mHumanWins", "0") : "0";
        String computerLbl = mPrefs.getString("mComputerWins", "0") != null ? mPrefs.getString("mComputerWins", "0") : "0";
        String tiesLbl = mPrefs.getString("mTies", "0") != null ? mPrefs.getString("mTies", "0") : "0";
        lblHuman.setText(humanLbl);
        lblAndroid.setText(computerLbl);
        lblTies.setText(tiesLbl);

//        String abc = mPrefs.getString("difficulty_level", null);
//        if (abc != null){
//            switch (abc){
//                case "Easy":
//                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
//                    break;
//                case "Harder":
//                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
//                    break;
//                case "Expert":
//                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
//                    break;
//                default:
//                    break;
//            }
//            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
//        }

        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mSoundOn = mPrefs.getBoolean("sound",true);
        //Set Mediaplayer Volume
        if(mSoundOn) set_volume = 10; else set_volume = 0;
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,set_volume, 0);

        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        if (savedInstanceState == null) {
            startNewGame(true);
        }
        else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            gameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            lblHuman.setText(savedInstanceState.getString("mHumanWins"));
            lblAndroid.setText(savedInstanceState.getString("mComputerWins"));
            lblTies.setText(savedInstanceState.getString("mTies"));
            turn = savedInstanceState.getBoolean("mGoFirst");
        }
//        displayScores();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putString("mHumanWins", lblHuman.getText().toString());
        ed.putString("mComputerWins", lblAndroid.getText().toString());
        ed.putString("mTies", lblTies.getText().toString());
        ed.putString("difficulty_level", String.valueOf(mGame.getDifficultyLevel()));
        ed.commit();

    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mHumanMediaPlayer.stop();
//        mHumanMediaPlayer.release();
//
//        mComputerMediaPlayer.stop();
//        mComputerMediaPlayer.release();
//    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the game's state
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        gameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        lblHuman.setText(savedInstanceState.getString("mHumanWins"));
        lblAndroid.setText(savedInstanceState.getString("mComputerWins"));
        lblTies.setText(savedInstanceState.getString("mTies"));
        turn = savedInstanceState.getBoolean("mGoFirst");
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.add(R.string.menu_option);
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        startNewGame(true);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame(true);
                return true;
//            case R.id.ai_difficulty:
////                DialogoSeleccion dialogoSeleccion = new DialogoSeleccion();
////                dialogoSeleccion.show(fragmentManager, "tagAlerta");
////                // Display the selected difficulty level
////                Toast.makeText(getApplicationContext(), String.valueOf(R.id.ai_difficulty),
////                        Toast.LENGTH_SHORT).show();
//                showDialog(DIALOG_DIFFICULTY_ID);
//                startNewGame(true);
//                return true;
            case R.id.quit:
                DialogoConfirmacion dialogo = new DialogoConfirmacion();
                dialogo.show(fragmentManager, "tagAlerta");

                lblHuman.setText("0");
                lblAndroid.setText("0");
                lblTies.setText("0");
                //showDialog(DIALOG_QUIT_ID);
                //startNewGame(true);
                return true;
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                Dialog dialog = builder.create();
                return true;
            case R.id.reset_Score:
                lblHuman.setText("0");
                lblAndroid.setText("0");
                lblTies.setText("0");
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_DIFFICULTY_ID:

                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.
                int selected = 0;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();   // Close dialog

                                // TODO: Set the diff level of mGame based on which item was selected.
                                switch (item)
                                {
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        break;
                                    case 2:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        break;
                                }


                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();

                break;
        }

        return dialog;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            mSoundOn = mPrefs.getBoolean("sound",true);
            //Set Mediaplayer Volume
            if(mSoundOn) set_volume = 10; else set_volume = 0;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,set_volume, 0);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }

    //    public void showEditDialog(int dialogType)
//    {
//        AlertDialog alertDialog = new AlertDialog.Builder(AndroidTicTacToeActivity.this).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("Alert message to be shown");
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
//        switch (dialogType)
//        {
//            case 1:
//                break;
//            case 2:
//                break;
//        }
//    }
    // Set up the game board.
    private void startNewGame(boolean reset) {
        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board
        mBoardView.setOnTouchListener(mTouchListener);
        if (turn) {
            mInfoTextView.setText(R.string.first_human);
            turn = false;
        } else {
            try{
                SystemClock.sleep(500);
                mComputerMediaPlayer.start();
            }catch(Exception e){
                e.printStackTrace();
            }
            // TODO: Aqui falla
            setMove(TicTacToeGame.COMPUTER_PLAYER, mGame.getComputerMove());
            mInfoTextView.setText(R.string.turn_human);
            turn = true;
        }
//        mGame.clearBoard();
//        // Reset all buttons
//        for (int i = 0; i < mBoardButtons.length; i++) {
////            mBoardButtons[i].setText("");
////            mBoardButtons[i].setEnabled(true);
////            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
//        }
//        if(reset)
//        {
//            lblHuman.setText(R.string.init_value);
//            lblTies.setText(R.string.init_value);
//            lblAndroid.setText(R.string.init_value);
//        }

//        mGame.clearBoard();
//        mBoardView.invalidate();
//        // Human goes first
//        mInfoTextView.setText("You go first.");
//        //Reset GameOver
        gameOver = false;
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if(!gameOver)
            {
                if (mBoardButtons[location].isEnabled()) {
                    setMove(TicTacToeGame.HUMAN_PLAYER, location);
                    // If no winner yet, let the computer make a move
                    winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText(R.string.turn_computer);
                        int move = mGame.getComputerMove();
                        setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                        winner = mGame.checkForWinner();
                    }
                    if (winner == 0)
                        mInfoTextView.setText(R.string.turn_human);
                    else if (winner == 1)
                    {
                        lblTies.setText(valueOf(Integer.parseInt(lblTies.getText().toString()) + 1));
                        mInfoTextView.setText(R.string.result_tie);
                        gameOver = true;
                    }

                    else if (winner == 2)
                    {
//                        mHumanWins++;
//                        mHumanScoreTextView.setText(Integer.toString(mHumanWins));
                        String defaultMessage = getResources().getString(R.string.result_human_wins);
                        mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
//                        lblHuman.setText(valueOf(Integer.parseInt(lblHuman.getText().toString()) + 1));
//                        mInfoTextView.setText(R.string.result_human_wins);
                        gameOver = true;


                    }
                    else
                    {
                        lblAndroid.setText(valueOf(Integer.parseInt(lblAndroid.getText().toString()) + 1));
                        mInfoTextView.setText(R.string.result_computer_wins);
                        gameOver = true;
                    }
                }
            }

        }
    }

//    private void setMove(char player, int location) {
//        mGame.setMove(player, location);
//        mBoardButtons[location].setEnabled(false);
//        mBoardButtons[location].setText(String.valueOf(player));
//        if (player == TicTacToeGame.HUMAN_PLAYER)
//            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
//        else
//            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
//    }

    private boolean setMove(char player, int location) {
        if(player==TicTacToeGame.COMPUTER_PLAYER)
            mComputerMediaPlayer.start();
        else
            mHumanMediaPlayer.start();
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }




//    public static class EditNameDialog extends DialogFragment {
//
//        private EditText mEditText;
//
//        public EditNameDialog() {
//            // Empty constructor required for DialogFragment
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.fragment_edit_name, container);
//            mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//            getDialog().setTitle("Hello");
//
//            return view;
//        }
//    }



//    public class FragmentDialogDemo extends FragmentActivity implements EditNameDialogListener {
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_tictactoe);
//            showEditDialog();
//        }
//
//        private void showEditDialog() {
//            FragmentManager fm = getSupportFragmentManager();
//            EditNameDialog editNameDialog = new EditNameDialog();
//            editNameDialog.show(fm, "fragment_edit_name");
//        }
//
//        @Override
//        public void onFinishEditDialog(String inputText) {
//            Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
//        }
//    }

}



