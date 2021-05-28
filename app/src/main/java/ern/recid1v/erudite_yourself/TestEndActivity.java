package ern.recid1v.erudite_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class TestEndActivity extends AppCompatActivity {

    TextView end_results;
    String result;
    Bundle args;
    String full_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_end);

        //убираем ActionBar
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        end_results = findViewById(R.id.text_end_results);

        args = getIntent().getExtras();
        result = args.getString("result");
        full_result = "Ваш результат: " + result + "/15";
        end_results.setText(full_result);
    }

    //обработка нажатия на кнопку "Пройти ещё раз"
    public void OnClickAgain(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    //обработка нажатия на кнопку "В меню"
    public void OnClickMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}