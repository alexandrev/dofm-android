package downloadorganizer.xandrev.com.dofm.service;

import java.io.File;
import java.io.IOException;

import fi.iki.elonen.SimpleWebServer;

/**
 * Created by alexa on 2/22/2015.
 */
public class WebService {
    private int port = 2809;
    private SimpleWebServer server;

    public WebService(){
        try {
            File fTmp = File.createTempFile("text","sux");
            server = new SimpleWebServer("0.0.0.0",port,fTmp,true);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
