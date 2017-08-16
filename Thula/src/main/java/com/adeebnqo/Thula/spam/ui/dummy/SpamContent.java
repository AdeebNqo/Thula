package com.adeebnqo.Thula.spam.ui.dummy;

import android.content.Context;

import com.adeebnqo.Thula.spam.SharedPreferenceSpamNumberStorage;
import com.adeebnqo.Thula.spam.SpamNumberStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpamContent {

    public static List<Message> ITEMS = new ArrayList<Message>();

    public static Map<String, Message> ITEM_MAP = new HashMap<String, Message>();

    private static void addItem(Message item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static class Message {
        public String id;
        public String content;

        public Message(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static void loadContent(Context context){

        ITEMS.clear();

        SpamNumberStorage spamNumberStorage = new SharedPreferenceSpamNumberStorage(context);
        Object[] items = spamNumberStorage.getSpamNumbers();

        for (Object item : items) {
            SpamContent.Message msg = new SpamContent.Message(null, (String) item);
            ITEMS.add(msg);
        }

    }
}
