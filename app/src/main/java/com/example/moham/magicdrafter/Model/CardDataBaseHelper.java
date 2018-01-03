package com.example.moham.magicdrafter.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Magic Drafter - CardDataBaseHelper.java
 * Created by Brigham Moll.
 * Created on 12/24/2017.
 * Last Revised on 12/26/2017.
 * Description: This special version of the SQLiteOpenHelper will use the built-in database
 * from the assets folder of the app.
 * This script was made after reading https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 * Specifically, the method called copyInfoIntoDatabase, which is used to pull information from a pre-made database file.
 */

public class CardDataBaseHelper extends SQLiteOpenHelper
{
    // The context, passed in during the constructor.
    private final Context appContext;

    // Path for built-in database file folder.
    private static final String DATABASE_PATH = "/data/data/com.example.moham.magicdrafter/databases/";

    // The actual database file name.
    private static String DATABASE_NAME = "mtgsetdb.sqlite3";

    // The database to be filled with copied card information from mtgsetdb.sqlite3
    private SQLiteDatabase cardDatabase;

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Generate the database.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // No current upgrade mechanism implemented. There is only one version of the DB.
    }

    // Stores context in order to access the database in the assets folder.
    public CardDataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.appContext = context;
    }

    // This method will create a database and then fill it with the values from the built-in database file.
    public void generateAppDatabase()
    {
        // Before creating database, check if it exists already in the app.
        SQLiteDatabase cardDatabase = null;

        try
        {
            String databasePath = DATABASE_PATH + DATABASE_NAME;
            cardDatabase = SQLiteDatabase.openDatabase(databasePath,null, SQLiteDatabase.OPEN_READONLY);
        }
        catch(SQLiteException e)
        {
            // The database file within the app does not exist -- Therefore, one needs to be created with the
            // database file from the app assets folder. Allow the program to continue executing.
        }

        if(cardDatabase != null)
        {
            // Close up the database. There is already one available.
            cardDatabase.close();
        }
        else
        {
            // No database available yet. Create empty one and add information from pre-made database to it.
            // Create empty database.
            this.getReadableDatabase();
            // Copy info in.
            try
            {
                copyInfoIntoDatabase();
            }
            catch(Exception e)
            {
                // Error while copying database.
                Log.e("DB Copy Error", "Error while copying database.");
            }
        }
    }

    // This method actually copies pre-made information into the empty database.
    // Based on the method from https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
    private void copyInfoIntoDatabase()
    {
        try
        {
            // Open database file from assets.
            InputStream cardDatabaseFile = appContext.getAssets().open(DATABASE_NAME);

            // Full path to the empty database.
            String databaseFullPath = DATABASE_PATH + DATABASE_NAME;

            // Open empty databse.
            OutputStream emptyDatabaseFile = new FileOutputStream(databaseFullPath);

            // Copy information over.
            byte[] buffer = new byte[1024];
            int length;
            while ((length = cardDatabaseFile.read(buffer)) > 0)
            {
                emptyDatabaseFile.write(buffer, 0, length);
            }

            // Close streams.
            emptyDatabaseFile.flush();
            emptyDatabaseFile.close();
            cardDatabaseFile.close();
        }
        catch (IOException e)
        {
            Log.e("DB Error", "Database copying error. Likely, db file named incorrectly in code.");
        }
    }

    public void openDatabase()
    {
        // Open the database.
        String databaseFullPath = DATABASE_PATH + DATABASE_NAME;
        cardDatabase = SQLiteDatabase.openDatabase(databaseFullPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close()
    {
        // If there is a database to close, close it.
        if(cardDatabase != null)
        {
            cardDatabase.close();
        }

        super.close();
    }
}
