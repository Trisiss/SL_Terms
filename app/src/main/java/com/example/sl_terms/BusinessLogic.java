package com.example.sl_terms;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
//import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import static android.content.Context.MODE_PRIVATE;

class BusinessLogic {
    private Context context;
    private TermsDbHelper mDbHelper;

    BusinessLogic(Context context) {
        this.context = context;
        mDbHelper = new TermsDbHelper(context);
    }

    private final class TermsTable implements BaseColumns {
        final static String TABLE_NAME = "metadata";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_NAME = "name";
    }

    private class TermsDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "terms.db";
        private static final int DATABASE_VERSION = 1;

        TermsDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL_CREATE_TABLE = "CREATE TABLE " + TermsTable.TABLE_NAME + " ("
                    + TermsTable._ID + " INTEGER PRIMARY KEY NOT NULL, "
                    + TermsTable.COLUMN_NAME + " TEXT NOT NULL);";
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //При обновлении схемы бд
        }
    }

    TermRecord[] searchTerm(String str) {
        ArrayList<TermRecord> listTerms = new ArrayList<>();
        String query = "SELECT "
                + TermsTable._ID + ", "
                + TermsTable.COLUMN_NAME
                + " FROM " + TermsTable.TABLE_NAME
                + " WHERE " + TermsTable.COLUMN_NAME
                + " LIKE " + "'%" + str.toLowerCase() + "%'"
                + " ORDER BY " + TermsTable.COLUMN_NAME;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            TermRecord term = new TermRecord();
            term.id = cursor.getInt(cursor.getColumnIndex(TermsTable._ID));
            term.name = cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NAME));
            listTerms.add(term);
        }
        cursor.close();
        return listTerms.toArray(new  TermRecord[listTerms.size()]);
    }

    private void addMetadata(int id, String name) {
        String query = "SELECT "
                + TermsTable._ID
                + " FROM " + TermsTable.TABLE_NAME
                + " WHERE " + TermsTable._ID + " = " + id;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put(TermsTable._ID, id);
            values.put(TermsTable.COLUMN_NAME, name);
            long newRowId = db.insert(TermsTable.TABLE_NAME, null, values);
            if (newRowId == -1) {
                Log.d("MY_LOG", "Ошибка при записи термина");
            } else {
                Log.d("MY_LOG", "Термин с id = " + newRowId + " записан");
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(TermsTable.COLUMN_NAME, name);
            db.update(TermsTable.TABLE_NAME,
                    values,
                    TermsTable._ID + "= ?",
                    new String[] {Integer.toString(id)});
            Log.d("MY_LOG", "Термин с id = " + id + " обновлён");
        }
        cursor.close();
    }

    private void writeFile(String nameFile, String contentFile) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    context.openFileOutput(nameFile, MODE_PRIVATE)));
            bw.write(contentFile);
            bw.close();
            Log.d("MY_LOG", "Файл " + nameFile + " записан");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadTerm(TermRecord term) {
        DataBase dataBase = new DataBase();
        term.name = term.name.toLowerCase();
        writeFile(term.name + ".html", dataBase.getTermByID(term.id));
        addMetadata(term.id, term.name);
    }

    void loadImage(String url, String name) {
        try {
            InputStream in = new java.net.URL(url).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            File file = new File(context.getFilesDir().getPath(), name);
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
            out.flush();
            out.close();
            Log.d("MY_LOG", "Файл " + name + " записан");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void loadBinary(String url, String name) {
        try {
            InputStream in = new java.net.URL(url).openStream();
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name);

            file.exists();

            OutputStream out = new FileOutputStream(file);
            copyFile(in,out);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}

