/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadorganizer.xandrev.com.dofm.service;

import android.util.Log;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import downloadorganizer.xandrev.com.dofm.common.ConfigurationService;
import downloadorganizer.xandrev.com.dofm.common.Constants;
import downloadorganizer.xandrev.com.dofm.organizers.Organizer;
import downloadorganizer.xandrev.com.dofm.organizers.service.OrganizerManager;


public class ExecutorService {

    private static ExecutorService instance;
    private static final Object LOCK = new Object();

    private final ConfigurationService cfg;
    private final OrganizerManager manager;
    private final List<Organizer> organizerList;
    private String finalDirectory;
    private String initialDirectory;
    private boolean isRunning = false;

    public static ExecutorService getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ExecutorService();
            }
        }
        return instance;
    }
    private Date executionTime;

    private ExecutorService() {
        executionTime = new Date();
        cfg = ConfigurationService.getInstance(null);
        String organizerListString = cfg.getProperty(Constants.ORGANIZER_NAMES);
        if(organizerListString == null || organizerListString.isEmpty()){
            organizerListString = "TVShowsOrganizer,MovieOrganizer,FileOrganizer";
        }
        manager = OrganizerManager.getInstance(organizerListString);
        organizerList = manager.getOrganizerList();
        reloadConfiguration();

    }

    private void reloadConfiguration() {
        finalDirectory = cfg.getProperty(Constants.FINAL_FOLDER);
        initialDirectory = cfg.getProperty(Constants.INITIAL_FOLDER);
    }

    public void applyExistentFiles() {
        reloadConfiguration();
        applyExistentFiles(initialDirectory);
        executionTime = new Date();
    }

    public void applyExistentFiles(String initialDirectory) {
        if (!isRunning) {
            Log.d("DEBUG","Initial folder detected: " +initialDirectory);
            isRunning = true;
            File file = new File(initialDirectory);
            Log.d("DEBUG","Starting to apply the organization to the folder: " + file.getAbsolutePath());
            applyOrganizers(file);
            Log.d("DEBUG","Finished the organization to the selected folder");
            isRunning = false;
        }
    }

    private void applyOrganizers(File initialFolder) {
        ArrayList<String> list = new ArrayList<String>();
        Log.d("DEBUG","Organizer list size: "+organizerList.size());
        for (Organizer org : organizerList) {
            Log.i("INFO","Organizer: " + org.getClass().toString());
            Log.d("DEBUG","Organizer Extension List: "+org.getExtension().size());
            Collection<File> fileList = org.getFiles(initialFolder);
            if (fileList != null) {
                Log.d("INFO","File List: " + fileList);
                for (File fd : fileList) {
                    Log.d("INFO","File Name: " + fd.getAbsolutePath());
                    String folder = org.generateFolder(fd.getName());
                    if (folder != null && !list.contains(fd.getAbsolutePath())) {
                        list.add(fd.getAbsolutePath());
                        organizeFile(fd, folder);
                    }
                }
            }
        }

    }

    public void organizeFile(File fd, String folder) {
        Log.i("INFO", "Folder: " + folder);
        Log.i("INFO","Root Folder: " + folder);
        String finalPath = finalDirectory + File.separator + folder;
        File dirPath = new File(finalPath);
        Log.i("INFO","Final Path: " + finalPath);
        if (!dirPath.exists()) {
            boolean result = dirPath.mkdirs();
            Log.d("DEBUG", "Resultado de la creacion de directorios: " + result);
        }
        File finalFile = new File(finalPath + File.separator + fd.getName());
        if (!finalFile.exists()) {
            try {
                Log.i("INFO","Final path not exist. Copying the file at: " + finalFile.getAbsolutePath());
                String origPath = fd.getAbsolutePath();
                boolean renamed = fd.renameTo(finalFile);
                boolean deleted = false;
                boolean copied = false;
                Log.i("INFO","Renamed attempt result successfully: " + renamed);
                if (!renamed) {
                    Log.i("INFO","Trying to copy the file to: " + finalFile.getAbsolutePath());
                    Files.copy(fd, finalFile);
                    copied = finalFile.exists();
                    if (copied) {

                            deleted = fd.delete();
                            Log.i("INFO","Copy finished: " + finalFile.getAbsolutePath());

                    }

                }

                Log.i("INFO", "Starting to audit the file organized");
                Log.i("INFO","Audit the file organized completed");
            } catch (IOException ex) {
                Log.e("ERROR", "", ex);
            }
        }
        fd.delete();
    }

    public void organizeFile(File fd) {
        Log.i("INFO", "Starting organization for file: "+fd);
        if(fd != null) {
            for (Organizer org : getOrganizers()) {
                Log.d("DEBUG", "Starting organization with organizer: "+org.getClass().getName());
                if (org.apply(fd)) {
                    Log.d("DEBUG","The file is allowed to use this organizer");
                    String folder = org.generateFolder(fd.getName());
                    if(folder != null && !folder.isEmpty()) {
                        Log.i("INFO", "Folder: " + folder);
                        String finalPath = finalDirectory + File.separator + folder;
                        File dirPath = new File(finalPath);
                        Log.i("INFO", "Final Path: " + finalPath);
                        if (!dirPath.exists()) {
                            boolean result = dirPath.mkdirs();
                            Log.d("DEBUG", "Resultado de la creacion de directorios: " + result);
                        }
                        File finalFile = new File(finalPath + File.separator + fd.getName());
                        Log.d("DEBUG", "Final file: " + finalFile.getAbsolutePath());
                        if (!finalFile.exists()) {
                            try {
                                Log.i("INFO", "Final path not exist. Copying the file at: " + finalFile.getAbsolutePath());
                                String origPath = fd.getAbsolutePath();
                                boolean renamed = fd.renameTo(finalFile);
                                boolean deleted = false;
                                boolean copied = false;
                                Log.i("INFO", "Renamed attempt result successfully: " + renamed);
                                if (!renamed) {
                                    Log.i("INFO", "Trying to copy the file to: " + finalFile.getAbsolutePath());
                                    Files.copy(fd, finalFile);
                                    copied = finalFile.exists();
                                    if (copied) {

                                        deleted = fd.delete();
                                        Log.i("INFO", "Copy finished: " + finalFile.getAbsolutePath());

                                    }

                                }
                                Log.i("INFO", "Starting to audit the file organized");
                                Log.i("INFO", "Audit the file organized completed");
                            } catch (IOException ex) {
                                Log.e("ERROR", "", ex);
                            }
                        }
                        if(fd.exists()) {
                            fd.delete();
                        }
                        break;
                    }
                }
            }
        }
        Log.i("INFO", "Finished organization for file: "+fd);
    }


    public String getTime() {
        return executionTime.toString();
    }

    public Collection<Organizer> getOrganizers() {
        return organizerList;
    }


}