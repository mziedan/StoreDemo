package com.beekoding.storedemo.Utils;

/**
 * Created by moham on 2017-05-15.
 */

import android.os.Environment;

import java.io.*;

public class ObjectToFileUtil {

    public static String objectToFile(Object object) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + "cache" + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += "data";
        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return path;
    }

    public static Object objectFromFile(String path) throws IOException, ClassNotFoundException {
        Object object = null;
        File data = new File(path);
        if (data.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
            object = objectInputStream.readObject();
            objectInputStream.close();
        }
        return object;
    }
}
