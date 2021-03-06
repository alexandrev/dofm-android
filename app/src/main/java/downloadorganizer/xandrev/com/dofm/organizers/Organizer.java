package downloadorganizer.xandrev.com.dofm.organizers;

import android.util.Log;

import com.google.common.io.Files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * General interface of a new item organizer
 *
 * @author xandrev.com
 *
 */
public abstract class Organizer implements Comparable<Object>, FilenameFilter {

    private static final String LOG_TAG = "Organizer";

    /**
     * Method that indicate the priority of the organizer to priorize its
     * application
     *
     * @return priority. The low the better.
     */
    public abstract int getPriority();

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    public abstract String generateFolder(String fileName);

    /**
     * Method that return the root folder for this organizer
     *
     * @return root folder name
     */
    public abstract String getRootFolder();

    public abstract Collection<String> getExtension();

    public Collection<File> getFiles(File initialFolder) {
        Collection<File> out = new ArrayList<>();
        Log.d(LOG_TAG,"Recovering the files from: "+initialFolder);
        if (initialFolder != null && initialFolder.exists()) {
            Log.d(LOG_TAG,"Recovering extensions");
            final Collection<String> extensionList = getExtension();
            Log.d(LOG_TAG,"Extensions list recovered: "+extensionList);
            if (extensionList.size() > 0) {
                Log.d(LOG_TAG,"Extensions list size: "+extensionList.size());
                File[] tmpOut = initialFolder.listFiles();
                Log.d(LOG_TAG,"Converting array to list");
                if(tmpOut != null && tmpOut.length > 0) {
                    for(File f : tmpOut) {
                        Log.d(LOG_TAG,"Checking file: "+f.getAbsolutePath());
                        if(f.exists() && f.isDirectory()){
                            Log.i("DEBUG","Folder detected:"+f.getAbsolutePath());
                            Collection<File> tmpListEmbedded = getFiles(f);
                            if(tmpListEmbedded != null && tmpListEmbedded.size() > 0){
                                out.addAll(tmpListEmbedded);
                            }
                        }else {
                            if(accept(initialFolder,f.getAbsolutePath())) {
                                out.add(f);
                            }
                        }
                    }
                }
                Log.d(LOG_TAG,"Returned object: "+out.size());
            }
        }
        return out;
    }

    public boolean apply(File fd) {
        if(fd != null) {
            String ext = Files.getFileExtension(fd.getAbsolutePath());
            if(ext != null && !ext.isEmpty()){
                return getExtension().contains(ext);
            }
        }
        return false;
    }

    public boolean accept(File dir, String filename) {
        Collection<String> extensionList = getExtension();
        Log.d(LOG_TAG,"Checking for the file: "+ filename);
        Log.d(LOG_TAG,"Extension list size: "+ extensionList.size());
        String extension = Files.getFileExtension(filename);
        Log.d(LOG_TAG,"Extension recovered: "+ extension);
        if (extension != null) {
            Log.d(LOG_TAG,"Extension contained in list: "+ extensionList.contains(extension));
            return extensionList.contains(extension);
        }
        return false;
    }

    public abstract void reload();

}
