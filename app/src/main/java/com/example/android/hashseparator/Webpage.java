package com.example.android.hashseparator;

/**
 * Created by Tomi on 14.8.2017..
 */

public class Webpage {

   private String mUrl;
   private byte[] mHash;
   private String mStorage;

    public Webpage(String url, byte[] hash, String storage) {
        mUrl = url;
        mHash = hash;
        mStorage = storage;
    }

    public String getUrl() {
        return mUrl;
    }

    public byte[] getHash() {
        return mHash;
    }

    public String getStorage() {
        return mStorage;
    }

    public void setUrl(String url) {mUrl = url;}

    public void setHash(byte[] hash) {mHash = hash;}

    public void setStorage(String storage) {mStorage = storage;}
}
