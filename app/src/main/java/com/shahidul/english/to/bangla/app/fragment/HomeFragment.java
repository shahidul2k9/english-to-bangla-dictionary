package com.shahidul.english.to.bangla.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.shahidul.english.to.bangla.app.R;
import com.shahidul.english.to.bangla.app.activity.MainActivity;
import com.shahidul.english.to.bangla.app.dao.DatabaseOpenHelper;
import com.shahidul.english.to.bangla.app.dao.DictionaryDao;
import com.shahidul.english.to.bangla.app.dao.DictionaryDaoImpl;
import com.shahidul.english.to.bangla.app.model.Word;
import com.shahidul.english.to.bangla.app.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shahidul Islam
 * @since 10/24/2015.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private MaterialSearchView searchView;
    private DictionaryDao dictionaryDao;
    private MainActivity mainActivity;
    private Toolbar toolBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = (MainActivity) getActivity();
        dictionaryDao = new DictionaryDaoImpl(new DatabaseOpenHelper(mainActivity).getReadWritableDatabase());
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        View fragmentView = inflater.inflate(R.layout.fragment_home, null);
        searchView = (MaterialSearchView) fragmentView.findViewById(R.id.search_view);
        searchView.setVoiceSearch(true);
        searchView.setFragment(this);
        searchView.setCursorDrawable(R.drawable.color_cursor_white);
        toolBar = (Toolbar) (mainActivity.findViewById(R.id.toolbar));
        searchView.setToolBar(toolBar);
        SearchViewAdapter searchViewAdapter = new SearchViewAdapter(mainActivity);
        searchView.setAdapter(searchViewAdapter);
        if (getArguments() != null) {
            String searchString = getArguments().getString(Intent.EXTRA_TEXT);
            searchView.showSearch();
            searchView.setQuery(searchString, false);
        }
        return fragmentView;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchView.isSearchOpen()){
            toolBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
        inflater.inflate(R.menu.menu_home_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        dictionaryDao.destroy();
        toolBar.setVisibility(View.VISIBLE);
        super.onDestroy();
        Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
    }

    public boolean canConsumeBackButtonPressEvent() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return true;
        }
        return false;
    }

    private class SearchViewAdapter extends BaseAdapter implements Filterable {
        private List<Word> wordList;
        private LayoutInflater layoutInflater;

        public SearchViewAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Word word = wordList.get(position);
            View view = convertView;
            final WordHolder holder;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.word_list_item, null);
                holder = new WordHolder(view);
                view.setTag(holder);
            } else {
                holder = (WordHolder) view.getTag();
            }
            holder.fromView.setText(word.getFrom());
            holder.toView.setText(Util.getFirstWord(word.getTo()));
            int favoriteResourceId = (word.isFavorite() ? (R.drawable.favorite) : (R.drawable.not_favorite));
            holder.favoriteView.setImageResource(favoriteResourceId);
            holder.wordContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.onClickWord(word.getId());
                }
            });
            holder.favoriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (word.isFavorite()) {
                        dictionaryDao.removeFromFavorite(word.getId());
                        word.setIsFavorite(false);
                        holder.favoriteView.setImageResource(R.drawable.not_favorite);
                    } else {

                        dictionaryDao.makeFavorite(word.getId());
                        word.setIsFavorite(true);
                        holder.favoriteView.setImageResource(R.drawable.favorite);
                    }
                }
            });
            return view;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    Log.d(TAG, new Exception().getStackTrace()[0].getMethodName());
                    FilterResults filterResults = new FilterResults();
                    if (Util.isTextEmpty(constraint)) {
                        filterResults.values = new ArrayList<Word>();
                        filterResults.count = 0;
                        return filterResults;
                    }
                    List<Word> matchingWordList = dictionaryDao.getWordListByPrefixMatching(constraint.toString());
                    filterResults.values = matchingWordList;
                    filterResults.count = matchingWordList.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    wordList = (List<Word>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        private class WordHolder {
            RelativeLayout wordContainer;
            TextView fromView;
            TextView toView;
            ImageView favoriteView;

            public WordHolder(View view) {
                wordContainer = (RelativeLayout) view.findViewById(R.id.ripple_view);
                fromView = (TextView) view.findViewById(R.id.from_word);
                toView = (TextView) view.findViewById(R.id.to_word);
                favoriteView = (ImageView) view.findViewById(R.id.favorite);
            }
        }
    }
}
