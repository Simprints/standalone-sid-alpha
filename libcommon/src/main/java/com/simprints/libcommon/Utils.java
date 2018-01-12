package com.simprints.libcommon;

import android.support.annotation.NonNull;
import android.util.Base64;

public class Utils {

    public static @NonNull String byteArrayToBase64(@NonNull byte[] bytes)
    {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static @NonNull byte[] base64ToBytes(@NonNull String base64) throws IllegalArgumentException
    {
        return Base64.decode(base64, Base64.DEFAULT);
    }
}