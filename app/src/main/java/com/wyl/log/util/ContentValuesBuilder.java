package com.wyl.log.util;

import android.content.ContentValues;

import com.google.gson.internal.$Gson$Preconditions;

/**
 * @author : yuelinwang
 * @time : 6/9/21
 * @desc : ContentValues工具类
 */
public class ContentValuesBuilder {
    ContentValues values;

    public ContentValuesBuilder() {
        values = new ContentValues();
    }

    public ContentValuesBuilder put(String key, String value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds all values from the passed in ContentValues.
     *
     * @param other the ContentValues from which to copy
     */
    public ContentValuesBuilder putAll(ContentValues other) {
        values.putAll(other);
        return this;

    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Byte value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Short value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Integer value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Long value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Float value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Double value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, Boolean value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a value to the set.
     *
     * @param key the name of the value to put
     * @param value the data for the value to put
     */
    public ContentValuesBuilder put(String key, byte[] value) {
        values.put(key, value);
        return this;
    }

    /**
     * Adds a null value to the set.
     *
     * @param key the name of the value to make null
     */
    public ContentValuesBuilder putNull(String key) {
        values.putNull(key);
        return this;
    }

    public ContentValues build() {
        return values;
    }
}
