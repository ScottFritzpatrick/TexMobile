package ca.alexcomeau.texmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.Hashtable;

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
    private Hashtable<Integer, NinePatchDrawable> htShapes;
    private Bitmap stackState;
    int width, height;

    public GameView(Context ctx, AttributeSet attrs)
    {
        super(ctx, attrs);
        context = ctx;
        getHolder().addCallback(this);
        setFocusable(true);
        gameStarted = false;
        // Initialize
        stackState = Bitmap.createBitmap(1,1, Bitmap.Config.RGB_565);
    }

    @Override
    public void onMeasure(int widthSpec, int heightSpec)
    {
        // This override is so the view will fill all available space, while also maintaining its aspect ratio.
        int heightMode = MeasureSpec.getMode(heightSpec);
        int widthMode = MeasureSpec.getMode(widthSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);

        if (widthMode == MeasureSpec.EXACTLY)
            width = widthSize;
        else if (widthMode == MeasureSpec.AT_MOST)
        {
            width = Math.min(heightSize / 2, widthSize);
            // Trim excess. There's 10 columns, so it needs to be divisible by 10
            width = width - (width % 10);
        }
        else
            width = Integer.MAX_VALUE;

        if (heightMode == MeasureSpec.EXACTLY)
            height = heightSize;
        else if (heightMode == MeasureSpec.AT_MOST)
        {
            height = Math.min(width * 2, heightSize);
            // Trim excess. There's 20 rows, so it needs to be divisible by 20
            height = height - (height % 20);
        }
        else
            height = Integer.MAX_VALUE;

        this.setMeasuredDimension(width, height);
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

        htShapes = new Hashtable<>();

        // Get all the drawables
        htShapes.put(Color.BLUE, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_blue));
        htShapes.put(Color.GREEN, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_green));
        htShapes.put(Color.RED, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_red));
        htShapes.put(Color.YELLOW, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_yellow));
        htShapes.put(Color.rgb(255,165,0), (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_orange));
        htShapes.put(Color.CYAN, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_cyan));
        htShapes.put(Color.BLUE, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_blue));
        htShapes.put(Color.MAGENTA, (NinePatchDrawable) ContextCompat.getDrawable(activity.getBaseContext(), R.drawable.block_magenta));
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
        {
            game.advanceFrame(activity.getInput());
        }
    }

    public void render(Canvas canvas)
    {
        if(getRedraw() && gameStarted)
        {
            // Set up the rectangle width based on the canvas size
            rectWidth = canvas.getWidth() / 10;

            NinePatchDrawable tile;
            canvas.drawColor(Color.BLACK);

            if(game.getStackRedraw())
            {
                stackState = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
                Canvas stack = new Canvas(stackState);
                stack.drawColor(Color.BLACK);

                int[][] colors = game.getStack();
                // Paint the stack onto the canvas. Top two rows aren't drawn.
                for(int i = 0; i <= 20; i++)
                    for(int j = 0; j < 10; j++)
                    {
                        // The canvas is already black so we don't have to draw those.
                        if(colors[i][j] != Color.BLACK)
                        {
                            tile = htShapes.get(colors[i][j]);
                            tile.setBounds(j * rectWidth,
                                    (20 - i) * rectWidth - rectWidth,
                                    j * rectWidth + rectWidth,
                                    (20 - i) * rectWidth);
                            tile.draw(stack);
                        }
                    }
            }

            canvas.drawBitmap(stackState, 0, 0, new Paint());

            // Paint the active piece onto the canvas
            Block currentPiece = game.getCurrentBlock();
            if (currentPiece != null)
            {
                tile = htShapes.get(currentPiece.getBlockColor());
                for (Point coord : currentPiece.getAbsoluteCoordinates())
                {
                    tile.setBounds(coord.x * rectWidth,
                            (20 - coord.y) * rectWidth - rectWidth,
                            coord.x * rectWidth + rectWidth,
                            (20 - coord.y) * rectWidth);
                    tile.draw(canvas);
                }
            }

            // Update the text views
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    txtScore.setText(String.format(activity.getString(R.string.score), game.getScore()));
                    txtLevel.setText(String.format(activity.getString(R.string.level), game.getLevel(), game.getMaxLevel()));
                    if(game.getSoundEffectToPlay() > -1)
                        activity.playSound(game.getSoundEffectToPlay());
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
    public boolean getRedraw() { return game.getPieceRedraw() || game.getStackRedraw(); }
}
