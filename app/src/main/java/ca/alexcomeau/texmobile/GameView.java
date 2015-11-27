package ca.alexcomeau.texmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import ca.alexcomeau.texmobile.blocks.Block;

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread thread;
    private GameManager game;
    private Context context;
    private GameActivity activity;
    private TextView txtScore;
    private TextView txtLevel;
    private int rectWidth;
    private boolean gameStarted;

    public GameView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        context = ctx;
        getHolder().addCallback(this);
        setFocusable(true);
        gameStarted = false;
    }

    public void setupGame(int start, int end, GameActivity activity)
    {
        game = new GameManager();
        game.start(start, end);

        setupGame(game, activity);
    }

    public void setupGame(GameManager game, GameActivity activity)
    {
        this.game = game;
        this.activity = activity;

        txtScore = (TextView) activity.findViewById(R.id.txtScore);
        txtLevel = (TextView) activity.findViewById(R.id.txtLevel);

        gameStarted = true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // Set up the thread when the surface is ready for it
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

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
    protected void onDraw(Canvas canvas) { }

    public void update()
    {
        if(gameStarted)
            game.advanceFrame(activity.getInput());
    }

    public void render(Canvas canvas)
    {
        if(game.getRedraw() && gameStarted)
        {
            // Set up the rectangle width based on the canvas size
            if(rectWidth == 0)
                rectWidth = canvas.getWidth() / 10;

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
                        canvas.drawRect(j * rectWidth,                 // Left
                                        (20 - i) * rectWidth - rectWidth, // Top
                                        j * rectWidth + rectWidth,        // Right
                                        (20 - i) * rectWidth,          // Bottom
                                        paint);                     // Color
                }

            // Paint the active piece onto the canvas
            Block currentPiece = game.getCurrentBlock();
            if (currentPiece != null)
            {
                paint.setColor(currentPiece.getBlockColor());
                for (Point coord : currentPiece.getAbsoluteCoordinates())
                {
                    canvas.drawRect(coord.x * rectWidth,
                                    (20 - coord.y) * rectWidth - rectWidth,
                                    coord.x * rectWidth + rectWidth,
                                    (20 - coord.y) * rectWidth,
                                    paint);
                }
            }

            // Update the text views
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    txtScore.setText(String.format(activity.getString(R.string.score), game.getScore()));
                    txtLevel.setText(String.format(activity.getString(R.string.level), game.getLevel(), game.getMaxLevel()));
                }
            });
        }

        if(game.getGameOver() != null)
        {
            // Stop the thread
            thread.setRunning(false);
            // Draw a message
            Paint paint = new Paint();
            paint.setTextSize(rectWidth);

            // shadow
            paint.setColor(Color.DKGRAY);
            canvas.drawText(context.getString(R.string.gameover), rectWidth + 2, rectWidth + 2, paint);
            canvas.drawText(context.getString(R.string.pressAny), rectWidth + 2, rectWidth * 3 + 2, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText(context.getString(R.string.gameover), rectWidth, rectWidth, paint);
            canvas.drawText(context.getString(R.string.pressAny), rectWidth, rectWidth * 3, paint);

        }
    }

    public GameManager getGame() { return game; }
    public void stop() { thread.setRunning(false); }
    public void start() { thread.setRunning(true); }
    public boolean getRedraw() { return game.getRedraw(); }
}
