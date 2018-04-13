package com.rainbow.smartpos.common.components;

/**
 * Created by shushant on 3/18/14.
 */


import android.util.Log;

import com.rainbow.smartpos.common.shell.SH;
import com.rainbow.smartpos.install.Constants;

import java.util.Locale;

public class LighttpdExecutor implements ComponentProviderInterface {

    @Override
    public void connect() {

        String command = String.format(

                Locale.ENGLISH,
                "%s -f %s",
                Constants.LIGHTTPD_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/lighttpd.conf"
        );

        SH.run(command);
        Log.d("CMD", command);

    }

    @Override
    public void destroy() {

        SH.run(
                Constants.BUSYBOX_SBIN_LOCATION + " killall lighttpd");

    }
}
