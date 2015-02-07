package downloadorganizer.xandrev.com.dofm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.service.ExecutorService;


public class MainActivity extends ActionBarActivity {

    private Intent mServiceIntent;
    private ExecutorService service;
    private ConfigurationService configuration;
    private boolean isRunning;
    private Menu menu;
    private ListView organizedItems;

    private static final String LOG_TAG = "MainActivity";
    private BroadcastReceiver receiver;
    private ArrayList<String> list;


    public void manageBackgroundService(MenuItem serviceMenuItem){
        if(isRunning) {
            stopService(serviceMenuItem);

        }
        else{
            startService(serviceMenuItem);
        }
    }

    private void startService(MenuItem serviceMenuItem) {
        mServiceIntent = new Intent(this, downloadorganizer.xandrev.com.dofm.service.OrganizeService.class);
        this.startService(mServiceIntent);
        isRunning=true;
        if (serviceMenuItem != null) {
            serviceMenuItem.setTitle(R.string.stopService);
        }
    }

    private void stopService(MenuItem serviceMenuItem) {
        if(mServiceIntent != null) {
            this.stopService(mServiceIntent);
            isRunning = false;
            mServiceIntent = null;
            if (serviceMenuItem != null) {
                serviceMenuItem.setTitle(R.string.startService);
            }
        }
    }


    public void launchOrganizer(final View view){

        ArrayList<File> files = service.applyExistentFiles();
        Toast.makeText(this, R.string.toast_completed, Toast.LENGTH_LONG).show();
        TextView tv1 = (TextView)findViewById(R.id.textView3);
        tv1.setText(service.getTime().toString());
        if(files != null){
            list = new ArrayList<String>();
            for (int i = 0; i < files.size(); ++i) {
                if(files.get(i) != null) {
                    list.add(files.get(i).getAbsolutePath());
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            if(this.organizedItems != null) {
                this.organizedItems.setAdapter(adapter);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        this.organizedItems = (ListView) findViewById(R.id.listView);
        configuration.reloadConfiguration(getApplicationContext());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        list = new ArrayList<String>();
        filter.addAction("downloadorganizer.xandrev.com.dofm.Message");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            String s = intent.getStringExtra("data");
                Log.d(LOG_TAG, "Received a new message: "+s);
                if(organizedItems != null) {
                    Log.d(LOG_TAG, "Recovering array adapter");
                    list.add(s);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
                    organizedItems.setAdapter(adapter);
                    Log.d(LOG_TAG, "Adding new item to the array adapter");
                }
            }
        };
        registerReceiver(receiver,filter);
        configuration = ConfigurationService.getInstance(getApplicationContext());
        configuration.reloadConfiguration(getApplicationContext());
        service = ExecutorService.getInstance();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        if(configuration.isEmpty()){
            startActivityForResult(new Intent(this, SettingsActivity.class), 0);
        }
        else{
            MenuItem mItem = menu.findItem(R.id.action_service);
            startService(mItem);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this,SettingsActivity.class), 0);
            return true;
        }
        else if(id == R.id.action_service){
            manageBackgroundService(item);
        }
        else if(id == R.id.action_imexport){
            startActivityForResult(new Intent(this,ImportExportActivity.class),0);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
