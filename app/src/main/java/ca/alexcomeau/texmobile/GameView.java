package ca.alexcomeau.texmobile;

import android.content.Context;
import android.content.Intent;
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
    private Context context;
    private int pixels;

    public GameView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        context = ctx;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
    }

    public void setupGame(int start, int end, int pixels)
    {
        game = new GameManager();
        game.start(start, end);
        this.pixels = 40;
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
        if(game.getRedraw())
        {
            canvas.drawColor(Color.BLACK);
            Paint paint = new Paint();
            int[][] colors = game.getStack();

            // Paint the stack onto the canvas. Top two rows aren't drawn.
            for (int i = 0; i <= 20; i++)
                for (int j = 0; j < 10; j++)
                {
                    paint.setColor(colors[i][j]);

                    // The canvas is already black so we don't have to draw those.
                    if (paint.getColor() != Color.BLACK)
                        canvas.drawRect(j * pixels,                 // Left
                                        (20 - i) * pixels - pixels, // Top
                                        j * pixels + pixels,        // Right
                                        (20 - i) * pixels,          // Bottom
                                        paint);                     // Color
                }

            // Paint the active piece onto the canvas
            Block currentPiece = game.getCurrentBlock();
            if (currentPiece != null)
            {
                paint.setColor(currentPiece.getBlockColor());
                for (Point coord : currentPiece.getAbsoluteCoordinates())
                {
                    canvas.drawRect(coord.x * pixels,
                                    (20 - coord.y) * pixels - pixels,
                                    coord.x * pixels + pixels,
                                    (20 - coord.y) * pixels,
                                    paint);
                }
            }
        }

        if(game.getGameOver() != null)
        {
            // Stop the thread
            thread.setRunning(false);
            // Draw a message
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(pixels);
            canvas.drawText(context.getString(R.string.gameover), pixels, pixels, paint);
            canvas.drawText(context.getString(R.string.pressAny), pixels, pixels * 3, paint);
        }
    }
}
