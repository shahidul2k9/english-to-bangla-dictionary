package com.shahidul.english.to.bangla.app.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shahidul.english.to.bangla.app.R;
import com.shahidul.english.to.bangla.app.activity.MainActivity;
import com.shahidul.english.to.bangla.app.constant.Key;
import com.shahidul.english.to.bangla.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.bangla.app.dao.DictionaryDao;
import com.shahidul.english.to.bangla.app.dao.DictionaryDaoImpl;
import com.shahidul.english.to.bangla.app.model.Word;

/**
 * @author Shahidul Islam
 * @since 7/27/2015.
 */
public class WordDetailFragment extends Fragment {
    private TextView fromView;
    private TextView toView;
    private ImageView favoriteView;
    private ImageView editView;
    private ImageView deleteView;
    private ImageView speakerView;
    private DictionaryDao dictionaryDao;
    private Word word;
    private Handler handler = new Handler();
    private MainActivity mainActivity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionaryDao = new DictionaryDaoImpl(new DatabaseOpenHelper(getActivity()).getReadWritableDatabase());
        mainActivity = (MainActivity)getActivity();
        mainActivity.hideSoftKeyboard();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_word_dteail, null);
        fromView = (TextView) fragmentView.findViewById(R.id.from);
        toView = (TextView) fragmentView.findViewById(R.id.to);
        editView = (ImageView) fragmentView.findViewById(R.id.edit_view);
        deleteView = (ImageView) fragmentView.findViewById(R.id.delete_view);
        speakerView = (ImageView) fragmentView.findViewById(R.id.speaker_view);
        favoriteView = (ImageView) fragmentView.findViewById(R.id.favorite);
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mainActivity.gesture.onTouchEvent(event);
            }
        });
        return fragmentView;


    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        final int wordId = getArguments().getInt(Key.RECORD_ID);
        word = dictionaryDao.getWordById(wordId);
        int favoriteViewImageResource = (word.isFavorite()? R.drawable.favorite:R.drawable.not_favorite);
        fromView.setText(word.getFrom());
        toView.setText(formatMeaning(word.getTo()));
        favoriteView.setImageResource(favoriteViewImageResource);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.editWord(word.getId());

            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog deleteWordDialog = new AlertDialog.Builder(mainActivity)
                        .setTitle(R.string.delete_word)
                        .setMessage(R.string.sure_to_delete_word)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dictionaryDao.deleteWord(word.getId());
                                mainActivity.onBackPressed();
                            }
                        })
                        .setNegativeButton(R.string.no,null)
                        .create();
                deleteWordDialog.show();
            }
        });

        speakerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.speak(word.getFrom());
            }
        });
        favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavorite = word.isFavorite();
                word.setIsFavorite(!isFavorite);
                if (isFavorite) {
                    dictionaryDao.removeFromFavorite(word.getId());
                    favoriteView.setImageResource(R.drawable.not_favorite);
                } else {
                    dictionaryDao.makeFavorite(word.getId());
                    favoriteView.setImageResource(R.drawable.favorite);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dictionaryDao.destroy();
    }

    private String formatMeaning(String commaSeparatedMeaning){
        String means[] = commaSeparatedMeaning.split(",");
        String formattedMeaning = null;
        for (String mean : means){
            if (formattedMeaning == null){
                formattedMeaning = mean.trim();
            }
            else {
                formattedMeaning = formattedMeaning + "\n" + mean.trim();
            }
        }
        return formattedMeaning;
    }

}
