package downloadorganizer.xandrev.com.dofm.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.preference.PreferenceManager;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by alexa on 12/7/2014.
 */
public class ConfigurationService {

    private static ConfigurationService instance;

    private SharedPreferences prefs;



    private static final Object LOCK = new Object();

    public static ConfigurationService getInstance(Context context){
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ConfigurationService(context);
            }
        }
        return instance;
    }


    private ConfigurationService(Context context){
        reloadConfiguration(context);
    }

    public void reloadConfiguration(Context context){
        Log.d("DEBUG", "Reloading configuration");
        if(context != null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
            print();
        }
        Log.d("DEBUG", "Configuration reloaded");
    }

    public String getProperty(String key){
        if(prefs != null){
            return prefs.getString(key,"");
        }
        return null;
    }

    public Boolean getPropertyAsBoolean(String key){
        if(prefs != null){
            return prefs.getBoolean(key,false);
        }
        return null;
    }
    public void print(){
        Log.d("DEBUG", "Printing configuration values");
        if(prefs != null){
            Map<String, String> names = (Map<String, String>) prefs.getAll();
            if(names != null){
                Iterator<String> it = names.keySet().iterator();
                while(it.hasNext()){
                    Object name = (Object) it.next();
                    try {
                        Log.d("DEBUG", name + " - " + names.get(name));
                    }catch(ClassCastException ex){

                    }
                }

            }
        }
        Log.d("DEBUG", "Printed configuration values");

    }

    public Map<String, ?> getAll(){
        return prefs.getAll();
    }


    public boolean isEmpty() {
        return prefs == null || prefs.getAll().size() == 0;
    }

    public void putProperty(String value, String value1, String value2) {
        SharedPreferences.Editor editor = prefs.edit();
        if(value2 != null && value2.contains("String")) {
            editor.putString(value, value1);
        }
        else if(value2.contains("Boolean")){
            editor.putBoolean(value, Boolean.parseBoolean(value1));
        }
        editor.commit();
    }
}
