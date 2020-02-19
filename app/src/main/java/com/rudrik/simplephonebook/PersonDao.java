package com.rudrik.simplephonebook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PersonDao {

    @Query("select * from person")
    List<Person> getPersonList();

    /*
    @Query("SELECT * " +
            "FROM person " +
            "WHERE firstname||lastname||phone||address like '% :r'")


    @Query("SELECT * " +
            "FROM person " +
            "WHERE firstname like '% :search'")
    List<Person> getFilteredPerson(String search);
    */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPerson(Person person);

    @Update
    int updatePerson(Person person);

    @Delete
    int deletePerson(Person person);

}
