package downloadorganizer.xandrev.com.dofm.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import downloadorganizer.xandrev.com.dofm.R;
import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.common.Constants;

/**
 * Created by alexa on 12/7/2014.
 */
public class OrganizeService extends Service {

    private static final String LOG_TAG = "OrganizeService";

    private FileObserver observer;
    private ConfigurationService cfg;
    private ExecutorService service;


    public OrganizeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Context context = getApplicationContext();
        cfg = ConfigurationService.getInstance(context);
        cfg.reloadConfiguration(context);
        service = ExecutorService.getInstance();
        Log.d(LOG_TAG, "onCreate");
        String initialFolder = cfg.getProperty(Constants.INITIAL_FOLDER);
        Log.d(LOG_TAG, "Initial folder detected:"+initialFolder);
        if(initialFolder == null ||initialFolder.isEmpty()){
            initialFolder = Constants.INITIAL_FOLDER;
        }
        Log.d(LOG_TAG, "Final Initial folder detected:"+initialFolder);
        final File fileInitFolder = new File(initialFolder);
        Log.d(LOG_TAG,"Final: "+fileInitFolder.getAbsolutePath());
        Log.d(LOG_TAG, "Initial Folder existed:" + fileInitFolder.exists());

        observer = new FileObserver(fileInitFolder.getAbsolutePath()) {
            @Override

            public void onEvent(int event, String path) {
                if(event == FileObserver.CREATE){
                    Log.d(LOG_TAG,"Created event has been fired from file:"+path);
                    File fTmp = new File(fileInitFolder.getAbsolutePath()+File.separator+path);
                    Log.d(LOG_TAG,"File: "+fTmp.getAbsolutePath());
                    Log.d(LOG_TAG,"Starting organization from:"+path);
                    service.organizeFile(fTmp);
                    Log.d(LOG_TAG,"Finished organization from:"+path);
                }
            }
        };
        observer.startWatching();
        Log.d(LOG_TAG,"Started Watching");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, R.string.toast_started, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onDestroy() {
        observer.stopWatching();
        Toast.makeText(this, R.string.toast_stopped, Toast.LENGTH_LONG).show();
        Log.d(LOG_TAG, "onDestroy");
    }


}
