/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.lang.reflect.*;
import java.util.*;
import java.util.prefs.Preferences;

@SuppressWarnings("StaticNonFinalUsedInInitialization")
public class WinRegistryService {
  // inspired by
    // http://javabyexample.wisdomplug.com/java-concepts/34-core-java/62-java-registry-wrapper.html
    // http://www.snipcode.org/java/1-java/23-java-class-for-accessing-reading-and-writing-from-windows-registry.html
    // http://snipplr.com/view/6620/accessing-windows-registry-in-java/

    public static final int HKEY_CURRENT_USER = 0x80000001;
    public static final int HKEY_LOCAL_MACHINE = 0x80000002;
    public static final int REG_SUCCESS = 0;
    public static final int REG_NOTFOUND = 2;
    public static final int REG_ACCESSDENIED = 5;

    private static final int KEY_ALL_ACCESS = 0xf003f;
    private static final int KEY_READ = 0x20019;
    private static Preferences userRoot = Preferences.userRoot();
    private static Preferences systemRoot = Preferences.systemRoot();
    private static Class<? extends Preferences> userClass = userRoot.getClass();
    private static Method regOpenKey = null;
    private static Method regCloseKey = null;
    private static Method regQueryValueEx = null;
    private static Method regEnumValue = null;
    private static Method regQueryInfoKey = null;
    private static Method regEnumKeyEx = null;
    private static Method regCreateKeyEx = null;
    private static Method regSetValueEx = null;
    private static Method regDeleteKey = null;
    private static Method regDeleteValue = null;

