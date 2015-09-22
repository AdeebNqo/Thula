package com.adeebnqo.Thula.spam;

import android.content.Context;
import android.content.SharedPreferences;

import com.adeebnqo.Thula.data.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SharedPreferenceSpamNumberStorage implements SpamNumberStorage {

    private String sharedPreferenceFileName = getClass().getName();
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceSpamNumberStorage(Context context){
        this.context = context;
    }

    @Override
    public void addNumber(Message msg) {
        addNumber(msg.getName());
    }

    @Override
    public void addNumber(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(String.valueOf(id), "").apply();
    }

    @Override
    public void deleteNumber(Message msg) {
        deleteNumber(msg.getName());
    }

    @Override
    public void deleteNumber(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().remove(String.valueOf(id)).apply();
    }

    @Override
    public boolean contains(Message msg) {
        return contains(msg.getName());
    }

    @Override
    public boolean contains(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        return sharedPreferences.contains(String.valueOf(id));
    }
}
