package downloadorganizer.xandrev.com.dofm;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;


public class ImportExportActivity extends ActionBarActivity {

    private static final int FILE_SELECT_CODE = 0;
    private static final String TAG = "DEBUG";
    private ConfigurationService config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);
        config = ConfigurationService.getInstance(getApplicationContext());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    public void importConfig(final View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void exportConfig(final View view){
        File fTmp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        Log.d("DEBUG", "File folder:"+fTmp.getAbsolutePath());

        if(fTmp != null){
            if(!fTmp.exists()){
                fTmp.mkdirs();
            }
            long tTime = new Date().getTime();
            String newConfigFile = fTmp.getAbsolutePath() + File.separator + "config." + tTime + ".properties";
            Log.d("DEBUG", "Final exporting filename:"+newConfigFile);
            File fConfig = new File(newConfigFile);
            if(!fConfig.exists()){
                Log.d("DEBUG", "File not exists");
                try {
                    fConfig.createNewFile();
                    Log.d("DEBUG", "File created");
                    FileWriter fWriter = new FileWriter(newConfigFile);
                    Map<String, ?> propertyValues = config.getAll();
                    Iterator<String> itKeys = propertyValues.keySet().iterator();
                    while(itKeys.hasNext()){
                        String key  = itKeys.next();
                        Object valueObj = propertyValues.get(key);
                        String value = valueObj.toString();
                        fWriter.write(key+":"+value+":"+valueObj.getClass().toString()+"\n");
                        Log.d("DEBUG", "Writing: "+key+":"+value);
                    }
                    fWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = getPath(this, uri);
                        Log.d(TAG, "File Path: " + path);
                        File file = new File(path);
                        importConfig(file);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }



                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importConfig(File file) {
        if(file != null){
            try {
                FileInputStream fis = this.getApplicationContext().openFileInput(file.getAbsolutePath());
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String values[] = line.split(":");
                    if(values != null && values.length > 0){
                        config.putProperty(values[0],values[1],values[2]);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getPath(Context context, Uri uri) throws URISyntaxException {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(uri,  proj, null, null, null);
            if(cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import_export, menu);
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
            return true;
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
            View rootView = inflater.inflate(R.layout.fragment_import_export, container, false);
            return rootView;
        }
    }
}
