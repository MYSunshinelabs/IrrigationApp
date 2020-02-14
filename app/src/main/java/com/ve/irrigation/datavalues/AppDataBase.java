package com.ve.irrigation.datavalues;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

/**
 * Created by laxmi on 13/6/18.
 */
@Database(entities = {ConnectionSourceData.class,Wifi.class}, version = 3, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase appDataBase;

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Wifi "+ " ADD COLUMN status INTEGER DEFAULT 0 NOT NULL");
        }
    };
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Wifi "+ " ADD COLUMN name VARCHAR(50)");
            database.execSQL("ALTER TABLE Wifi "+ " ADD COLUMN ipAddress VARCHAR(50)");
            database.execSQL("ALTER TABLE Wifi "+ " ADD COLUMN port VARCHAR(50)");
        }
    };

    public static AppDataBase getAppDataBase(Context context) {
        if (appDataBase == null)
            appDataBase = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "user-database")
                    // allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
//                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                    .build();
        return appDataBase;
    }


    public abstract ConnectionSourceDAO connectionSourceDAO();

    public abstract WifiDAO wifiDAO();

}
