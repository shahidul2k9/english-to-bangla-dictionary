package com.shahidul.english.to.bangla.app.dao;


import com.shahidul.english.to.bangla.app.model.Word;

import java.util.List;

/**
 * @author Shahidul
 * @since 5/30/2015
 */
public interface DictionaryDao {
    Word getWordById(int id);
    Word getWordByName(String name);
    List<Word> getWordListByPrefixMatching(String prefix);
    List<Word> getFavoriteWordList();
    List<String> getNameListByPrefixMatching(String prefix);
    void makeFavorite(int id);
    void removeFromFavorite(int id);
    long insertWord(Word word);
    void updateWord(Word word);
    void deleteWord(int id);
    void destroy();
}
