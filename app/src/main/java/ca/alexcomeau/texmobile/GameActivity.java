package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements Serializable{
    private GameView gameView;
    private String input;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = "";
        gameView = (GameView) findViewById(R.id.svBoard);

        List<Button> btns = new ArrayList<>();
        btns.add((Button) findViewById(R.id.btnDrop1));
        btns.add((Button) findViewById(R.id.btnDrop2));
        btns.add((Button) findViewById(R.id.btnMoveLeft));
        btns.add((Button) findViewById(R.id.btnMoveRight));
        btns.add((Button) findViewById(R.id.btnRotateLeft));
        btns.add((Button) findViewById(R.id.btnRotateRight));

        for(Button b : btns)
                b.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        switch(event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                input = v.getTag().toString();
                                if(gameView.getGame().getGameOver() != null)
                                    gameOver();
                                return true;
                            case MotionEvent.ACTION_UP:
                                input = "";
                                return true;
                        }
                        return false;
                    }
                });

        // Make the canvas fill the screen
        /*Display display = getWindowManager().getDefaultDisplay();
        Point dimens = new Point();
        display.getSize(dimens);
        int screenheight = dimens.y;
        double h, w;

        if(display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            h = screenheight * 0.7;
        else
            h = screenheight * 0.9;

        w = h * 0.5;
        gameView.setLayoutParams(new android.widget.LinearLayout.LayoutParams((int) w, (int) h));
        */

        if(savedInstanceState == null)
        {
            Intent intent = getIntent();
            gameView.setupGame(intent.getIntExtra("startLevel", 0), intent.getIntExtra("endLevel", 999), this);
        }
        else
        {
            gameView.setupGame((GameManager) savedInstanceState.getParcelable("game"), this);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        gameView.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("game", gameView.getGame());
    }

    private void gameOver()
    {
        // Check if it's a new high score
        ScoreDBManager scores = new ScoreDBManager(this);
        scores.open();
        int lowestScore = scores.getLowestScore();
        scores.close();

        if(gameView.getGame().getScore() > lowestScore)
        {
            Intent intent = new Intent("ca.alexcomeau.texmobile.EnterScore");
            startActivityForResult(intent, 1);
        }

        Intent intent = new Intent("ca.alexcomeau.texmobile.HighScores");
        finish();
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            GameManager game = gameView.getGame();
            // Convert the frames to minutes and seconds
            String time;
            int seconds = game.getFrames() / 30; // 30 frames per second
            time = (seconds / 60) + ":" + (seconds % 60);

            // Write the high score to the database
            ScoreDBManager scores = new ScoreDBManager(this);
            scores.open();
            scores.writeScore(data.getStringExtra("name"), game.getScore(), time, game.getGrade());
            scores.close();
        }
    }

    public String getInput() { return input; }
}
