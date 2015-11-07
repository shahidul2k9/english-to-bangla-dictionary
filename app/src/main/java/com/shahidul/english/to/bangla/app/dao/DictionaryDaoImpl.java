package com.shahidul.english.to.bangla.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shahidul.english.to.bangla.app.config.Configuration;
import com.shahidul.english.to.bangla.app.model.Word;
import com.shahidul.english.to.bangla.app.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahidul Islam
 * @since 7/4/2015.
 */
public class DictionaryDaoImpl implements DictionaryDao {
    private SQLiteDatabase sqLiteDatabase;
    public DictionaryDaoImpl(SQLiteDatabase sqLiteDatabase){
        this.sqLiteDatabase = sqLiteDatabase;
    }
    @Override
    public Word getWordById(int id) {

        Cursor cursor = sqLiteDatabase.query(Util.getCurrentTableName(),new String[]{Database.COLUMN_ID, Database.COLUMN_FROM, Database.COLUMN_TO, Database.COLUMN_FAVORITE},
                Database.COLUMN_ID + " = ?",new String[]{String.valueOf(id)},null,null,null,null);
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int fromColumnIndex = cursor.getColumnIndex(Database.COLUMN_FROM);
        int toColumnIndex = cursor.getColumnIndex(Database.COLUMN_TO);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idColumnIndex),cursor.getString(fromColumnIndex), cursor.getString(toColumnIndex), cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }

    @Override
    public Word getWordByName(String name) {
        Cursor cursor = sqLiteDatabase.query(Util.getCurrentTableName(),new String[]{Database.COLUMN_ID, Database.COLUMN_FROM, Database.COLUMN_TO, Database.COLUMN_FAVORITE},
                Database.COLUMN_FROM + " = ?", new String[]{name},null,null,null);
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int fromColumnIndex = cursor.getColumnIndex(Database.COLUMN_FROM);
        int toColumnIndex = cursor.getColumnIndex(Database.COLUMN_TO);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        Word word = null;
        if (cursor.moveToNext()){
            word = new Word(cursor.getInt(idColumnIndex), cursor.getString(fromColumnIndex), cursor.getString(toColumnIndex), cursor.getInt(favoriteColumnIndex)==1);
        }
        return word;
    }

    @Override
    public List<Word> getWordListByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Util.getCurrentTableName(),new String[]{Database.COLUMN_ID, Database.COLUMN_FROM, Database.COLUMN_TO, Database.COLUMN_FAVORITE},
                Database.COLUMN_FROM + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_FROM);
        List<Word> wordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int fromColumnIndex = cursor.getColumnIndex(Database.COLUMN_FROM);
        int toColumnIndex = cursor.getColumnIndex(Database.COLUMN_TO);
        int favoriteColumnIndex = cursor.getColumnIndex(Database.COLUMN_FAVORITE);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(fromColumnIndex), cursor.getString(toColumnIndex), cursor.getInt(favoriteColumnIndex)==1);
            wordList.add(word);
        }
        return wordList;
    }

    @Override
    public List<Word> getFavoriteWordList() {
        Cursor cursor = sqLiteDatabase.query(Util.getCurrentTableName(),new String[]{Database.COLUMN_ID, Database.COLUMN_FROM, Database.COLUMN_TO},
                Database.COLUMN_FAVORITE + " = ?",new String[]{String.valueOf(1)},null,null,Database.COLUMN_FROM, String.valueOf(Configuration.MAX_DISPLAYABLE_FAVORITE_WORDS));
        List<Word> favoriteWordList = new ArrayList<Word>();
        int idColumnIndex = cursor.getColumnIndex(Database.COLUMN_ID);
        int fromColumnIndex = cursor.getColumnIndex(Database.COLUMN_FROM);
        int toColumnIndex = cursor.getColumnIndex(Database.COLUMN_TO);
        while (cursor.moveToNext()){
            Word word = new Word(cursor.getInt(idColumnIndex),cursor.getString(fromColumnIndex), cursor.getString(toColumnIndex),true);
            favoriteWordList.add(word);
        }
        return favoriteWordList;
    }

    @Override
    public List<String> getNameListByPrefixMatching(String prefix) {
        Cursor cursor = sqLiteDatabase.query(Util.getCurrentTableName(),new String[]{Database.COLUMN_FROM},
                Database.COLUMN_FROM + " LIKE ?",new String[]{prefix + "%"},null,null,Database.COLUMN_FROM);
        List<String> wordList = new ArrayList<String>();
        int fromColumnIndex = cursor.getColumnIndex(Database.COLUMN_FROM);
        while (cursor.moveToNext()){
            wordList.add(cursor.getString(fromColumnIndex));
        }
        return wordList;
    }

    @Override
    public void makeFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,1);
        sqLiteDatabase.update(Util.getCurrentTableName(),contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});
    }

    @Override
    public void removeFromFavorite(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FAVORITE,0);
        sqLiteDatabase.update(Util.getCurrentTableName(),contentValues,Database.COLUMN_ID + " = ?" ,new String[]{String.valueOf(id)});
    }

    @Override
    public long insertWord(Word word) {
        int favorite = word.isFavorite()?1:0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FROM, word.getFrom());
        contentValues.put(Database.COLUMN_TO, word.getTo());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        return sqLiteDatabase.insert(Util.getCurrentTableName(),null,contentValues);
    }

    @Override
    public void updateWord(Word word) {
        int favorite = word.isFavorite()?1:0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_FROM, word.getFrom());
        contentValues.put(Database.COLUMN_TO, word.getTo());
        contentValues.put(Database.COLUMN_FAVORITE, favorite);
        sqLiteDatabase.update(Util.getCurrentTableName(),contentValues,Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(word.getId())});
    }

    @Override
    public void deleteWord(int id) {
        sqLiteDatabase.delete(Util.getCurrentTableName(),Database.COLUMN_ID + " = ? ", new String[]{String.valueOf(id)});
    }

    @Override
    public void destroy() {
        sqLiteDatabase.close();
    }
}
