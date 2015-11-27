package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bundle the database if it isn't already.
        DatabaseBundler dbBundle = new DatabaseBundler(this);
        dbBundle.bundle("scoresdb");
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
