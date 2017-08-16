package com.adeebnqo.Thula.spam;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
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
    private final String folderName = "/Thula";
    private final String fileName = "spam_list.txt";
    File sdCard = Environment.getExternalStorageDirectory();
    boolean isExternalStorageFine;

    public SDCardStorage(Context context) {
        mContext = context;
        isExternalStorageFine = isExternalStorageWritable();
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public boolean hasExternalStorage() {
        return isExternalStorageFine;
    }

    public void saveSpamList() {
        if (isExternalStorageFine) {

            try{
                File dir = new File (sdCard.getAbsolutePath() + folderName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fileStream = new FileOutputStream(file);

                SpamNumberStorage spamNumberStorage = new SharedPreferenceSpamNumberStorage(mContext);
                Object[] items = spamNumberStorage.getSpamNumbers();

                for (Object item : items) {
                    fileStream.write(((String) item).getBytes());
                    fileStream.write("\n".getBytes());
                }

            } catch (IOException e) {
                Log.d(TAG, "Cannot save spam list to sd card.");
            }
        }
    }
    public void loadSpamList() {
        if (isExternalStorageFine) {

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
