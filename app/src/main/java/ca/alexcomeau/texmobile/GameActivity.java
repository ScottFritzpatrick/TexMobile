package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private GameView gameView;
    private SurfaceHolder surfaceHolder;
    private TextView txtScore, txtLevel;
    private GameManager game;

    private Timer calcTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = (GameView) findViewById(R.id.svBoard);
        surfaceHolder = gameView.getHolder();
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtLevel = (TextView) findViewById(R.id.txtLevel);

        // Make the canvas fill the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point dimens = new Point();
        display.getSize(dimens);
        int screenheight = dimens.y;
        double h, w;

        if(display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180)
            h = screenheight * 0.7;
        else
            h = screenheight * 0.9;

        w = h * 0.5;
        //gameView.setLayoutParams(new android.widget.LinearLayout.LayoutParams((int) w, (int) h));

        // Convert from dp to pixels
        int pixels = (int)((h / 20) * getResources().getDisplayMetrics().density);

        if(savedInstanceState == null)
        {
            Bundle myBundle = getIntent().getExtras();
            gameView.setupGame(myBundle.getInt("startLevel"), myBundle.getInt("endLevel"), pixels);
        }
        else
        {
            game = savedInstanceState.getParcelable("game");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        calcTimer = new Timer();
        calcTimer.scheduleAtFixedRate(new GameTask(), 0, 34);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceDestroyed(SurfaceHolder holder){}

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("game", game);
    }

    private class GameTask extends TimerTask
    {
        public void run()
        {
            // Advance the game one frame
            game.advanceFrame("");

            // This becomes true if something happened in the game that requires a redraw, eg movement
            if(game.getRedraw())
                drawGame();
        }
    }

    public void drawGame()
    {
        Canvas canvas = surfaceHolder.lockCanvas();


        // Give the canvas back
        surfaceHolder.unlockCanvasAndPost(canvas);

        // Update the text fields
        txtScore.setText(String.format(getResources().getString(R.string.score), game.getScore()));
        txtLevel.setText(String.format(getResources().getString(R.string.level), game.getLevel(), game.getMaxLevel()));
        
        // Check if the game is done
        if(game.getGameOver() != null)
        {
            // Stop the timer task
            calcTimer.cancel();
            
            // Call the game over handling
            gameOver(game.getGameOver());
        }
    }

    private void gameOver(Boolean result)
    {
        // If they won the game...
        if(result)
        {
            // Check if it's a new high score
            ScoreDBManager scores = new ScoreDBManager(this);
            int lowestScore = scores.getLowestScore();
            scores.close();

            if(game.getScore() > lowestScore)
            {
                Intent intent = new Intent("ca.alexcomeau.texmobile.EnterScore");
                startActivityForResult(intent, 1);
            }
        }

        Intent intent = new Intent("ca.alexcomeau.texmobile.HighScore");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            // Convert the frames to minutes and seconds
            String time;
            int seconds = game.getFrames() / 30; // 30 frames per second
            time = (seconds / 60) + ":" + (seconds % 60);

            // Write the high score to the database
            ScoreDBManager scores = new ScoreDBManager(this);
            scores.writeScore(data.getStringExtra("name"), game.getScore(), time, game.getGrade());
            scores.close();
        }
    }
}
