package com.adeebnqo.Thula.spam;

import com.adeebnqo.Thula.data.Message;

public interface SpamNumberStorage {
    void addNumber(Message msg);
    void addNumber(String id);
    void deleteNumber(Message msg);
    void deleteNumber(String id);
    boolean contains(Message msg);
    boolean contains(String id);
    Object[] getSpamNumbers();
}
