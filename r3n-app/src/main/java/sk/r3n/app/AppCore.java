package sk.r3n.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sk.r3n.util.FileUtil;

public class AppCore {

    public static final String APP_CORE_PROPS = "sk.r3n.app.CoreProperties";

    public static final String MODULE = "R3N_M_";

    private static final Map<String, String> appProperties = new HashMap<String, String>();

    private static final Map<String, Object> moduleMap = new HashMap<String, Object>();

    public static void main(String[] args) {
        String propertyFile = System.getProperty(APP_CORE_PROPS, "");
        if (!propertyFile.equals("")) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(new URL(propertyFile).getFile()));
                String line;
                List<String[]> modules = new ArrayList<String[]>();
                while ((line = br.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] data = line.split("=");
                        String key = data[0];
                        String value = data[1];
                        if (key.startsWith(MODULE)) {
                            modules.add(data);
                        } else {
                            appProperties.put(key, value);
                        }
                    }
                }
                for (String[] data : modules) {
                    String key = data[0];
                    key = key.substring(6);
                    register(key, Class.forName(data[1]).newInstance());
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                FileUtil.close(br);
            }
        }
    }

    public static String getProperty(String key) {
        return appProperties.get(key);
    }

    public static Object getModule(String key) {
        return moduleMap.get(key);
    }

    private static Method getMethod(String name, Object object) {
        Method method = null;
        try {
            Class[] noparams = {};
            method = object.getClass().getDeclaredMethod(name, noparams);
        } catch (Exception ex) {
        }
        return method;
    }

    public static void start(String key) {
        try {
            Object object = moduleMap.get(key);
            Method method = getMethod("start", object);
            if (method != null) {
                method.invoke(object, new Object[]{});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void stop(String key) {
        try {
            Object object = moduleMap.get(key);
            Method method = getMethod("stop", object);
            if (method != null) {
                method.invoke(object, new Object[]{});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void stop() {
        List<String> keys = new ArrayList<String>();
        keys.addAll(moduleMap.keySet());
        for (String key : keys) {
            remove(key);
        }
        System.exit(0);
    }

    public static void register(String key, Object object) {
        moduleMap.put(key, object);
        try {
            start(key);
        } catch (Exception e) {
            moduleMap.remove(key);
        }
    }

    public static void remove(String key) {
        try {
            stop(key);
        } finally {
            moduleMap.remove(key);
        }
    }
}
