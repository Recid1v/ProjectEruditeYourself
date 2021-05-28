package ern.recid1v.erudite_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void onClickCompleteTest(View view) {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnClickWordBank(View view) {
        Intent intent = new Intent(this, TableWordBankActivity.class);
        startActivity(intent);
        finish();
    }
}