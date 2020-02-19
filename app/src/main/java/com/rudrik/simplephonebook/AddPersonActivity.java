package com.rudrik.simplephonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executor;

import static com.rudrik.simplephonebook.Person.myPeople;

public class AddPersonActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTitleAddMember;
    private EditText edtFname;
    private EditText edtLname;
    private EditText edtPhone;
    private EditText edtAddr;
    private Button btnAddPerson;

    private PersonDatabase db;
    private Executor bg, main;

    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        db = PersonDatabase.getInstance(this);

        bg = AppExecutors.getInstance().diskIO();
        main = AppExecutors.getInstance().mainThread();

        getSupportActionBar().setTitle("All Contacts ("+myPeople.size()+")");

        init();
    }

    private void init() {
        person = (Person) getIntent().getSerializableExtra("PERSON");
        System.out.println(person);

        tvTitleAddMember = (TextView) findViewById(R.id.tvTitleAddMember);
        edtFname = (EditText) findViewById(R.id.edtFname);
        edtLname = (EditText) findViewById(R.id.edtLname);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtAddr = (EditText) findViewById(R.id.edtAddr);
        btnAddPerson = (Button) findViewById(R.id.btnAddPerson);
        btnAddPerson.setOnClickListener(this);

        loadData();
    }

    private void loadData() {
        if (person != null){
            tvTitleAddMember.setText("Edit details");
            edtFname.setText(person.getFirstName());
            edtLname.setText(person.getLastName());
            edtPhone.setText(person.getPhone());
            edtAddr.setText(person.getAddr());
            btnAddPerson.setText("Update Person");
        }
    }

    private Person validateData(){
        Person p = new Person(
                edtFname.getText().toString(),
                edtLname.getText().toString(),
                edtPhone.getText().toString(),
                edtAddr.getText().toString()
        );

        if (p.getFirstName().isEmpty()){
            edtFname.setError("Empty field!");
            edtFname.requestFocus();
        }else if (p.getLastName().isEmpty()){
            edtLname.setError("Empty field!");
            edtLname.requestFocus();
        }else if (p.getAddr().isEmpty()){
            edtAddr.setError("Empty field!");
            edtAddr.requestFocus();
        }else if (p.getPhone().isEmpty()){
            edtPhone.setError("Empty field!");
            edtPhone.requestFocus();
        }else{
            return p;
        }

        return null;
    }

    private void insertPerson(final Person person){
        bg.execute(new Runnable() {
            @Override
            public void run() {
                db.personDao().insertPerson(person);
            }
        });
    }

    private void updatePerson(final Person person){
        bg.execute(new Runnable() {
            @Override
            public void run() {
                db.personDao().updatePerson(person);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAddPerson) {
            Person p = validateData();
            if (person == null) {
                if (p != null) {
                    insertPerson(p);
                    this.finish();
                }
            } else {
                if (p != null) {
                    p.setId(person.getId());
                    updatePerson(p);
                    this.finish();
                }
            }
        }
    }
}
