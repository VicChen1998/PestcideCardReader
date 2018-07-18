package com.vicchen.pestcidecardreader.Global;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {

    private static SQLiteDatabase database;

    private static final String readingsTableName = "Readings";

    public Database() {
        if (database == null)
            database = SQLiteDatabase.openOrCreateDatabase(GlobalPath.getDatabasePath(), null);

        String createTable = "create table if not exists " + readingsTableName + "(" +
                "_id integer primary key autoincrement," +
                "filename text, " +
                "reading1 text, " +
                "reading2 text, " +
                "reading3 text, " +
                "reading4 text, " +
                "reading5 text, " +
                "reading6 text, " +
                "reading7 text, " +
                "reading8 text) ";

        database.execSQL(createTable);
    }

    public static void close() {
        database.close();
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static String getReadingsTableName() {
        return readingsTableName;
    }

    public void insertReadings(String sampleBoardFilename,
                               String readings[]) {

        String insert_sql = "insert into " + readingsTableName +
                " (filename, reading1, reading2, reading3, reading4, reading5, reading6, reading7, reading8) " +
                "values ('" + sampleBoardFilename + "', '" +
                readings[0] + "', '" +
                readings[1] + "', '" +
                readings[2] + "', '" +
                readings[3] + "', '" +
                readings[4] + "', '" +
                readings[5] + "', '" +
                readings[6] + "', '" +
                readings[7] + "');";

        Log.d("INSERT SQL", insert_sql);

        database.execSQL(insert_sql);
    }

    public String[] searchReadings(String sampleBoardFilename) {
        Cursor cursor = database.query(readingsTableName,
                null,
                "filename = ?",
                new String[]{sampleBoardFilename},
                null, null, null);

        String outputs[] = new String[8];

        if (cursor.getCount() == 1 && cursor.moveToFirst()) {
            for (int i = 0; i < 8; i++)
                outputs[i] = cursor.getString(i + 2);

            return outputs;
        }

        for (int i = 0; i < 8; i++)
            outputs[i] = "";
        return outputs;
    }

    public void deleteReadings(String sampleBoardFilename) {
        database.delete(readingsTableName, "filename = ?", new String[]{sampleBoardFilename});
    }

}
