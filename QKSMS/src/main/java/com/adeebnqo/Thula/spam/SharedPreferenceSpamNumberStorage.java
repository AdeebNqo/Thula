package com.adeebnqo.Thula.spam;

import android.content.Context;
import android.content.SharedPreferences;

import com.adeebnqo.Thula.data.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SharedPreferenceSpamNumberStorage implements SpamNumberStorage {

    private String sharedPreferenceFileName = getClass().getName();
    private Gson builder = new GsonBuilder().create();
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceSpamNumberStorage(Context context){
        this.context = context;
    }

    @Override
    public void addNumber(Message msg) {
        addNumber(msg.getId());
    }

    @Override
    public void addNumber(long id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(String.valueOf(id), "").apply();
    }

    @Override
    public void deleteNumber(Message msg) {
        deleteNumber(msg.getId());
    }

    @Override
    public void deleteNumber(long id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().remove(String.valueOf(id)).apply();
    }

    @Override
    public boolean contains(Message msg) {
        return contains(msg.getId());
    }

    @Override
    public boolean contains(long id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        return sharedPreferences.contains(String.valueOf(id));
    }
}
