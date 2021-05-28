package ern.recid1v.erudite_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class TestActivity extends AppCompatActivity {

    HashMap<String, String> HM_word_bank;
    int level = 0;
    Random random;
    TextView Tv_word;
    Button btn_true_value;
    Button btn_false_value1;
    Button btn_false_value2;
    Button btn_idk;
    ImageButton btn_next;
    ImageButton btn_results;
    LinkedList<Object> keysLinkList;
    String word;
    String true_value;
    String false_value1;
    Cursor cursor;
    String false_value2;
    int index_btn_for_false_value1;
    int index_btn_for_false_value2;
    int index_btn_for_true_value;
    int true_answers = 0;
    Button[] btns_choices = new Button[3];
    LinearLayout[] progress = new LinearLayout[15];
    Dialog exit_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Tv_word = findViewById(R.id.tV_word);

        progress[0] = findViewById(R.id.point1);
        progress[1] = findViewById(R.id.point2);
        progress[2] = findViewById(R.id.point3);
        progress[3] = findViewById(R.id.point4);
        progress[4] = findViewById(R.id.point5);
        progress[5] = findViewById(R.id.point6);
        progress[6] = findViewById(R.id.point7);
        progress[7] = findViewById(R.id.point8);
        progress[8] = findViewById(R.id.point9);
        progress[9] = findViewById(R.id.point10);
        progress[10] = findViewById(R.id.point11);
        progress[11] = findViewById(R.id.point12);
        progress[12] = findViewById(R.id.point13);
        progress[13] = findViewById(R.id.point14);
        progress[14] = findViewById(R.id.point15);

        btns_choices[0] = findViewById(R.id.btn_choice_1);
        btns_choices[1] = findViewById(R.id.btn_choice_2);
        btns_choices[2] = findViewById(R.id.btn_choice_3);

        btn_next = findViewById(R.id.btn_next);

        //скрываем кнопку "Следующий"
        btn_next.setVisibility(View.INVISIBLE);

        btn_results = findViewById(R.id.btn_results);

        //скрываем кнопку "Результаты"
        btn_results.setVisibility(View.INVISIBLE);

        btn_idk = findViewById(R.id.btn_idk);

        //убираем ActionBar
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
        SQLiteDatabase mDb;
        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        cursor = mDb.rawQuery("SELECT * FROM word_bank ORDER BY RANDOM()",
                null);

        HM_word_bank = new HashMap<>();

        //заполняем HM_словарь
        for (int i = 0; i < 45; i++) {
            cursor.moveToNext();
            HM_word_bank.put(
                    cursor.getString(cursor.getColumnIndex("word")),
                    cursor.getString(cursor.getColumnIndex("value"))
            );
        }
        cursor.close();

        random = new Random();

        //инициализация LinkedList ключей типа Object
        LinkedList<Object> linkedList = new LinkedList<>();
        Collections.addAll(linkedList, HM_word_bank.keySet().toArray());
        keysLinkList = new LinkedList<>();
        for (int i = 0; i < 45; i++){
            Object key = linkedList.get(random.nextInt(linkedList.size()));
            keysLinkList.add(key);
            linkedList.remove(key);
        }

        //вытаскивание слова и его удаление из keysArrList
        word = keysLinkList.poll().toString();

        //вытаскивание для слова верного значения и его удаление из HM_word_bank
        true_value =  HM_word_bank.get(word);
        HM_word_bank.remove(word);

        //берем  2 определения и удаляем их
        false_value1 = HM_word_bank.get(keysLinkList.getFirst().toString());
        HM_word_bank.remove(keysLinkList.getFirst().toString());
        keysLinkList.removeFirst();

        false_value2 = HM_word_bank.get(keysLinkList.getFirst().toString());;
        HM_word_bank.remove(keysLinkList.getFirst().toString());
        keysLinkList.removeFirst();

        //устанавливаем слово на экран
        Tv_word.setText(word);

        random = new Random();
        //выбираем рандомно номера кнопок для определений
        index_btn_for_false_value1 = random.nextInt(3);
        index_btn_for_false_value2 = random.nextInt(3);
        while (index_btn_for_false_value1 == index_btn_for_false_value2) {
            index_btn_for_false_value1 = random.nextInt(3);
            index_btn_for_false_value2 = random.nextInt(3);
        }
        if (index_btn_for_false_value1 != 0 && index_btn_for_false_value2 != 0) {
            index_btn_for_true_value = 0;
        } else if (index_btn_for_false_value1 != 1 && index_btn_for_false_value2 != 1) {
            index_btn_for_true_value = 1;
        } else {
            index_btn_for_true_value = 2;
        }

        //связываем номер с кнопкой
        btn_true_value = btns_choices[index_btn_for_true_value];
        btn_false_value1 = btns_choices[index_btn_for_false_value1];
        btn_false_value2 = btns_choices[index_btn_for_false_value2];

        //устанавливаем определения на экран
        btn_true_value.setText(true_value);
        btn_false_value1.setText(false_value1);
        btn_false_value2.setText(false_value2);

        //обработка нажатия на правильное определение
        btn_true_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                true_answers += 1;

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_true_value.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                //Приостанавливает на 2 секунды
                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(100);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_true_value.startAnimation(anim);


                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_true_value.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(Color.argb(100,30,
                                        116, 19));
                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия на неправильное определение 1
        btn_false_value1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_false_value1.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_false_value1.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_false_value1.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_false_value1.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));
                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия на неправильное определение 2
        btn_false_value2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_false_value2.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);

                //сделает кнопку оранжевой
                btn_false_value2.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_false_value2.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_false_value2.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_false_value2.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));

                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия кнопки "Не знаю"
        btn_idk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //блокируем определения
                btn_idk.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_idk.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_idk.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_idk.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_idk.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));


                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });
    }

    //обработка нажатия на стрелочку
    public void OnClickNext(View view) {

        level++;

        //прячем кнопку "Следующий"
        btn_next.setVisibility(View.INVISIBLE);

        //разблокируем кнопки
        btns_choices[0].setEnabled(true);
        btns_choices[1].setEnabled(true);
        btns_choices[2].setEnabled(true);
        btn_idk.setEnabled(true);

        //окрашиваем кнопки обратно в белый цвет
        btn_idk.setBackgroundColor(Color.argb(100, 255, 255, 255));
        for (int i = 0; i < 3; i++){
            btns_choices[i].setBackgroundColor(Color.argb(100, 255, 255, 255));
        }
        //вытаскивание слова и его удаление из keysArrList
        word = keysLinkList.poll().toString();

        //вытаскивание для слова верного значения и его удаление из HM_word_bank
        true_value = HM_word_bank.get(word);
        HM_word_bank.remove(word);

        //берем  2 определения и удаляем их
        false_value1 = HM_word_bank.get(keysLinkList.getFirst().toString());
        HM_word_bank.remove(keysLinkList.getFirst().toString());
        keysLinkList.removeFirst();

        false_value2 = HM_word_bank.get(keysLinkList.getFirst().toString());;
        HM_word_bank.remove(keysLinkList.getFirst().toString());
        keysLinkList.removeFirst();

        //устанавливаем слово на экран
        Tv_word.setText(word);

        random = new Random();

        //выбираем рандомно номера кнопок для определений
        index_btn_for_false_value1 = random.nextInt(3);
        index_btn_for_false_value2 = random.nextInt(3);
        while (index_btn_for_false_value1 == index_btn_for_false_value2) {
            index_btn_for_false_value1 = random.nextInt(3);
            index_btn_for_false_value2 = random.nextInt(3);
        }
        if (index_btn_for_false_value1 != 0 && index_btn_for_false_value2 != 0) {
            index_btn_for_true_value = 0;
        } else if (index_btn_for_false_value1 != 1 && index_btn_for_false_value2 != 1) {
            index_btn_for_true_value = 1;
        } else {
            index_btn_for_true_value = 2;
        }

        //связываем номер с кнопкой
        btn_true_value = btns_choices[index_btn_for_true_value];
        btn_false_value1 = btns_choices[index_btn_for_false_value1];
        btn_false_value2 = btns_choices[index_btn_for_false_value2];

        //устанавливаем определения на экран
        btn_true_value.setText(true_value);
        btn_false_value1.setText(false_value1);
        btn_false_value2.setText(false_value2);

        //обработка нажатия на правильное определение
        btn_true_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                true_answers += 1;

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_true_value.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                //Приостанавливает на 2 секунды
                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(100);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_true_value.startAnimation(anim);


                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_true_value.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(Color.argb(100,30,
                                        116, 19));
                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия на неправильное определение 1
        btn_false_value1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_false_value1.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_false_value1.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_false_value1.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_false_value1.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));
                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия на неправильное определение 2
        btn_false_value2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //блокируем определения
                btn_idk.setEnabled(false);
                btn_false_value2.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);

                //сделает кнопку оранжевой
                btn_false_value2.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_false_value2.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_false_value2.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_false_value2.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));

                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });

        //обработка нажатия кнопки "Не знаю"
        btn_idk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //блокируем определения
                btn_idk.setEnabled(false);
                btn_true_value.setEnabled(false);
                btn_false_value1.setEnabled(false);
                btn_false_value2.setEnabled(false);

                //сделает кнопку оранжевой
                btn_idk.setBackgroundColor(
                        Color.argb(100,255, 152, 0));

                new CountDownTimer(1150, 1000) {
                    public void onFinish() {

                        //сделает кнопку с правильным значением зеленой
                        btn_true_value.setBackgroundColor(
                                Color.argb(100,0, 255, 10));

                        //сделает кнопку красной
                        btn_idk.setBackgroundColor(
                                Color.argb(100,255, 0, 0));

                        //сделает кнопку мигающей
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(75);
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        btn_idk.startAnimation(anim);

                        //задержка 2 секунды
                        new CountDownTimer(1000, 1000) {
                            public void onFinish() {

                                //откючение мигания
                                btn_idk.clearAnimation();

                                //если последний тест
                                if (level == 14){
                                    //показываем кнопку "Результаты"
                                    btn_results.setVisibility(View.VISIBLE);

                                }
                                else {
                                    //показываем кнопку "Следующий"
                                    btn_next.setVisibility(View.VISIBLE);

                                }

                                //отмечает прогресс игры
                                progress[level].setBackgroundColor(
                                        Color.argb(100,255, 0, 0));


                            }
                            public void onTick(long millisUntilFinished) {
                            }
                        }.start();
                    }
                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
        });
    }

    //обработка нажатия на стрелочку на последнем уровне
    public void OnClickResults(View view) {
        Intent intent = new Intent(this, TestEndActivity.class);

        //передаем количество правильных ответов активности завершения теста
        intent.putExtra("result", Integer.toString(true_answers));

        startActivity(intent);
        finish();
    }

    @Override
    //вызов диалогового окна при нажатии системной конпки "Назад"
    public void onBackPressed() {

        exit_test = new Dialog(this);

        //блокируем закрытие диалогового окна системной кнопкой
        exit_test.setCancelable(false);
        //скрываем заголовок
       exit_test.requestWindowFeature(Window.FEATURE_NO_TITLE);

        exit_test.setContentView(R.layout.exit_test_dialog);

        //делаем фон прозрачным
        exit_test.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        exit_test.show();


    }

    public void DialogOnClickYes(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();

    }

    public void DialogOnClickNo(View view) {
        exit_test.cancel();
    }
}