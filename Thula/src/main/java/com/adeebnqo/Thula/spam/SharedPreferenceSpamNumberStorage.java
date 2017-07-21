package com.adeebnqo.Thula.spam;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.adeebnqo.Thula.data.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SharedPreferenceSpamNumberStorage implements SpamNumberStorage {

    private String sharedPreferenceFileName = getClass().getName();
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceSpamNumberStorage(Context context){
        this.context = context;
    }

    @Override
    public void addNumber(Message msg) {
        addNumber(msg.getAddress());
    }

    @Override
    public void addNumber(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().putString(id.trim(), "").apply();
    }


    @Override
    public void deleteNumber(Message msg) {
        deleteNumber(msg.getName());
    }

    @Override
    public void deleteNumber(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        sharedPreferences.edit().remove(id.trim()).apply();
    }

    @Override
    public boolean contains(Message msg) {
        return contains(msg.getAddress());
    }

    @Override
    public boolean contains(String id) {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        return sharedPreferences.contains(id.trim());
    }

    @Override
    public Object[] getSpamNumbers() {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        Map<String, ?> items = sharedPreferences.getAll();
        Gson gson = new GsonBuilder().create();
        return items.keySet().toArray();
    }

    @Override
    public boolean isEmpty() {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);

        Map<String, ?> items = sharedPreferences.getAll();
        return items.isEmpty();
    }

    @Override
    public void clean() {
        sharedPreferences = context.getSharedPreferences(sharedPreferenceFileName, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