    static {
        try {
            regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
                    new Class[]{int.class, byte[].class, int.class});
            regOpenKey.setAccessible(true);
            regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey",
                    new Class[]{int.class});
            regCloseKey.setAccessible(true);
            regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx",
                    new Class[]{int.class, byte[].class});
            regQueryValueEx.setAccessible(true);
            regEnumValue = userClass.getDeclaredMethod("WindowsRegEnumValue",
                    new Class[]{int.class, int.class, int.class});
            regEnumValue.setAccessible(true);
            regQueryInfoKey = userClass.getDeclaredMethod("WindowsRegQueryInfoKey1",
                    new Class[]{int.class});
            regQueryInfoKey.setAccessible(true);
            regEnumKeyEx = userClass.getDeclaredMethod(
                    "WindowsRegEnumKeyEx", new Class[]{int.class, int.class,
                        int.class});
            regEnumKeyEx.setAccessible(true);
            regCreateKeyEx = userClass.getDeclaredMethod(
                    "WindowsRegCreateKeyEx", new Class[]{int.class,
                        byte[].class});
            regCreateKeyEx.setAccessible(true);
            regSetValueEx = userClass.getDeclaredMethod(
                    "WindowsRegSetValueEx", new Class[]{int.class,
                        byte[].class, byte[].class});
            regSetValueEx.setAccessible(true);
            regDeleteValue = userClass.getDeclaredMethod(
                    "WindowsRegDeleteValue", new Class[]{int.class,
                        byte[].class});
            regDeleteValue.setAccessible(true);
            regDeleteKey = userClass.getDeclaredMethod(
                    "WindowsRegDeleteKey", new Class[]{int.class,
                        byte[].class});
            regDeleteKey.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
        }
    }

    private WinRegistryService() {
    }

    public static String readString(int hkey, String key, String valueName)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readString(systemRoot, hkey, key, valueName);
            case HKEY_CURRENT_USER:
                return readString(userRoot, hkey, key, valueName);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    public static Map<String, String> readStringValues(int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readStringValues(systemRoot, hkey, key);
            case HKEY_CURRENT_USER:
                return readStringValues(userRoot, hkey, key);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    public static List<String> readStringSubKeys(int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                return readStringSubKeys(systemRoot, hkey, key);
            case HKEY_CURRENT_USER:
                return readStringSubKeys(userRoot, hkey, key);
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public static void createKey(int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int[] ret;
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                ret = createKey(systemRoot, hkey, key);
                regCloseKey.invoke(systemRoot, new Object[]{Integer.valueOf(ret[0])});
                break;
            case HKEY_CURRENT_USER:
                ret = createKey(userRoot, hkey, key);
                regCloseKey.invoke(userRoot, new Object[]{Integer.valueOf(ret[0])});
                break;
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    }

    public static void writeStringValue(int hkey, String key, String valueName, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                writeStringValue(systemRoot, hkey, key, valueName, value);
                break;
            case HKEY_CURRENT_USER:
                writeStringValue(userRoot, hkey, key, valueName, value);
                break;
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    public static void deleteKey(int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int rc = -1;
        if (hkey == HKEY_LOCAL_MACHINE) {
            rc = deleteKey(systemRoot, hkey, key);
        } else if (hkey == HKEY_CURRENT_USER) {
            rc = deleteKey(userRoot, hkey, key);
        }
        if (rc != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
        }
    }

    public static void deleteValue(int hkey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int rc = -1;
        if (hkey == HKEY_LOCAL_MACHINE) {
            rc = deleteValue(systemRoot, hkey, key, value);
        } else if (hkey == HKEY_CURRENT_USER) {
            rc = deleteValue(userRoot, hkey, key, value);
        }
        if (rc != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
        }
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static int deleteValue(Preferences root, int hkey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        @SuppressWarnings("UnnecessaryBoxing")
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
            Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS)});
        if (handles[1] != REG_SUCCESS) {
            return handles[1];  // can be REG_NOTFOUND, REG_ACCESSDENIED
        }
        @SuppressWarnings("UnnecessaryUnboxing")
        int rc = ((Integer) regDeleteValue.invoke(root,
                new Object[]{
                    Integer.valueOf(handles[0]), toCstr(value)
                })).intValue();
        regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
        return rc;
    }

    private static int deleteKey(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        @SuppressWarnings({"UnnecessaryUnboxing", "UnnecessaryBoxing"})
        int rc = ((Integer) regDeleteKey.invoke(root,
                new Object[]{Integer.valueOf(hkey), toCstr(key)})).intValue();
        return rc;  // can REG_NOTFOUND, REG_ACCESSDENIED, REG_SUCCESS
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static String readString(Preferences root, int hkey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        @SuppressWarnings("UnnecessaryBoxing")
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
            Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ)});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        @SuppressWarnings("UnnecessaryBoxing")
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[]{
            Integer.valueOf(handles[0]), toCstr(value)});
        regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
        return (valb != null ? new String(valb).trim() : null);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static Map<String, String> readStringValues(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        @SuppressWarnings("Convert2Diamond")
        HashMap<String, String> results = new HashMap<String, String>();
        @SuppressWarnings("UnnecessaryBoxing")
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
            Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ)});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        @SuppressWarnings("UnnecessaryBoxing")
        int[] info = (int[]) regQueryInfoKey.invoke(root,
                new Object[]{Integer.valueOf(handles[0])});

        // int count = info[2]; // count
        int count = info[0];    // bug fix 20130112
        int maxlen = info[3]; // value length max
        for (int index = 0; index < count; index++) {
            @SuppressWarnings("UnnecessaryBoxing")
            byte[] name = (byte[]) regEnumValue.invoke(root, new Object[]{
                Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1)});
            String value = readString(hkey, key, new String(name));
            results.put(new String(name).trim(), value);
        }
        regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
        return results;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static List<String> readStringSubKeys(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        @SuppressWarnings("Convert2Diamond")
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
            Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ)});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root,
                new Object[]{Integer.valueOf(handles[0])});

        // int count = info[2]; // count
        int count = info[0];    // bug fix 20130112
        int maxlen = info[3]; // value length max
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumKeyEx.invoke(root, new Object[]{
                Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1)});
            results.add(new String(name).trim());
        }
        regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
        return results;
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static int[] createKey(Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return (int[]) regCreateKeyEx.invoke(root,
                new Object[]{Integer.valueOf(hkey), toCstr(key)});
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{
            Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS)});

        regSetValueEx.invoke(root,
                new Object[]{
                    Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value)
                });
        regCloseKey.invoke(root, new Object[]{Integer.valueOf(handles[0])});
    }

    private static byte[] toCstr(String str) {
        byte[] result = new byte[str.length() + 1];

        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }

}
