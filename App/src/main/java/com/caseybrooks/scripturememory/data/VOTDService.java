package com.caseybrooks.scripturememory.data;

import com.caseybrooks.androidbibletools.basic.Passage;

public class VOTDService {
    public VOTDService() {

    }

    //either returns a verse, verifying that it is today's verse, or it returns
    //null, indicating that we must download today's verse
//    public static Passage getCurrentVerse(Context context) {
//
//        //get all verses that are either tagged or in the state of VOTD
//        VerseDB db = new VerseDB(context).open();
//        Passage mostRecent = db.getMostRecentVOTD();
//        db.close();
//
//        //no VOTD exist in database at this time
//        if(mostRecent != null) {
//            //check the timestamp of the most recent verse against the current time.
//            //if the current verse is on the same day as today, then it is current.
//            //if the current verse is not today, it needs to get updated
//            Calendar today = Calendar.getInstance();
//            Calendar current = Calendar.getInstance();
//            current.setTimeInMillis(mostRecent.getMetadata().getLong(DefaultMetaData.TIME_CREATED));
//
//            boolean isCurrent =
//                (today.get(Calendar.ERA) == current.get(Calendar.ERA)
//                && today.get(Calendar.YEAR) == current.get(Calendar.YEAR)
//                && today.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR));
//
//            if(isCurrent) {
//                mostRecent.getMetadata().putBoolean("IS_CURRENT", true);
//                return mostRecent;
//            }
//            else {
//                mostRecent.getMetadata().putBoolean("IS_CURRENT", false);
//                return mostRecent;
//            }
//        }
//
//        return null;
//    }

//Asynchronously download verses
//------------------------------------------------------------------------------


//Callback to return a Verse
//------------------------------------------------------------------------------
    public static interface GetVerseListener {
        public void onPreDownload();
        public void onVerseDownloaded(Passage passage);
    }
}
