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
                               String reading1, String reading2,
                               String reading3, String reading4,
                               String reading5, String reading6,
                               String reading7, String reading8) {

        String insert_sql = "insert into " + readingsTableName +
                " (filename, reading1, reading2, reading3, reading4, reading5, reading6, reading7, reading8) " +
                "values ('" + sampleBoardFilename + "', '" +
                reading1 + "', '" +
                reading2 + "', '" +
                reading3 + "', '" +
                reading4 + "', '" +
                reading5 + "', '" +
                reading6 + "', '" +
                reading7 + "', '" +
                reading8 + "');";

        Log.d("INSERT SQL", insert_sql);

        database.execSQL(insert_sql);
    }

    public String[] searchReadings(String sampleBoardFilename) {
        Cursor cursor = database.query(readingsTableName, null, null, null, null, null, null);
        String outputs[] = new String[8];

        if (cursor.moveToFirst()) {

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                if (cursor.getString(1).equals(sampleBoardFilename)) {
                    for (int j = 0; j < 8; j++)
                        outputs[j] = cursor.getString(j + 2);
                    return outputs;
                }
            }
        }

        for (int i = 0; i < 8; i++)
            outputs[i] = "";
        return outputs;
    }

}
