package com.dobregons.android.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

public class DialogoSeleccion extends DialogFragment {
    private TicTacToeGame.DifficultyLevel difficultyLevel;

    private static Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] levels = {
                getResources().getString(R.string.difficulty_easy),
                getResources().getString(R.string.difficulty_harder),
                getResources().getString(R.string.difficulty_expert)};
//        final String[] items = {"Español", "Inglés", "Francés"};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

//        builder.setTitle("Selección")
//                .setItems(levels, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        TicTacToeGame ticTacToeGame = new TicTacToeGame();
//
//                        Log.i("Dialogos", "Opción elegida: " + levels[item]);
//                        dialog.dismiss();
//                    }
//                });

//        builder.setTitle("Selección")
//        .setMultiChoiceItems(items, null,
//        		new DialogInterface.OnMultiChoiceClickListener() {
//        	public void onClick(DialogInterface dialog, int item, boolean isChecked) {
//                Log.i("Dialogos", "Opción elegida: " + items[item]);
//            }
//	    });

        builder.setTitle("Selección")
           .setSingleChoiceItems(levels, -1,
        		   new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    TicTacToeGame ticTacToeGame = new TicTacToeGame();

                    switch(item)
                    {
                        case 0:
                            difficultyLevel = TicTacToeGame.DifficultyLevel.Easy;
                            break;
                        case 1:
                            difficultyLevel = TicTacToeGame.DifficultyLevel.Harder;
                            break;
                        case 2:
                            difficultyLevel = TicTacToeGame.DifficultyLevel.Expert;
                            break;
                    }

                    ticTacToeGame.setDifficultyLevel(difficultyLevel);

	    	        Log.i("Dialogos", "Opción elegida: " + levels[item]);

	    	    }
	    	});

        return builder.create();
    }
}
