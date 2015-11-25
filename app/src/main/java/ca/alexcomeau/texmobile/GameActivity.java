package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import ca.alexcomeau.texmobile.blocks.Block;

public class GameActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private TextView txtScore, txtLevel;
    private GameManager game;
    // Rectangle width/height in dp
    private float pixels;
    private Timer calcTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        surfaceView = (SurfaceView) findViewById(R.id.svBoard);
        surfaceHolder = surfaceView.getHolder();
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
        surfaceView.setLayoutParams(new android.widget.FrameLayout.LayoutParams((int) w, (int) h));
        
        // Convert from dp to pixels
        pixels = (int)(h / 20) * getResources().getDisplayMetrics().density;

        if(savedInstanceState == null)
        {
            game = new GameManager();
            Bundle myBundle = getIntent().getExtras();
            game.start(myBundle.getInt("startLevel"), myBundle.getInt("endLevel"));
        }
        else
        {
            game = savedInstanceState.getParcelable("game");
        }

        calcTimer = new Timer();
        calcTimer.scheduleAtFixedRate(new GameTask(), 0, 34);
    }

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
        // Fill the canvas with black
        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        int[][] colors = game.getStack();
        int countRow, countColumn;
        countRow = 0;

        // Paint the stack onto the canvas
        for(int[] row : colors)
        {
            // The first two rows aren't displayed
            if(countRow > 1)
            {
                countColumn = 0;
                for (int i : row)
                {
                    // The canvas is already black so we don't have to draw those boxes
                    if (i != Color.BLACK)
                    {
                        paint.setColor(i);
                        canvas.drawRect(countColumn * pixels, // Left
                                        canvas.getHeight() - (countRow * pixels) - pixels, // Top
                                        (countColumn * pixels) + pixels, // Right
                                        canvas.getHeight() - (countRow * pixels), // Bottom
                                        paint);
                        // Next one over
                        countColumn++;
                    }
                }
            }
            countRow++;
        }

        // Paint the active piece onto the canvas
        Block currentPiece = game.getCurrentBlock();
        paint.setColor(currentPiece.getBlockColor());
        for(Point coord : currentPiece.getAbsoluteCoordinates())
        {
            canvas.drawRect(coord.x * pixels,
                    canvas.getHeight() - (coord.y * pixels) - pixels,
                    (coord.x * pixels) + pixels,
                    canvas.getHeight() - (coord.y * pixels),
                    paint);
        }

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
        if(result)
        {
            HighScore scores = new HighScore(this);
            scores.open();

            // Check if it's a new high score
            if(game.getScore() > scores.getLowestScore())
            {
                scores.close();
                Intent intent = new Intent("ca.alexcomeau.texmobile.EnterScore");
                intent.putExtra("score", game.getScore());
                intent.putExtra("time", game.getFrames());
                intent.putExtra("grade", game.getGrade());
                startActivity(intent);
            }
            else
            {
                scores.close();
                Intent intent = new Intent("ca.alexcomeau.texmobile.HighScore");
                startActivity(intent);
            }
        }
        else
        {
            Intent intent = new Intent("ca.alexcomeau.texmobile.HighScore");
            startActivity(intent);
        }
    }
}
