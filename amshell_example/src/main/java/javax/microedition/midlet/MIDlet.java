/*
 * Copyright 2012 Kulikov Dmitriy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package javax.microedition.midlet;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.TreeMap;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.util.ContextHolder;

public abstract class MIDlet extends Application {

    private TreeMap<String, String> properties = new TreeMap();
    private Locale locale;

    @Override
    public void onCreate() {
        super.onCreate();

        ContextHolder.setContext(this);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("META-INF/MANIFEST.MF")));

            String line;
            int index;

            while ((line = br.readLine()) != null) {
                index = line.indexOf(':');

                if (index > 0) {
                    properties.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
                }
            }

            br.close();
        } catch (Throwable t) {
            System.out.println("getAppProperty() will not be available due to " + t.toString());
        }

        initApp();
    }

    public String getAppProperty(String key) {
        return properties.get(key);
    }

    public void notifyPaused() {
        ContextHolder.notifyPaused();
    }

    public void notifyDestroyed() {
        ContextHolder.notifyDestroyed();
    }

    public abstract void initApp();

    public abstract void startApp();

    public void setLocale(String language) {
        locale = new Locale(language);
        updateConfiguration();
    }

    public static String getDefaultLocale() {
        return Locale.getDefault().getCountry();
    }

    public void updateConfiguration() {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();

        if (locale != null) {
            conf.locale = locale;
        }

        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    public void onConfigurationChanged(Configuration conf) {
        updateConfiguration();
    }

    public void startActivity(Class cls) {
        startActivity(cls, null);
    }

    public void startActivity(Class cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        startActivity(intent);
    }

    public boolean platformRequest(String url) throws ConnectionNotFoundException {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e) {
            throw new ConnectionNotFoundException();
        }

        return true;
    }
}