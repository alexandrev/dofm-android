package downloadorganizer.xandrev.com.dofm.organizers.impl;

import android.util.Log;

import com.google.common.io.Files;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.organizers.Organizer;
import downloadorganizer.xandrev.com.dofm.organizers.files.impl.FileOrganizerConfiguration;
import downloadorganizer.xandrev.com.dofm.organizers.files.impl.FileOrganizerConstants;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConfiguration;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConstants;


public class FileOrganizer extends Organizer {

    private ConfigurationService config;
    private String name;
    private String type;
    private Collection<String> extensionList;


    public FileOrganizer() {
        config = ConfigurationService.getInstance(null);
        name = FileOrganizerConstants.NAME_ORGANIZER;
        type = FileOrganizerConstants.TYPE_ORGANIZER;
        reload();
    }

    /**
     * Method that return the root folder for this organizer
     *
     * @return root folder name
     */
    public String getRootFolder() {
        String rootFolder = config.getProperty(TVShowsOrganizerConfiguration.ROOT_FOLDER_CONFIGURATION);
        if (rootFolder == null) {
            rootFolder = TVShowsOrganizerConstants.ROOT_FOLDER_DEFAULT_VALUE;
        }
        return rootFolder;
    }

    @Override
    public Collection<String> getExtension() {
        return extensionList;
    }

    @Override
    public void reload() {
        parseExtension();
    }

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    public String generateFolder(String fileName) {
        String finalFolder = null;
        HashMap<String,String> extensionsFolder = parseExtension();
        Log.d("DEBUG","Starting to generate folder");
        if (fileName != null && !fileName.isEmpty()) {
            Log.d("DEBUG","Starting to work with file:" + fileName);
            String extension = Files.getFileExtension(fileName);
            Log.d("DEBUG","Extracted extension from file: " + extension);
            if (extension != null && !extension.isEmpty()) {
                String folder = extensionsFolder.get(extension);
                Log.d("DEBUG","Recovered folder from extension: " + folder);
                if (folder != null && !folder.isEmpty()) {
                    finalFolder = getRootFolder() + File.separator + folder;
                }
            }
        }
        Log.d("DEBUG","Final path:" + finalFolder);
        return finalFolder;
    }

    public int getPriority() {
        String priorityStr = config.getProperty(TVShowsOrganizerConfiguration.PRIORITY_CONFIGURATION);
        int priority = -1;
        if (priorityStr == null) {
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException ex) {
                priority = TVShowsOrganizerConstants.PRIORITY_DEFAULT_VALUE;
            }
        }
        return priority;
    }

    public int compareTo(Object t) {
        if (t instanceof Organizer) {
            return getPriority() - ((Organizer) t).getPriority();
        }
        return -1;
    }

    private HashMap<String,String> parseExtension() {
        extensionList = new ArrayList<String>();
        HashMap<String,String> extensionsFolder = new HashMap();
        String extensionsStr = config.getProperty(FileOrganizerConfiguration.EXTENSION_CONFIGURATION);
        if (extensionsStr == null || extensionsStr.isEmpty()) {
            extensionsStr = FileOrganizerConstants.EXTENSION_DEFAULT_VALUE;
        }
        if (extensionsStr != null && !extensionsStr.isEmpty()) {
            String[] extensionArray = extensionsStr.split(";");
            if (extensionArray != null) {
                Log.d("DEBUG","Extension array length: " + extensionArray.length);
                for (String extension : extensionArray) {
                    String[] extParsed = extension.split("=");
                    if (extParsed != null && extParsed.length == 2) {
                        Log.d("DEBUG","Extension parsed length: " + extParsed.length);
                        String folder = extParsed[0];
                        String values = extParsed[1];
                        Log.d("DEBUG","Folder: " + folder);
                        Log.d("DEBUG","Value: " + values);
                        String[] valueArray = values.split(",");
                        if (valueArray != null && valueArray.length > 0) {
                            for (String val : valueArray) {
                                if (!extensionsFolder.containsKey(val)) {
                                    Log.d("DEBUG","Adding a new extension folder for extension: " + val);
                                    extensionsFolder.put(val, folder);
                                    extensionList.add(val);
                                    Log.d("DEBUG","Added a new extension folder for extension");
                                }
                            }
                        }
                    }
                }
            }
            Log.d("DEBUG","Final parsed size: " + extensionsFolder.size());
        }
        return extensionsFolder;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
