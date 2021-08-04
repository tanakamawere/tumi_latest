package org.tmz.tumi.Utils;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.preference.PreferenceManager;

import org.tmz.tumi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Miscellaneous {
    private static final String TAG = "Miscellaneous";
    public SharedPreferences sharedPreferences, ratePreference;
    public SharedPreferences.Editor editor;
    Calendar calenderDate = Calendar.getInstance();

    //Method for internet
    public boolean internetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //Methods to get current date and time
    public String getDate() {
        //Date joined.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return simpleDateFormat.format(calenderDate.getTime());
    }

    public String getTime() {
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleTimeFormat.format(calenderDate.getTime());
    }

    /*Sunday  = 1
    Monday = 2
    Tuesday = 3
    Wednesday = 4
    Thursday = 5
    Friday = 6
    * Saturday = 7*/

    public int getDay() {
        return calenderDate.get(Calendar.DAY_OF_WEEK);
    }

    public int getHour() {
        return calenderDate.get(Calendar.HOUR_OF_DAY);
    }

    public String getCurrencyIdentification(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currencyString = sharedPreferences.getString(context.getString(R.string.currencySelected), context.getString(R.string.defaultCurrencyValue));
        String currencyReturnValue = context.getString(R.string.currencyIdentifier);

        assert currencyString != null;
        if (currencyString.equals(context.getString(R.string.defaultCurrencyValue))) {
            currencyReturnValue = context.getString(R.string.currencyIdentifier);
        } else if (currencyString.equals(context.getString(R.string.usdValue))) {
            currencyReturnValue = context.getString(R.string.usdLabel);
        } else if (currencyString.equals(context.getString(R.string.randValue))) {
            currencyReturnValue = context.getString(R.string.randsZARLabel);
        } else if (currencyString.equals(context.getString(R.string.zwlValue))) {
            currencyReturnValue = context.getString(R.string.zwlLabel);
        }
        return currencyReturnValue;
    }
}
