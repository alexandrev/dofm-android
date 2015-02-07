package downloadorganizer.xandrev.com.dofm.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import downloadorganizer.xandrev.com.dofm.R;
import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.common.Constants;
import downloadorganizer.xandrev.com.dofm.utils.RecursiveFileObserver;

/**
 * Created by alexa on 12/7/2014.
 */
public class OrganizeService extends Service {

    private static final String LOG_TAG = "OrganizeService";

    private FileObserver observer;
    private ConfigurationService cfg;
    private ExecutorService service;
    private LocalBroadcastManager broadcaster;


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
        broadcaster = LocalBroadcastManager.getInstance(this);
        service = ExecutorService.getInstance();
        Log.d(LOG_TAG, "onCreate");
        String initialFolder = cfg.getProperty(Constants.INITIAL_FOLDER);
        Log.d(LOG_TAG, "Initial folder detected:"+initialFolder);
        if(initialFolder == null ||initialFolder.isEmpty()){
            initialFolder = Constants.INITIAL_FOLDER;
        }
        Log.d(LOG_TAG, "Final Initial folder detected:"+initialFolder);
        final File fileInitFolder = new File(initialFolder);
        final Service thisService = this;
        Log.d(LOG_TAG,"Final: "+fileInitFolder.getAbsolutePath());
        Log.d(LOG_TAG, "Initial Folder existed:" + fileInitFolder.exists());

        observer = new RecursiveFileObserver(fileInitFolder.getAbsolutePath()) {
            @Override

            public void onEvent(int event, String path) {
                if(event == FileObserver.CREATE || event == FileObserver.MOVED_TO){
                    Log.d(LOG_TAG,"Created event has been fired from file:"+path);
                    File fTmp = new File(path);
                    if(!fTmp.isDirectory()) {
                        Log.d(LOG_TAG, "File: " + fTmp.getAbsolutePath());
                        Log.d(LOG_TAG, "Starting organization from:" + path);
                        File finalPath = service.organizeFile(fTmp);
                        Log.d(LOG_TAG, "Finished organization from:" + path);
                        sendResult(finalPath.getAbsolutePath());
                    }else{
                        this.stopWatching();
                        this.startWatching();
                    }
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

    public void sendResult(String message) {
        Intent local = new Intent();
        local.setAction("downloadorganizer.xandrev.com.dofm.Message");
        if(message != null) {
            local.putExtra("data", message);
        }
        this.sendBroadcast(local);
    }

}
