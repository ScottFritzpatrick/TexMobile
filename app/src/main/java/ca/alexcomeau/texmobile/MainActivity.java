package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bundle the database if it isn't already.
        DatabaseBundler dbBundle = new DatabaseBundler(this);
        dbBundle.bundle("scoresdb");

        // Make the links clickable in the credits
        TextView txtCredits = (TextView) findViewById(R.id.txtCredits);
        txtCredits.setMovementMethod(LinkMovementMethod.getInstance());

        mp = MediaPlayer.create(this, R.raw.chibi_ninja);
        mp.setVolume(0.7f, 0.7f);

        // setLooping doesn't work with the "AwesomePlayer"
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaplayer)
            {
                mediaplayer.seekTo(0);
                mediaplayer.start();
            }
        });

        // Go back to where the song was, if it had already been playing
        if(savedInstanceState != null)
            mp.seekTo(savedInstanceState.getInt("songPosition"));
        else
        {
            // If this was started from the HighScore activity, keep the song position.
            Intent i = getIntent();
            mp.seekTo(i.getIntExtra("songPosition", 0));
        }

        mp.start();

    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("songPosition", mp.getCurrentPosition());
    }

    @Override
    protected void onDestroy()
    {
        mp.release();
        mp = null;
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        mp.pause();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        mp.start();
        super.onResume();
    }

    public void btnClick(View v)
    {
        Intent intent = new Intent("ca.alexcomeau.texmobile.Game");
        //TODO: Add a drop down or something to select the starting level.
        intent.putExtra("startLevel", 0);
        intent.putExtra("maxLevel", Integer.parseInt(v.getTag().toString()));
        finish();
        startActivity(intent);
    }
}
