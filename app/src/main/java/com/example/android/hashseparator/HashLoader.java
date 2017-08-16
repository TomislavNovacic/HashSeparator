package com.example.android.hashseparator;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashLoader extends AsyncTaskLoader<byte[]> {

    private String mData;
    private byte[] dataBytes;

    public HashLoader(Context context, String data) {
        super(context);
        mData = data;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public byte[] loadInBackground() {
        if (mData == null || mData.isEmpty()) {
            return null;
        }
        try {
           dataBytes = mData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(dataBytes);
        return md.digest();
    }
}
