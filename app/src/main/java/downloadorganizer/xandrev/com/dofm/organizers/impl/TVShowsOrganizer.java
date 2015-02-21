package downloadorganizer.xandrev.com.dofm.organizers.impl;

import android.text.method.TextKeyListener;
import android.util.Log;

import com.google.common.io.Files;

import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.organizers.Organizer;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConfiguration;
import downloadorganizer.xandrev.com.dofm.organizers.tvshows.impl.TVShowsOrganizerConstants;


public class TVShowsOrganizer extends Organizer {

    private String name;
    private String type;
    private final ConfigurationService config = ConfigurationService.getInstance(null);
    private static final String LOG_TAG = "TVShowsOrganizer";

    public TVShowsOrganizer() {
        name = TVShowsOrganizerConstants.NAME_ORGANIZER;
        type = TVShowsOrganizerConstants.TYPE_ORGANIZER;
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

    public String getPattern(){
        String pattern = config.getProperty(TVShowsOrganizerConfiguration.PATTERN_CONFIGURATION);
        if (pattern == null || pattern.isEmpty()) {
            pattern = TVShowsOrganizerConstants.PATTERN_DEFAULT_VALUE;
        }
        return pattern;
    }

    /**
     * Method that indicate the folder name which the item has to be located
     *
     * @param fileName item to relocate
     * @return folder name to relocate the item.
     */
    public String generateFolder(String fileName) {
        String pattern = getPattern();
        Pattern p = Pattern.compile(pattern);
        Log.d(LOG_TAG,"Pattern: "+pattern);
        Matcher m = p.matcher(fileName);
        boolean matching = m.matches();
        Log.d(LOG_TAG, "Filename : " + fileName + " matches: " + matching);
        if (matching) {
            
            String shows = m.group(1);
            shows = shows.replaceAll("\\.", " ");
            shows = capitalize(shows);
            Log.d(LOG_TAG, "TV Show extracted: " + shows);
            if (shows != null) {
                shows = shows.trim();
            }

            String rootFolder = getRootFolder();


            if (!getFolderSeason()) {
                return rootFolder + File.separator + shows;
            } else {
                String season = m.group(3);
                if (season == null || season.isEmpty()) {
                    season = m.group(5);
                }

                int seasonInt = -1;
                try {
                    seasonInt = Integer.parseInt(season);
                } catch (NumberFormatException ex) {
                    Log.w(LOG_TAG,"",ex);
                }
                if (seasonInt > 0) {
                    return rootFolder + File.separator + shows + File.separator + "Season " + seasonInt;
                } else {
                    return rootFolder + File.separator + shows + File.separator + "Unknown Season";
                }

            }
        }
        Log.d(LOG_TAG, "No TV Show extracted");
        return null;
    }

    private String capitalize(String shows) {
        String out = shows;
        if(shows != null){
            out = "";
            String lowerShow = shows.toLowerCase();
            String[] words = lowerShow.split(" ");
            for(String word : words){
                String capWord = word.toUpperCase().charAt(0) + word.toLowerCase().substring(1);
                if(out  != ""){
                    out += " ";
                }
                out += capWord;
            }
        }
        return out;
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
            Log.d(LOG_TAG, "Extension string value: "+extensionsStr);
            String[] extList = extensionsStr.split(",");
            if(extList != null){
                extensionList.addAll(Arrays.asList(extList));
            }
            Log.d(LOG_TAG, "Extension list size: "+extensionList.size());
        }
    }

    public boolean getFolderSeason() {
        return config.getPropertyAsBoolean(TVShowsOrganizerConfiguration.FOLDER_SEASON_CONFIGURATION);
    }
}
