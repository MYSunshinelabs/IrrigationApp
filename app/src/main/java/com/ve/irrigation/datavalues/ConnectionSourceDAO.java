package com.ve.irrigation.datavalues;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by laxmi on 13/6/18.
 */

@Dao
public interface ConnectionSourceDAO {

    @Insert
    void insert(ConnectionSourceData... connectionSourceData);

    @Query("SELECT * FROM ConnectionSourceData")
    List<ConnectionSourceData> getAllConnectionSource();

    @Delete
    void deleteConnection(ConnectionSourceData connectionSourceData);

    @Update
    void updateConnection(ConnectionSourceData connectionSourceData);


}
