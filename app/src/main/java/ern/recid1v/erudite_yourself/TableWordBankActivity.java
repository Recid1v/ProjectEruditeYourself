package ern.recid1v.erudite_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


public class TableWordBankActivity extends AppCompatActivity {

    Cursor cursor;
    View.OnClickListener onClickWordListener;
    SQLiteDatabase mDb;
    TreeMap<String, String> TM_word_bank;
    LinearLayout mainLinearLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_word_bank);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Подключаем бд
        DatabaseHelper mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        cursor = mDb.rawQuery("SELECT * FROM word_bank ORDER BY word",
                null);

        TM_word_bank = new TreeMap<>();

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToNext();
            TM_word_bank.put(cursor.getString(cursor.getColumnIndex("word")),
                    cursor.getString(cursor.getColumnIndex("value")));
        }
        cursor.close();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Банк слов");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_baseline_arrow_back_24, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableWordBankActivity.this,
                        MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mainLinearLayout = findViewById(R.id.LL_words);

        final float scale = getResources().getDisplayMetrics().density;

        //заполняем ScrollView словами - начало
        for (Map.Entry<String, String> entry: TM_word_bank.entrySet()){

            //создаем контейнер-строку
            RelativeLayout RL_line = new RelativeLayout(this);
            RelativeLayout.LayoutParams RL_line_params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            RL_line_params.setMargins(0, (int) (5 * scale + 0.5f), 0, 0);

            RL_line.setBackgroundResource(R.drawable.color_for_rl);
            mainLinearLayout.addView(RL_line, RL_line_params);

            //создаём слово
            TextView TV_word = new TextView(this);
            TV_word.setText(entry.getKey());
            TV_word.setTextColor(Color.WHITE);
            TV_word.setTypeface(Typeface.DEFAULT_BOLD);
            TV_word.setTextSize(30);
            TV_word.setId(View.generateViewId());
            TV_word.setLayoutParams(new LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            RL_line.addView(TV_word);

            //обработка нажатия на RL_line - начало
            RL_line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog_word_mean = new Dialog(TableWordBankActivity.this,
                            R.style.DialogWordMeaningStyle);

                    //скрываем заголовок
                    dialog_word_mean.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog_word_mean.setContentView(R.layout.word_meaning_dialog);


                    //делаем фон прозрачным
                    dialog_word_mean.getWindow().setBackgroundDrawable(
                            new ColorDrawable(Color.TRANSPARENT));

                    TextView dialog_tv_mean = dialog_word_mean.findViewById(R.id.mean);
                    TextView dialog_tv_word = dialog_word_mean.findViewById(R.id.word);

                    dialog_tv_word.setText(TV_word.getText().toString());

                    dialog_tv_mean.setText(TM_word_bank.get(TV_word.getText().toString()));

                    dialog_word_mean.show();
                }
            });
            //обработка нажатия на RL_line - конец

            //создаем кнопку-значок редактирования
            ImageButton ib_edit = new ImageButton(this);
            ib_edit.setId(View.generateViewId());
            ib_edit.setBackgroundResource(R.drawable.button_border);
            ib_edit.setImageResource(R.drawable.ic_baseline_edit_24);
            RelativeLayout.LayoutParams ib_edit_params = new RelativeLayout.LayoutParams(
                    (int) (40 * scale + 0.5f),
                    (int) (40 * scale + 0.5f));
            ib_edit_params.addRule(RelativeLayout.ALIGN_PARENT_END);
            ib_edit.setPadding((int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f),
                    (int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f));

            RL_line.addView(ib_edit, ib_edit_params);

            //обработка нажатия на кнопку редактирования - начало
            ib_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String past_word = TV_word.getText().toString();
                    String past_value = TM_word_bank.get(past_word);

                    Dialog dialog_edit_word = new Dialog(TableWordBankActivity.this);

                    dialog_edit_word.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog_edit_word.setContentView(R.layout.add_word_dialog);

                    dialog_edit_word.getWindow().setBackgroundDrawable(
                            new ColorDrawable(Color.TRANSPARENT));

                    EditText et_input_word = dialog_edit_word.findViewById(R.id.input_word);
                    et_input_word.setText(past_word);
                    et_input_word.setTextColor(Color.WHITE);

                    EditText et_input_value = dialog_edit_word.findViewById(R.id.input_value);
                    et_input_value.setText(past_value);
                    et_input_value.setTextColor(Color.WHITE);

                    Button btn_edit = dialog_edit_word.findViewById(R.id.btn_add);
                    btn_edit.setText("Изменить");
                    dialog_edit_word.show();

                    //обработка нажатия на кнопку "Изменить" в окне редактирования - начало
                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String input_word = et_input_word.getText().toString();
                            String input_value = et_input_value.getText().toString();

                            //если слово и значение остались прежними
                            if (input_word.equals(past_word) && input_value.equals(past_value)){
                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                        "Пожалуйста, измените данные", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            //если строки ввода пусты
                            else if (input_word.length() == 0 || input_value.length() == 0){
                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                        "Поля ввода не должны быть пустыми", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else{
                                ContentValues cv = new ContentValues();
                                cv.put("word", input_word);
                                cv.put("value", input_value);

                                Cursor word_id_cursor = mDb.rawQuery(
                                        "SELECT _id FROM word_bank WHERE word = '" +
                                                past_word + "'",null);
                                word_id_cursor.moveToNext();
                                long result = mDb.update("word_bank", cv,
                                        "_id = ?", new String[]{word_id_cursor.getString
                                                (word_id_cursor.getColumnIndex("_id"))});
                                word_id_cursor.close();
                                //если слово успешно изменено - начало
                                if(result>0) {
                                    if (input_word.equals(past_word))
                                    TM_word_bank.put(past_word, input_value);
                                    else {
                                        TM_word_bank.remove(past_word);
                                        TM_word_bank.put(input_word, input_value);
                                    }
                                    TV_word.setText(input_word);

                                    TextView TV_edit = new TextView(
                                            TableWordBankActivity.this);
                                    TV_edit.setText("Изменено!");
                                    TV_edit.setTypeface(null, Typeface.ITALIC);
                                    TV_edit.setTextColor(Color.WHITE);
                                    TV_edit.setTextSize(20);

                                    RelativeLayout.LayoutParams TV_edit_params =
                                            new RelativeLayout.LayoutParams
                                                    (RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                            RelativeLayout.LayoutParams.
                                                                    WRAP_CONTENT);
                                    TV_edit_params.setMarginStart((int) (10 * scale + 0.5f));
                                    TV_edit_params.addRule(RelativeLayout.RIGHT_OF, TV_word.getId());
                                    TV_edit_params.addRule(RelativeLayout.ALIGN_BOTTOM, TV_word.getId());


                                    RL_line.addView(TV_edit, TV_edit_params);

                                    dialog_edit_word.cancel();

                                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                                            "Слово успешно изменено!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                //если слово успешно изменено - конец
                                else{
                                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                                            "Что-то пошло не так..", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                    });
                    //обработка нажатия на кнопку "Изменить" в окне редактирования - конец

                }
            });
            //обработка нажатия на кнопку редактирования - конец

            //создаем кнопку-значок удаления
            ImageButton ib_delete = new ImageButton(this);
            ib_delete.setBackgroundResource(R.drawable.button_border);
            ib_delete.setImageResource(R.drawable.ic_baseline_delete_24);
            RelativeLayout.LayoutParams ib_delete_params = new RelativeLayout.LayoutParams(
                    (int) (40 * scale + 0.5f),
                    (int) (40 * scale + 0.5f));
            ib_delete_params.addRule(RelativeLayout.LEFT_OF, ib_edit.getId());
            ib_delete_params.setMarginEnd((int) (5 * scale + 0.5f));
            ib_edit.setPadding((int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f),
                    (int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f));

            RL_line.addView(ib_delete, ib_delete_params);

            //обработка нажатия на кнопку-значок удаления - начало
            ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog_you_sure = new Dialog(TableWordBankActivity.this);
                    dialog_you_sure.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_you_sure.setContentView(R.layout.exit_test_dialog);
                    dialog_you_sure.getWindow().setBackgroundDrawable(new ColorDrawable(
                            Color.TRANSPARENT));

                    TextView TV_you_sure = dialog_you_sure.findViewById(R.id.text_answer);
                    TV_you_sure.setText("Вы уверены?");
                    TV_you_sure.setTextSize(30);

                    Button yes = dialog_you_sure.findViewById(R.id.btn_yes);

                    dialog_you_sure.show();
                    //обработка нажатия на кнопку "Да" - начало
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Cursor word_id_cursor = mDb.rawQuery(
                                    "SELECT _id FROM word_bank WHERE word = '" +
                                            TV_word.getText().toString() + "'",null);

                            word_id_cursor.moveToNext();

                            long result = mDb.delete("word_bank",
                                    "_id = ?", new String[]{word_id_cursor.getString
                                            (word_id_cursor.getColumnIndex("_id"))});
                            word_id_cursor.close();
                            if (result > 0){
                                mainLinearLayout.removeView(RL_line);
                                TM_word_bank.remove(TV_word.getText().toString());
                                dialog_you_sure.cancel();
                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                        "Слово успешно удалено!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else{
                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                        "Что-то пошло не так..", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                    //обработка нажатия на кнопку "Да" - конец

                    Button no = dialog_you_sure.findViewById(R.id.btn_no);

                    //обработка нажатия на кнопку "Нет" - начало
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_you_sure.cancel();
                        }
                    });
                    //обработка нажатия на кнопку "Нет" - конец


                }
            });
            //обработка нажатия на кнопку-значок удаления - конец
        }
        //заполняем ScrollView словами - конец
    }

    //обработка нажатия на кнопку "+" - начало
    public void onClickAdd(View view) {

        final float scale = getResources().getDisplayMetrics().density;

        Dialog dialog_add_word = new Dialog(TableWordBankActivity.this);

        dialog_add_word.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_add_word.setContentView(R.layout.add_word_dialog);
        dialog_add_word.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btn_add = dialog_add_word.findViewById(R.id.btn_add);
        btn_add.setText("Добавить");

        dialog_add_word.show();

        EditText et_input_word = dialog_add_word.findViewById(R.id.input_word);
        EditText et_input_value = dialog_add_word.findViewById(R.id.input_value);

        //обработка нажатия на кнопку "Добавить" в окне добавления слова - начало
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input_word = et_input_word.getText().toString();
                String input_value = et_input_value.getText().toString();

                //если строки ввода пусты
                if (input_word.length() == 0 || input_value.length() == 0){
                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                            "Поля ввода не должны быть пустыми", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (TM_word_bank.get(input_word) != null || checkingValue(input_value)){
                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                            "Слово и значение должны быть уникальны", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    ContentValues cv = new ContentValues();
                    cv.put("word", input_word);
                    cv.put("value", input_value);

                    long result = mDb.insert("word_bank",null,cv);
                    //если слово успешно добавлено - начало
                    if(result>0){

                        mainLinearLayout.removeAllViews();

                        TM_word_bank.put(input_word, input_value);

                        //заполняем ScrollView словами - начало
                        for (Map.Entry<String, String> entry: TM_word_bank.entrySet()){

                            //создаем контейнер-строку
                            RelativeLayout RL_line = new RelativeLayout(TableWordBankActivity.this);
                            RelativeLayout.LayoutParams RL_line_params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT);
                            RL_line_params.setMargins(0, (int) (5 * scale + 0.5f), 0, 0);

                            RL_line.setBackgroundResource(R.drawable.color_for_rl);
                            mainLinearLayout.addView(RL_line, RL_line_params);

                            //создаём слово
                            TextView TV_word = new TextView(TableWordBankActivity.this);
                            TV_word.setText(entry.getKey());
                            TV_word.setTextColor(Color.WHITE);
                            TV_word.setTypeface(Typeface.DEFAULT_BOLD);
                            TV_word.setTextSize(30);
                            TV_word.setId(View.generateViewId());
                            TV_word.setLayoutParams(new LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT));
                            RL_line.addView(TV_word);

                            TextView TV_new_or_edit = new TextView(
                                    TableWordBankActivity.this);
                            TV_new_or_edit.setText("Новое!");
                            TV_new_or_edit.setTypeface(null, Typeface.ITALIC);
                            TV_new_or_edit.setTextSize(20);
                            TV_new_or_edit.setTextColor(Color.WHITE);

                            RelativeLayout.LayoutParams TV_new_or_edit_params =
                                    new RelativeLayout.LayoutParams
                                            (RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                    RelativeLayout.LayoutParams.
                                                            WRAP_CONTENT);
                            TV_new_or_edit_params.setMarginStart((int) (10 * scale + 0.5f));
                            TV_new_or_edit_params.addRule(
                                    RelativeLayout.RIGHT_OF, TV_word.getId());
                            TV_new_or_edit_params.addRule(
                                    RelativeLayout.ALIGN_BOTTOM, TV_word.getId());

                            //Добавить к слову "Новое!" - начало
                            if (entry.getKey().equals(
                                    input_word)){

                                TV_new_or_edit.setText("Новое!");

                                RL_line.addView(TV_new_or_edit, TV_new_or_edit_params);
                            }
                            //Добавить к слову "Новое!" - конец

                            //обработка нажатия на RL_line - начало
                            RL_line.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Dialog dialog_word_mean = new Dialog(TableWordBankActivity.this,
                                            R.style.DialogWordMeaningStyle);

                                    //скрываем заголовок
                                    dialog_word_mean.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                    dialog_word_mean.setContentView(R.layout.word_meaning_dialog);


                                    //делаем фон прозрачным
                                    dialog_word_mean.getWindow().setBackgroundDrawable(
                                            new ColorDrawable(Color.TRANSPARENT));

                                    TextView dialog_tv_mean = dialog_word_mean.findViewById(R.id.mean);
                                    TextView dialog_tv_word = dialog_word_mean.findViewById(R.id.word);

                                    dialog_tv_word.setText(TV_word.getText().toString());

                                    dialog_tv_mean.setText(TM_word_bank.get(TV_word.getText().toString()));

                                    dialog_word_mean.show();
                                }
                            });
                            //обработка нажатия на RL_line - конец

                            //создаем кнопку-значок редактирования
                            ImageButton ib_edit = new ImageButton(TableWordBankActivity.this);
                            ib_edit.setId(View.generateViewId());
                            ib_edit.setBackgroundResource(R.drawable.button_border);
                            ib_edit.setImageResource(R.drawable.ic_baseline_edit_24);
                            RelativeLayout.LayoutParams ib_edit_params = new RelativeLayout.LayoutParams(
                                    (int) (40 * scale + 0.5f),
                                    (int) (40 * scale + 0.5f));
                            ib_edit_params.addRule(RelativeLayout.ALIGN_PARENT_END);
                            ib_edit.setPadding((int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f),
                                    (int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f));

                            RL_line.addView(ib_edit, ib_edit_params);

                            //обработка нажатия на кнопку редактирования - начало
                            ib_edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String past_word = TV_word.getText().toString();
                                    String past_value = TM_word_bank.get(past_word);

                                    Dialog dialog_edit_word = new Dialog(TableWordBankActivity.this);

                                    dialog_edit_word.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                    dialog_edit_word.setContentView(R.layout.add_word_dialog);

                                    dialog_edit_word.getWindow().setBackgroundDrawable(
                                            new ColorDrawable(Color.TRANSPARENT));

                                    EditText et_input_word = dialog_edit_word.findViewById(R.id.input_word);
                                    et_input_word.setText(past_word);
                                    et_input_word.setTextColor(Color.WHITE);

                                    EditText et_input_value = dialog_edit_word.findViewById(R.id.input_value);
                                    et_input_value.setText(past_value);
                                    et_input_value.setTextColor(Color.WHITE);

                                    Button btn_edit = dialog_edit_word.findViewById(R.id.btn_add);
                                    btn_edit.setText("Изменить");
                                    dialog_edit_word.show();

                                    //обработка нажатия на кнопку "Изменить" в окне редактирования - начало
                                    btn_edit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String input_word = et_input_word.getText().toString();
                                            String input_value = et_input_value.getText().toString();

                                            //если слово и значение остались прежними
                                            if (input_word.equals(past_word) && input_value.equals(past_value)){
                                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                        "Пожалуйста, измените данные", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            //если строки ввода пусты
                                            else if (input_word.length() == 0 || input_value.length() == 0){
                                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                        "Поля ввода не должны быть пустыми", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            else{
                                                ContentValues cv = new ContentValues();
                                                cv.put("word", input_word);
                                                cv.put("value", input_value);

                                                Cursor word_id_cursor = mDb.rawQuery(
                                                        "SELECT _id FROM word_bank WHERE word = '" +
                                                                past_word + "'",null);
                                                word_id_cursor.moveToNext();
                                                long result = mDb.update("word_bank", cv,
                                                        "_id = ?", new String[]{word_id_cursor.getString
                                                                (word_id_cursor.getColumnIndex("_id"))});
                                                word_id_cursor.close();
                                                //если слово успешно изменено - начало
                                                if(result>0) {
                                                    if (input_word.equals(past_word))
                                                        TM_word_bank.put(past_word, input_value);
                                                    else {
                                                        TM_word_bank.remove(past_word);
                                                        TM_word_bank.put(input_word, input_value);
                                                    }
                                                    TV_word.setText(input_word);

                                                    TV_new_or_edit.setText("Изменено!");

                                                    dialog_edit_word.cancel();

                                                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                            "Слово успешно изменено!", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                                //если слово успешно изменено - конец
                                                else{
                                                    Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                            "Что-то пошло не так..", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                }
                                            }
                                        }
                                    });
                                    //обработка нажатия на кнопку "Изменить" в окне редактирования - конец

                                }
                            });
                            //обработка нажатия на кнопку редактирования - конец

                            //создаем кнопку-значок удаления
                            ImageButton ib_delete = new ImageButton(TableWordBankActivity.this);
                            ib_delete.setBackgroundResource(R.drawable.button_border);
                            ib_delete.setImageResource(R.drawable.ic_baseline_delete_24);
                            RelativeLayout.LayoutParams ib_delete_params = new RelativeLayout.LayoutParams(
                                    (int) (40 * scale + 0.5f),
                                    (int) (40 * scale + 0.5f));
                            ib_delete_params.addRule(RelativeLayout.LEFT_OF, ib_edit.getId());
                            ib_delete_params.setMarginEnd((int) (5 * scale + 0.5f));
                            ib_edit.setPadding((int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f),
                                    (int) (5 * scale + 0.5f), (int) (5 * scale + 0.5f));

                            RL_line.addView(ib_delete, ib_delete_params);

                            //обработка нажатия на кнопку-значок удаления - начало
                            ib_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Dialog dialog_you_sure = new Dialog(TableWordBankActivity.this);
                                    dialog_you_sure.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog_you_sure.setContentView(R.layout.exit_test_dialog);
                                    dialog_you_sure.getWindow().setBackgroundDrawable(new ColorDrawable(
                                            Color.TRANSPARENT));

                                    TextView TV_you_sure = dialog_you_sure.findViewById(R.id.text_answer);
                                    TV_you_sure.setText("Вы уверены?");
                                    TV_you_sure.setTextSize(30);

                                    Button yes = dialog_you_sure.findViewById(R.id.btn_yes);

                                    dialog_you_sure.show();
                                    //обработка нажатия на кнопку "Да" - начало
                                    yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Cursor word_id_cursor = mDb.rawQuery(
                                                    "SELECT _id FROM word_bank WHERE word = '" +
                                                            TV_word.getText().toString() + "'",null);

                                            word_id_cursor.moveToNext();

                                            long result = mDb.delete("word_bank",
                                                    "_id = ?", new String[]{word_id_cursor.getString
                                                            (word_id_cursor.getColumnIndex("_id"))});
                                            word_id_cursor.close();
                                            if (result > 0){
                                                mainLinearLayout.removeView(RL_line);
                                                TM_word_bank.remove(TV_word.getText().toString());
                                                dialog_you_sure.cancel();
                                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                        "Слово успешно удалено!", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            else{
                                                Toast toast = Toast.makeText(TableWordBankActivity.this,
                                                        "Что-то пошло не так..", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                    });
                                    //обработка нажатия на кнопку "Да" - конец

                                    Button no = dialog_you_sure.findViewById(R.id.btn_no);

                                    //обработка нажатия на кнопку "Нет" - начало
                                    no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog_you_sure.cancel();
                                        }
                                    });
                                    //обработка нажатия на кнопку "Нет" - конец


                                }
                            });
                            //обработка нажатия на кнопку-значок удаления - конец

                        }
                        //заполняем ScrollView словами - конец
                        dialog_add_word.cancel();

                        Toast toast = Toast.makeText(TableWordBankActivity.this,
                                "Слово успешно добавлено!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    //если слово успешно добавлено - конец
                    else{
                        Toast toast = Toast.makeText(TableWordBankActivity.this,
                                "Что-то пошло не так..", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
        //обработка нажатия на кнопку "Добавить" в окне добавления слова - конец
    }
    //обработка нажатия на кнопку "+" - конец

    //проверка, есть ли введенное значение в word_bank - начало
    boolean checkingValue(String value){
        for (Map.Entry<String, String> entry: TM_word_bank.entrySet()) {
            if (value.equals(entry.getValue())){
                return true;
            }
        }
        return false;
    }
    //проверка, есть ли введенное значение в word_bank - конец


    @Override
    protected void onDestroy() {
        mDb.close();
        super.onDestroy();
    }
}