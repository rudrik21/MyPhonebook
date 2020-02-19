package com.rudrik.simplephonebook;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static com.rudrik.simplephonebook.Person.myPeople;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private AdptPersons adpt;

    private EditText edtSearch;
    private RecyclerView recyclerPersons;
    private FloatingActionButton fabAdd;

    private PersonDatabase db;
    private Executor bg, main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = PersonDatabase.getInstance(this);

        bg = AppExecutors.getInstance().diskIO();
        main = AppExecutors.getInstance().mainThread();

        init();
//        insert();
    }

    private void init() {
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        recyclerPersons = (RecyclerView) findViewById(R.id.recyclerPersons);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(this);

        edtSearch.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edtSearch.setText(null);
        refreshData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_add){
            Intent i = new Intent(this, AddPersonActivity.class);
            startActivity(i);
        }
    }

    private void refreshData() {

        bg.execute(new Runnable() {
            @Override
            public void run() {
                myPeople = db.personDao().getPersonList();
                System.out.println(myPeople.toString());

                main.execute(new Runnable() {
                    @Override
                    public void run() {
//                        if (adpt == null){
                            setAdapter(myPeople);
//                        }
//
//                        if (!myPeople.isEmpty()) {
//                            adpt.refreshData();
//                        }
                    }
                });

            }
        });
    }

    private void setAdapter(List<Person> list){
        adpt = new AdptPersons(this, list);
        recyclerPersons.setHasFixedSize(true);
        recyclerPersons.setLayoutManager(new LinearLayoutManager(this));
        recyclerPersons.setAdapter(adpt);
    }

    //  INSERT NEW PERSON
    private void insert() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.personDao().insertPerson(new Person("Rudrik", "Panchal", "6476154991", "1050 Markham rd"));
                db.personDao().insertPerson(new Person("Richa", "Patel", "2352734892", "1515 Nanak house"));
            }
        });
    }


    //  DELETE PERSON
    private void deletePerson(final Person person){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int rows = db.personDao().deletePerson(person);
                Log.e("DELETED ROWS", String.valueOf(rows));
            }
        });
    }

    //  PERFORMING SEARCH

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        return;
    }

    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {

            edtSearch.removeTextChangedListener(this);

            System.out.println(s);

            List<Person> list = new ArrayList<>();
            for (Person p : myPeople) {
                if (p.getFirstName().startsWith(s.toString()) ||
                p.getLastName().startsWith(s.toString()) ||
                p.getAddr().startsWith(s.toString()) ||
                p.getPhone().startsWith(s.toString())){
                    list.add(p);
                }
            }

            if (!list.isEmpty()){
                setAdapter(list);
            }

            edtSearch.addTextChangedListener(this);

        }else{
            edtSearch.removeTextChangedListener(this);

            System.out.println(s);

            setAdapter(myPeople);

            edtSearch.addTextChangedListener(this);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();
        edtSearch.setText(null);
    }
}
