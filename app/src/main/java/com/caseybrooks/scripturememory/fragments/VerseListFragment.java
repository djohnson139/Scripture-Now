package com.caseybrooks.scripturememory.fragments;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.caseybrooks.androidbibletools.basic.Passage;
import com.caseybrooks.androidbibletools.container.Verses;
import com.caseybrooks.scripturememory.R;
import com.caseybrooks.scripturememory.data.BibleVerseAdapter;
import com.caseybrooks.scripturememory.data.MetaSettings;
import com.caseybrooks.scripturememory.data.VerseDB;

public class VerseListFragment extends ListFragment {
//Data members
//------------------------------------------------------------------------------
	Context context;
    ActionBar ab;
    Toolbar tb;
    ActionMode mActionMode;

	BibleVerseAdapter bibleVerseAdapter;
	String list;
    int state;

    VerseDB db;
	
//Lifecycle and Initialization
//------------------------------------------------------------------------------
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		getListView().setSelector(new StateListDrawable());
		getListView().setFastScrollEnabled(true);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		context = getActivity();
		Bundle extras = getArguments();
		if(extras.containsKey("KEY_LIST")) {
			list = extras.getString("KEY_LIST");
		}
		else {
			list = "current";
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
        db = new VerseDB(context);
        db.open();

		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(context,
							   R.array.sort_methods, android.R.layout.simple_spinner_dropdown_item);

		ab = ((ActionBarActivity) context).getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setListNavigationCallbacks(spinnerAdapter, navigationListener);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setSelectedNavigationItem(MetaSettings.getSortBy(context));

		populateBibleVerses();
	}

	@Override
	public void onPause() {
		super.onPause();
		((ActionBarActivity) context).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        db.close();
    }

    BibleVerseAdapter.OnMultiSelectListener iconClick = new BibleVerseAdapter.OnMultiSelectListener() {
        @Override
        public void onMultiSelect(View view, int position) {
            if (mActionMode != null) {

            }

            // Start the CAB using the ActionMode.Callback defined above
            mActionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(mActionModeCallback);
        }
    };

    BibleVerseAdapter.OnItemClickListener itemClick = new BibleVerseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            switchToEditFragment((int)bibleVerseAdapter.getItemId(position));
        }
    };

	private void populateBibleVerses() {
		Verses<Passage> verses;
		VerseDB db = new VerseDB(context);
        state = (list.equals("memorized")) ? 5 : 1;
		
		db.open();
        if(state == 5)
		    verses = db.getStateVerses(state);
        else
            verses = db.getAllCurrentVerses();
		db.close();

        switch(MetaSettings.getSortBy(context)) {
            case 0:
                verses.sortByID();
                break;
            case 1:
                verses.sort();
                break;
            case 2:
                verses.sortAlphabetical();
                break;
        }

		bibleVerseAdapter = new BibleVerseAdapter(context, verses);
        bibleVerseAdapter.setOnMultiSelectListener(iconClick);
        bibleVerseAdapter.setOnItemClickListener(itemClick);
		setListAdapter(bibleVerseAdapter);
	}

//ActionBar Spinner
//------------------------------------------------------------------------------
    ActionBar.OnNavigationListener navigationListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int position, long itemId) {

            String[] strings = getActivity().getResources().getStringArray(R.array.sort_methods);

			MetaSettings.putSortBy(context, position);
            populateBibleVerses();

            return true;
        }
    };
	
//Host Activity Interface
//------------------------------------------------------------------------------
	private static onListEditListener listener;
	
	public void switchToEditFragment(int id) {
	    if(listener != null) {
	        listener.toEdit(id);
	    }
	}

    public interface onListEditListener {
	    void toEdit(int id);
    }

	public static void setOnListEditListener(onListEditListener listener) {
	    VerseListFragment.listener = listener;
	}

//Contextual ActionMode for multi-selection
//------------------------------------------------------------------------------
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.menu_share:
//                    shareCurrentItem();
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }

            return false;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}