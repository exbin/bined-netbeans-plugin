/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.bined.ideplugin.preferences;

/**
 *
 * @author hajdam
 */
public interface Preferences {

    void flush();

    String get(String key, String def);

    boolean getBoolean(String key, boolean def);

    byte[] getByteArray(String key, byte[] def);

    double getDouble(String key, double def);

    float getFloat(String key, float def);

    int getInt(String key, int def);

    long getLong(String key, long def);

    void put(String key, String value);

    void putBoolean(String key, boolean value);

    void putByteArray(String key, byte[] value);

    void putDouble(String key, double value);

    void putFloat(String key, float value);

    void putInt(String key, int value);

    void putLong(String key, long value);

    void remove(String key);

    void sync();
    
}
