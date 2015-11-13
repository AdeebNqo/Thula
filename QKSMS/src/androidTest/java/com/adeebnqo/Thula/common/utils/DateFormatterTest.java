package com.adeebnqo.Thula.common.utils;

import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import com.adeebnqo.Thula.ui.MainActivity;
import com.adeebnqo.Thula.ui.settings.SettingsFragment;

import java.text.SimpleDateFormat;

public class DateFormatterTest extends AndroidTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test24Hours(){
        SharedPreferences prefs = MainActivity.getPrefs(getContext());
        prefs.edit().putBoolean(SettingsFragment.TIMESTAMPS_24H, true).commit();
        assertTrue(DateFormatter.accountFor24HourTime(getContext(), new SimpleDateFormat("h:mm a")).equals(new SimpleDateFormat("H:mm")));
        prefs.edit().putBoolean(SettingsFragment.TIMESTAMPS_24H, false).commit();
    }

    public void testDateSymbolsAPI(){
        assertEquals(new SimpleDateFormat("H:mm a").toPattern(), "H:mm a");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
