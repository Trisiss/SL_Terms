package com.example.sl_terms

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.BaseColumns
import android.util.Log
import com.example.sl_terms.models.TermRecord
import java.io.*
import java.net.URL
import java.util.*


class BusinessLogic(private val context: Context) {
    private val DATABASE_NAME = "terms.db"
    private val DATABASE_VERSION: Int = 1
    private val mDbHelper: TermsDbHelper

    init {
        mDbHelper = TermsDbHelper(context)
    }

    private object TermsTable : BaseColumns {
        const val TABLE_NAME = "metadata"
        const val _ID = BaseColumns._ID
        const val COLUMN_NAME = "name"
    }

    private inner class TermsDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            val SQL_CREATE_TABLE = ("CREATE TABLE " + TermsTable.TABLE_NAME + " ("
                    + TermsTable._ID + " INTEGER PRIMARY KEY NOT NULL, "
                    + TermsTable.COLUMN_NAME + " TEXT NOT NULL);")
            db.execSQL(SQL_CREATE_TABLE)
        }

        override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) { //При обновлении схемы бд
        }

    }

    fun searchTerm(str: String): Array<TermRecord> {
        val listTerms = ArrayList<TermRecord>()
        val query = ("SELECT "
                + TermsTable._ID + ", "
                + TermsTable.COLUMN_NAME
                + " FROM " + TermsTable.TABLE_NAME
                + " WHERE " + TermsTable.COLUMN_NAME
                + " LIKE " + "'%" + str.toLowerCase() + "%'"
                + " ORDER BY " + TermsTable.COLUMN_NAME)
        val db = mDbHelper.readableDatabase
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val term = TermRecord()
            term.id = cursor.getInt(cursor.getColumnIndex(TermsTable._ID))
            term.name = cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NAME))
            listTerms.add(term)
        }
        cursor.close()
        return listTerms.toTypedArray()
    }

    private fun addMetadata(id: Int, name: String?) {
        val query = ("SELECT "
                + TermsTable._ID
                + " FROM " + TermsTable.TABLE_NAME
                + " WHERE " + TermsTable._ID + " = " + id)
        val db = mDbHelper.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.count == 0) {
            val values = ContentValues()
            values.put(TermsTable._ID, id)
            values.put(TermsTable.COLUMN_NAME, name)
            val newRowId = db.insert(TermsTable.TABLE_NAME, null, values)
            if (newRowId == -1L) {
                Log.d("MY_LOG", "Ошибка при записи термина")
            } else {
                Log.d("MY_LOG", "Термин с id = $newRowId записан")
            }
        } else {
            val values = ContentValues()
            values.put(TermsTable.COLUMN_NAME, name)
            db.update(TermsTable.TABLE_NAME,
                    values,
                    TermsTable._ID + "= ?", arrayOf(Integer.toString(id)))
            Log.d("MY_LOG", "Термин с id = $id обновлён")
        }
        cursor.close()
    }

    private fun writeFile(nameFile: String, contentFile: String?) {
        try {
            val bw = BufferedWriter(OutputStreamWriter(
                    context.openFileOutput(nameFile, Context.MODE_PRIVATE)))
            bw.write(contentFile)
            bw.close()
            Log.d("MY_LOG", "Файл $nameFile записан")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadTerm(term: TermRecord?) {
        val dataBase = DataBase()
        term!!.name = term.name!!.toLowerCase()
        writeFile(term.name + ".html", dataBase.getTermByID(term.id))
        addMetadata(term.id, term.name)
    }

    fun loadImage(url: String?, name: String) {
        try {
            val `in` = URL(url).openStream()
            val bitmap = BitmapFactory.decodeStream(`in`)
            val file = File(context.filesDir.path, name)
            val out: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            out.flush()
            out.close()
            Log.d("MY_LOG", "Файл $name записан")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadBinary(url: String?, name: String?) {
        try {
            val `in` = URL(url).openStream()
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name)
            file.exists()
            val out: OutputStream = FileOutputStream(file)
            copyFile(`in`, out)
            `in`.close()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

}