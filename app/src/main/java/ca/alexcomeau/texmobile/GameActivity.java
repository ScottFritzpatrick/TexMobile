package ca.alexcomeau.texmobile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Timer;
import java.util.TimerTask;

import ca.alexcomeau.texmobile.blocks.Block;

public class GameActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private GameManager game;
    private int width, height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        surfaceView = (SurfaceView) findViewById(R.id.svBoard);
        surfaceHolder = surfaceView.getHolder();
        width = 10;
        height = 10;

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

        Timer calcTimer = new Timer();
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
        Canvas c = surfaceHolder.lockCanvas();
        // Fill the canvas with black
        c.drawColor(Color.BLACK);
        Paint p = new Paint();
        int[][] colors = game.getStack();
        int countRow, countColumn;
        countRow = 0;


        // Paint the stack onto the canvas
        for(int[] row : colors)
        {
            countColumn = 0;
            for (int i : row) {
                // The canvas is already black so we don't have to draw those boxes
                if(i != Color.BLACK)
                {
                    p.setColor(i);
                    c.drawRect(countColumn * width, // Left
                            c.getHeight() - (countRow * height) - height, // Top
                            (countColumn * width) + width, // Right
                            c.getHeight() - (countRow * height), // Bottom
                            p);
                    // Next one over
                    countColumn++;
                }
            }
            countRow++;
        }

        // Paint the active piece onto the canvas
        Block currentPiece = game.getCurrentBlock();
        p.setColor(currentPiece.getBlockColor());
        for(Coordinate coord : currentPiece.getAbsoluteCoordinates())
        {
            c.drawRect(coord.x * width,
                    c.getHeight() - (coord.y * height) - height,
                    (coord.x * width) + width,
                    c.getHeight() - (coord.y * height),
                    p);
        }

        // Give the canvas back
        surfaceHolder.unlockCanvasAndPost(c);
    }
}
