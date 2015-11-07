package com.shahidul.english.to.bangla.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shahidul.english.to.bangla.app.R;
import com.shahidul.english.to.bangla.app.activity.MainActivity;
import com.shahidul.english.to.bangla.app.config.Configuration;
import com.shahidul.english.to.bangla.app.constant.Constant;
import com.shahidul.english.to.bangla.app.constant.Key;
import com.shahidul.english.to.bangla.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.bangla.app.dao.DictionaryDao;
import com.shahidul.english.to.bangla.app.dao.DictionaryDaoImpl;
import com.shahidul.english.to.bangla.app.model.Word;

/**
 * @author Shahidul
 * @since 5/31/2015
 */
public class EditWordFragment extends Fragment implements View.OnClickListener {
    private EditText fromWordView;
    private EditText toWordView;
    private Button addUpdateView;
    private DictionaryDao dictionaryDao;
    private int wordId;
    private MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        dictionaryDao = new DictionaryDaoImpl(new DatabaseOpenHelper(activity).getReadWritableDatabase());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_add_new_word, null);
        fromWordView = (EditText) fragmentView.findViewById(R.id.from_word);
        toWordView = (EditText) fragmentView.findViewById(R.id.to_word);
        addUpdateView = (Button) fragmentView.findViewById(R.id.add_update);
        if(Configuration.isPrimaryLanguageEnglish){
            fromWordView.setHint(R.string.from_word);
            toWordView.setHint(R.string.to_meaning);
        }
        else {
            fromWordView.setHint(R.string.from_word2);
            toWordView.setHint(R.string.to_meaning2);
        }

        addUpdateView.setOnClickListener(this);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return ((MainActivity) getActivity()).gesture.onTouchEvent(event);
            }
        });
        return fragmentView;
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (getArguments() != null) {
            wordId = getArguments().getInt(Key.RECORD_ID, Constant.INVALID_ROW_ID);
            if (wordId != Constant.INVALID_ROW_ID) {
                Word word = dictionaryDao.getWordById(wordId);
                fromWordView.setText(word.getFrom());
                toWordView.setText(word.getTo());
            }
        }
        if (wordId != Constant.INVALID_ROW_ID) {
            addUpdateView.setText(getString(R.string.update));
        } else {
            addUpdateView.setText(getString(R.string.add));
        }
    }

    @Override
    public void onDestroy() {
        dictionaryDao.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_update) {
            String fromWord = fromWordView.getText().toString().trim();
            final String toWord = toWordView.getText().toString().trim();
            if (fromWord.length() == 0) {
                if (Configuration.isPrimaryLanguageEnglish) {
                    showToast(getString(R.string.from_word_require));
                }
                else {
                    showToast(getString(R.string.from_word_require2));
                }
            } else if (toWord.length() == 0) {
                if (Configuration.isPrimaryLanguageEnglish) {
                    showToast(getString(R.string.to_meaning_require));
                }
                else {
                    showToast(getString(R.string.to_meaning_require2));
                }
            } else {
                if (wordId == Constant.INVALID_ROW_ID) {
                    activity.hideSoftKeyboard();
                    wordId = (int) dictionaryDao.insertWord(new Word(fromWord, toWord, false));
                    showToast(getString(R.string.word_added));
                    addUpdateView.setText(getString(R.string.update));
                } else {
                    activity.hideSoftKeyboard();
                    Word word = dictionaryDao.getWordById(wordId);
                    word.setFrom(fromWord);
                    word.setTo(toWord);
                    dictionaryDao.updateWord(word);
                    showToast(getString(R.string.word_updated));
                }
            }
        }
    }

    void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
