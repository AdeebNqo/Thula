package com.adeebnqo.Thula.spam;

import com.adeebnqo.Thula.data.Message;

public interface SpamNumberStorage {
    void addNumber(Message msg);
    void addNumber(long id);
    void deleteNumber(Message msg);
    void deleteNumber(long id);
    boolean contains(Message msg);
    boolean contains(long id);
}
