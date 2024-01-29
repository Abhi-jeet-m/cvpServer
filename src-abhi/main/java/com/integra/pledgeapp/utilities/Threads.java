/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.integra.pledgeapp.utilities;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author debashishrout
 * @author shashidharac
 */
public class Threads extends Thread {

    public static int sleepTime;
    @Override
    public void run() {
        while (true) {
            try {

                String tokenresult = cleanAuthTokens();
                System.out.println("Tokens Cleared:" + tokenresult);
                long sleepTimeSec = sleepTime * 1000 * 60; //mins to milliseconds
                Thread.currentThread().sleep(sleepTimeSec);
            } catch (InterruptedException ex) {
                System.out.println("[Context Listner]InterruptedException " + ex.getMessage());
            }
        }

    }

    public synchronized String cleanAuthTokens() {
        JSONObject JS = ContextObjects.authTokens;
        Iterator<?> keys = JS.keys();
        JSONObject obsoletes = new JSONObject();
        String out = "0";
        while (keys.hasNext()) {
            try {
                String key = (String) keys.next();
                if (JS.get(key) instanceof JSONObject) {
                    JSONObject js = JS.getJSONObject(key);
                    Date d1 = (Date) js.get("reqtime");
                    Date crr = new Date();
                    int crrhr = crr.getMinutes();
                    crr.setMinutes(crrhr - sleepTime);
                    if (d1.compareTo(crr) < 0) {
                        obsoletes.put(key, "");
                    }
                }
            } catch (JSONException ex) {
                Logger.getLogger(Threads.class.getName()).log(Level.SEVERE, null, ex);
                out = ex.getMessage();
            }
        }
        if (obsoletes.length() > 0) {
            out=obsoletes.length()+"";
            Iterator<?> keys1 = obsoletes.keys();
            while (keys1.hasNext()) {
                String key = (String) keys1.next();
                ContextObjects.clearAuthTokens(key);
            }

        }
        return out;
    }
}
