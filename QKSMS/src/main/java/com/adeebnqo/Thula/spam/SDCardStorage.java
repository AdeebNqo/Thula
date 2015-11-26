package com.adeebnqo.Thula.spam;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.adeebnqo.Thula.data.Contact;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SDCardStorage {

    private final String TAG = "Thula SDCardStorage";

    private Context mContext;
    private final String folderName = "ThulaSpamList";
    private final String fileName = "list.txt";
    File sdCard = Environment.getExternalStorageDirectory();
    boolean isSDPresent;

    public SDCardStorage(Context context) {
        mContext = context;
        isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public boolean hasSDCard() {
        return isSDPresent;
    }

    public void saveSpamList() {
        if (isSDPresent) {

            try{
                File dir = new File (sdCard.getAbsolutePath() + folderName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);

                FileOutputStream fileStream = new FileOutputStream(file);

                SpamNumberStorage spamNumberStorage = new SharedPreferenceSpamNumberStorage(mContext);
                Object[] items = spamNumberStorage.getSpamNumbers();

                for (Object item : items) {
                    fileStream.write(((String) item).getBytes());
                }

            } catch (IOException e) {
                Log.d(TAG, "Cannot save spam list to sd card.");
            }
        }
    }
    public void loadSpamList() {
        if (isSDPresent) {

            SpamNumberStorage spamNumberStorage = new SharedPreferenceSpamNumberStorage(mContext);

            if (spamNumberStorage.isEmpty()) {

                spamNumberStorage.clean();

                try {
                    File dir = new File(sdCard.getAbsolutePath() + folderName);
                    File file = new File(dir, fileName);
                    InputStream inputStream = new FileInputStream(file);

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String tmpReadString;
                    while ((tmpReadString = bufferedReader.readLine()) != null) {
                        spamNumberStorage.addNumber(tmpReadString);
                    }

                    inputStream.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.d(TAG, "Can not read file: " + e.toString());
                }
            }

        }
    }
}
