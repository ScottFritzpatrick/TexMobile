package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements Serializable{
    private GameView gameView;
    private String input;
    private MediaPlayer mp;
    private SoundPool sp;
    private int[] soundEffects;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        input = "";
        gameView = (GameView) findViewById(R.id.svBoard);
        mp = MediaPlayer.create(this, R.raw.all_of_us);
        mp.setVolume(0.5f, 0.5f);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaplayer)
            {
                mediaplayer = MediaPlayer.create(getBaseContext(), R.raw.all_of_us);
                mediaplayer.start();
            }
        });

        if(savedInstanceState == null)
        {
            Intent intent = getIntent();
            gameView.setupGame(intent.getIntExtra("startLevel", 0), intent.getIntExtra("maxLevel", 999), this);
        }
        else
        {
            gameView.setupGame((GameManager) savedInstanceState.getParcelable("game"), this);
            mp.seekTo(savedInstanceState.getInt("songPosition"));
        }

        mp.start();

        sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundEffects = new int[2];
        soundEffects[0] = sp.load(this, R.raw.piece_lock, 1);
        soundEffects[1] = sp.load(this, R.raw.line_clear, 1);

        // Wire up all the buttons
        List<Button> btns = new ArrayList<>();
        btns.add((Button) findViewById(R.id.btnDrop));
        btns.add((Button) findViewById(R.id.btnDown));
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
    }

    public void playSound(int i)
    {
        sp.play(soundEffects[i], 1, 1, 1, 0, 1.0f);
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
        else
        {
            Intent intent = new Intent("ca.alexcomeau.texmobile.HighScores");
            finish();
            startActivity(intent);
        }
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

            if(Integer.toString(seconds % 60).length() == 1)
                time = (seconds / 60) + ":0" + (seconds % 60);
            else
                time = (seconds / 60) + ":" + (seconds % 60);

            // Write the high score to the database
            ScoreDBManager scores = new ScoreDBManager(this);
            scores.open();
            scores.writeScore(data.getStringExtra("name"), game.getScore(), time, game.getGrade());
            scores.close();

            Intent intent = new Intent("ca.alexcomeau.texmobile.HighScores");
            finish();
            startActivity(intent);
        }
    }

    public String getInput() { return input; }

    @Override
    protected void onPause()
    {
        mp.pause();
        super.onPause();
        gameView.stop();
    }

    @Override
    protected void onResume()
    {
        mp.start();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable("game", gameView.getGame());
        outState.putInt("songPosition", mp.getCurrentPosition());
    }

    @Override
    protected void onDestroy()
    {
        mp.release();
        sp.release();
        mp = null;
        sp = null;
        super.onDestroy();
    }
}
