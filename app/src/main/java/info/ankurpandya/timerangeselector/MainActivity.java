package info.ankurpandya.timerangeselector;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView txt;

    Date fromTime = null;
    Date toTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = findViewById(R.id.txt);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickTimeRange.Builder(MainActivity.this)
                        .setTitle("Aaloo")
                        .setSubTitle("Lelo")
                        .setFromTime(MainActivity.this.fromTime)
                        .setToTime(MainActivity.this.toTime)
                        .setListener(new PickTimeRange.Listener() {
                            @Override
                            public void onTimeRangeSelected(Date fromTime, Date toTime) {
                                MainActivity.this.fromTime = fromTime;
                                MainActivity.this.toTime = toTime;
                                updateText();
                            }
                        })
                        .build().show();
            }
        });
        updateText();
    }

    private void updateText() {
        txt.setText(
                fromTime + " \n" +
                        toTime
        );
    }
}