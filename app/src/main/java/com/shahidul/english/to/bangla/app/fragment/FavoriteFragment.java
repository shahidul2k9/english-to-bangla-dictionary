package com.shahidul.english.to.bangla.app.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.shahidul.english.to.bangla.app.R;
import com.shahidul.english.to.bangla.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.bangla.app.dao.DictionaryDao;
import com.shahidul.english.to.bangla.app.dao.DictionaryDaoImpl;
import com.shahidul.english.to.bangla.app.model.Word;
import com.shahidul.english.to.bangla.app.util.Util;
import com.shahidul.english.to.bangla.app.listener.WordClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahidul Islam
 * @since 7/28/2015.
 */
public class FavoriteFragment extends Fragment implements WordClickListener {
    private static final String TAG = FavoriteFragment.class.getSimpleName();
    private TextView noFavoriteWordFound;
    private ListView favoriteWordListView;
    private DictionaryDao dictionaryDao;
    private FavoriteListAdapter favoriteListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        dictionaryDao = new DictionaryDaoImpl(new DatabaseOpenHelper(getActivity()).getReadableDatabase());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        View fragmentView = inflater.inflate(R.layout.fragment_favorite, null);
        favoriteWordListView = (ListView) fragmentView.findViewById(R.id.list);
        noFavoriteWordFound = (TextView) fragmentView.findViewById(R.id.no_favorite_word_found);


        favoriteWordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = (Word) favoriteListAdapter.getItem(position);
                onClickWord(word.getId());
            }
        });
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... params) {
                return dictionaryDao.getFavoriteWordList();
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                favoriteListAdapter = new FavoriteListAdapter(FavoriteFragment.this.getActivity());
                favoriteListAdapter.setWordList(words);
                favoriteWordListView.setAdapter(favoriteListAdapter);
                if (favoriteListAdapter.getCount() == 0) {
                    noFavoriteWordFound.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        dictionaryDao.destroy();
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        super.onDestroy();
    }

    @Override
    public void onClickWord(int wordId) {
        ((WordClickListener) getActivity()).onClickWord(wordId);
    }

    public class FavoriteListAdapter extends BaseAdapter {
        private List<Word> wordList;
        private LayoutInflater layoutInflater;

        public FavoriteListAdapter(Context context) {
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            wordList = new ArrayList<>();
        }


        @Override
        public int getCount() {
            return wordList.size();
        }

        @Override
        public Object getItem(int position) {
            return wordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return wordList.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView;
            WordHolder holder;
            if (convertView == null) {
                itemView = layoutInflater.inflate(R.layout.favorite_list_item, null);
                holder = new WordHolder(itemView);
                itemView.setTag(holder);
            } else {
                itemView = convertView;
                holder = (WordHolder) itemView.getTag();
            }
            final Word word = wordList.get(position);
            holder.fromView.setText(word.getFrom());
            holder.toView.setText(Util.getFirstWord(word.getTo()));
            return itemView;
        }

        public void setWordList(List<Word> newWordList) {
            wordList = newWordList;
        }

        public class WordHolder {
            LinearLayout wordContainer;
            TextView fromView;
            TextView toView;

            public WordHolder(View itemView) {
                wordContainer = (LinearLayout) itemView.findViewById(R.id.word_container);
                fromView = (TextView) itemView.findViewById(R.id.from_word);
                toView = (TextView) itemView.findViewById(R.id.to_word);
            }
        }
    }

}