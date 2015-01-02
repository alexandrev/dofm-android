package downloadorganizer.xandrev.com.dofm.organizers.service;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import downloadorganizer.xandrev.com.dofm.organizers.Organizer;

public class OrganizerManager {

    private static OrganizerManager instance;
    private List<Organizer> organizerList;
    private static final String ORGANIZER_SEPARATOR = ",";
    private static final String PACKAGE_NAME = "downloadorganizer.xandrev.com.dofm.organizers.impl.";
    private static final Object LOCK = new Object();
    private static final String LOG_TAG = "OrganizerManager";

    public static OrganizerManager getInstance(String configurationList) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new OrganizerManager(configurationList);
            }
        }
        return instance;
    }

    protected OrganizerManager(String configurationList) {
        organizerList = new ArrayList<Organizer>();
        if (configurationList != null) {
            String[] listString = configurationList.split(ORGANIZER_SEPARATOR);
            for (String str : listString) {
                Organizer organizerTarget = generateOrganizer(str);
                if (organizerTarget != null) {
                    organizerList.add(organizerTarget);
                }
            }
        }
        
        Collections.sort(organizerList);
        for(Organizer org: organizerList){
            Log.d(LOG_TAG,"Organizer: "+org.getRootFolder() + " Priority: "+ org.getPriority());
        }
        

    }

    private Organizer generateOrganizer(String str) {
        if (str != null) {
            String normalClass = PACKAGE_NAME + str;
            try {
                Object classObject = Class.forName(normalClass).newInstance();
                if (classObject instanceof Organizer) {
                    return (Organizer) classObject;
                }

            } catch (Exception ex) {
                Log.e("ERROR","Error detected",ex);
            }
        }
        return null;
    }

    public List<Organizer> getOrganizerList() {
        return organizerList;
    }
}
