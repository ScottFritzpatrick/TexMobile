package ca.alexcomeau.texmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bundle the database if it isn't already.
        DatabaseBundler dbBundle = new DatabaseBundler(getPackageName(), getBaseContext());
        dbBundle.bundle("scoresdb");
    }
}
