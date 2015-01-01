package downloadorganizer.xandrev.com.dofm.organizers.impl;


import android.util.Log;

import com.google.common.io.Files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.organizers.Organizer;
import downloadorganizer.xandrev.com.dofm.organizers.movies.impl.MovieOrganizerConfiguration;
import downloadorganizer.xandrev.com.dofm.organizers.movies.impl.MovieOrganizerConstants;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConfiguration;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConstants;


public class MovieOrganizer extends Organizer {

    private ConfigurationService config;
    private String name;
      private String type;




    public MovieOrganizer() {
        config = ConfigurationService.getInstance(null);
        name = MovieOrganizerConstants.NAME_ORGANIZER;
        type = MovieOrganizerConstants.TYPE_ORGANIZER;
    }

    public String getRootFolder() {
        String rootFolder = config.getProperty(TVShowsOrganizerConfiguration.ROOT_FOLDER_CONFIGURATION);
        if (rootFolder == null) {
            rootFolder = TVShowsOrganizerConstants.ROOT_FOLDER_DEFAULT_VALUE;
        }
        return rootFolder;
    }

    @Override
    public Collection<String> getExtension() {
        ArrayList<String> extensionList = new ArrayList<String>();
        String extensionsStr = config.getProperty(TVShowsOrganizerConfiguration.EXTENSIONS_CONFIGURATION);
        if (extensionsStr == null || extensionsStr.isEmpty()) {
            extensionsStr = TVShowsOrganizerConstants.EXTENSION_DEFAULT_VALUE;
        }
        parseExtension(extensionsStr,extensionList);
        return extensionList;
    }

    @Override
    public void reload() {

    }

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    public String generateFolder(String fileName) {

        String rootFolder = getRootFolder();
        if (!getOwnFolder()) {
            return rootFolder;
        } else {
            int index = fileName.lastIndexOf('.');
            if (index != -1) {
                String folder = fileName.substring(0, index);
                return rootFolder + File.separator + folder.trim();
            }
        }
        return null;
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

    public String extractMovieName(String origPath) {
        String out = "";
        if (origPath != null) {
            int idxOf = origPath.lastIndexOf(File.separatorChar);
            int lstIndexOf = origPath.lastIndexOf('.');
            if (lstIndexOf < 0) {
                lstIndexOf = origPath.length();
            }

            String tmp = origPath.substring(idxOf + 1, lstIndexOf);
            tmp = tmp.replace('.', ' ');
            tmp = tmp.replaceAll("\\([^\\)]*\\)", "");
            out = tmp.trim();
        }
        return out;
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



    private void parseExtension(String extensionsStr,Collection<String> extensionList) {
        if(extensionsStr != null && !extensionsStr.isEmpty()){
            Log.d("DEBUG", "Extension string value: "+extensionsStr);
            String[] extList = extensionsStr.split(",");
            if(extList != null){
                extensionList.addAll(Arrays.asList(extList));
            }
            Log.d("DEBUG", "Extension list size: "+extensionList.size());
        }
    }

    public boolean getOwnFolder(){
        return config.getPropertyAsBoolean(MovieOrganizerConfiguration.OWN_FOLDER_MOVIE);
    }

}
