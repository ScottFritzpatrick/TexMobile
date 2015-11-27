package ca.alexcomeau.texmobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        ScoreDBManager scoreManager = new ScoreDBManager(this);
        scoreManager.open();
        List<Score> scores = scoreManager.getAllScores();
        scoreManager.close();

        TableLayout tl = (TableLayout) findViewById(R.id.tblScores);

        // Add all the scores to the table
        for(Score s : scores)
        {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp);

            TextView txtName = new TextView(this);
            txtName.setText(s.name);
            txtName.setLayoutParams(new TableRow.LayoutParams(1));

            TextView txtScore = new TextView(this);
            txtScore.setText(s.score + "");
            txtScore.setLayoutParams(new TableRow.LayoutParams(2));

            TextView txtTime = new TextView(this);
            txtTime.setText(s.time);
            txtTime.setLayoutParams(new TableRow.LayoutParams(3));

            TextView txtGrade = new TextView(this);
            txtGrade.setText(s.grade);
            txtGrade.setLayoutParams(new TableRow.LayoutParams(4));

            row.addView(txtName);
            row.addView(txtScore);
            row.addView(txtTime);
            row.addView(txtGrade);

            tl.addView(row);
        }
    }
}
