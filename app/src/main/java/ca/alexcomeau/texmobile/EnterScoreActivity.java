package ca.alexcomeau.texmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EnterScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_score);
    }

    public void btnEnterClick(View v)
    {
        // Send the entered name back to the game activity
        EditText txtName = (EditText) findViewById(R.id.txtName);
        Intent output = new Intent();
        output.putExtra("name", txtName.getText().toString());
        setResult(RESULT_OK, output);
        finish();
    }
}
