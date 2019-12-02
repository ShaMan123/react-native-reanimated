package com.swmansion.reanimated.reflection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;

public class ReanimatedWritableCollection extends ReanimatedWritableMap implements WritableArray {

    public static ReanimatedWritableCollection fromMap(ReadableMap source) {
        if (source instanceof ReanimatedWritableCollection) {
            return ((ReanimatedWritableCollection) source);
        } else {
            ReanimatedWritableCollection out = new ReanimatedWritableCollection();
            out.merge(source);
            return out;
        }
    }

    private String key(String name) {
        return WritableArrayUtils.isIndex(name) ? key(Integer.valueOf(name)) : name;
    }

    private String key(int index) {
        return String.valueOf(index < 0 ? size() + index : index) ;
    }

    @Override
    public boolean hasKey(@NonNull String name) {
        return super.hasKey(key(name));
    }

    private String nextIndex() {
        return String.valueOf(size());
    }

    private Boolean isArray() {
        return size() > 0;
    }

    @Nullable
    @Override
    public ReadableArray getArray(@NonNull String name) {
        return super.getArray(key(name));
    }

    @Nullable
    @Override
    public ReadableArray getArray(int index) {
        return getArray(key(index));
    }

    @Override
    public void pushArray(@Nullable ReadableArray array) {
        putArray(nextIndex(), array);
    }

    @Override
    public boolean getBoolean(@NonNull String name) {
        return super.getBoolean(key(name));
    }

    @Override
    public boolean getBoolean(int index) {
        return getBoolean(key(index));
    }

    @Override
    public void pushBoolean(boolean value) {
        putBoolean(nextIndex(), value);
    }

    @Override
    public double getDouble(@NonNull String name) {
        return super.getDouble(key(name));
    }

    @Override
    public double getDouble(int index) {
        return getDouble(key(index));
    }

    @Override
    public void pushDouble(double value) {
        putDouble(nextIndex(), value);
    }

    @NonNull
    @Override
    public Dynamic getDynamic(@NonNull String name) {
        return super.getDynamic(key(name));
    }

    @NonNull
    @Override
    public Dynamic getDynamic(int index) {
        return getDynamic(key(index));
    }

    public void pushDynamic(Dynamic value) {
        putDynamic(nextIndex(), value);
    }

    @Override
    public void putDynamic(String name, Object o) {
        super.putDynamic(key(name), o);
    }

    @Override
    public int getInt(@NonNull String name) {
        return super.getInt(key(name));
    }

    @Override
    public int getInt(int index) {
        return getInt(key(index));
    }

    @Override
    public void pushInt(int value) {
        putInt(nextIndex(), value);
    }

    @Nullable
    @Override
    public ReanimatedWritableMap getMap(@NonNull String name) {
        return super.getMap(key(name));
    }

    @Nullable
    @Override
    public ReadableMap getMap(int index) {
        return getMap(key(index));
    }

    @Override
    public void pushMap(@Nullable ReadableMap map) {
        putMap(nextIndex(), map);
    }

    @Nullable
    @Override
    public String getString(@NonNull String name) {
        return super.getString(key(name));
    }

    @Nullable
    @Override
    public String getString(int index) {
        return getString(key(index));
    }

    @Override
    public void pushString(@Nullable String value) {
        putString(nextIndex(), value);
    }

    @NonNull
    @Override
    public ReadableType getType(@NonNull String name) {
        return super.getType(key(name));
    }

    @NonNull
    @Override
    public ReadableType getType(int index) {
        return getType(key(index));
    }

    @Override
    public boolean isNull(int index) {
        return isNull(key(index));
    }

    @Override
    public void pushNull() {
        putNull(nextIndex());
    }

    @Override
    public int size() {
        ReadableMapKeySetIterator keySetIterator = keySetIterator();
        String key;
        int size = 0;

        while (keySetIterator.hasNextKey()) {
            key = keySetIterator.nextKey();
            if (WritableArrayUtils.isIndex(key)) {
                size = Math.max(size, Integer.valueOf(key) + 1);
            }
        }

        return size;
    }

    @NonNull
    @Override
    public ArrayList<Object> toArrayList() {
        ArrayList<Object> list = new ArrayList<>();
        ReadableMapKeySetIterator keySetIterator = keySetIterator();
        String key;
        int index;

        while (keySetIterator.hasNextKey()) {
            key = keySetIterator.nextKey();
            if (WritableArrayUtils.isIndex(key)) {
                index = Integer.valueOf(key);
                list.ensureCapacity(index + 1);
                while (list.size() <= index) {
                    list.add(null);
                }
                list.set(index, new ReanimatedDynamic(getDynamic(key)).value());
            }
        }

        return list;
    }

    @NonNull
    @Override
    public String toString() {
        return isArray() ? toArrayList().toString() : super.toString();
    }
}
