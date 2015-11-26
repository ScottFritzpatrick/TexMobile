package ca.alexcomeau.texmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import ca.alexcomeau.texmobile.blocks.Block;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread thread;
    private GameManager game;
    private int pixels;

    public GameView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    public void setupGame(int start, int end, int pixels)
    {
        game = new GameManager();
        game.start(start, end);
        this.pixels = 50;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        while(retry)
        {
            try
            {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

    }

    public void update()
    {
        game.advanceFrame("right");
    }

    public void render(Canvas canvas)
    {
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
                    for(int i : row)
                    {
                        // The canvas is already black so we don't have to draw those boxes
                        if(i != Color.BLACK)
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
            if(currentPiece != null)
            {
                paint.setColor(Color.WHITE);
                for(Point coord : currentPiece.getAbsoluteCoordinates())
                {
                    Log.d("erzz","coord.y = " + coord.y);
                    canvas.drawRect(coord.x * pixels,
                            canvas.getHeight() - (coord.y * pixels) - pixels,
                            (coord.x * pixels) + pixels,
                            canvas.getHeight() - (coord.y * pixels),
                            paint);
                }
            }

    }
}
