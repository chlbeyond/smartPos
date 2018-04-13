package com.rainbow.smartpos.common.components;

/**
 * Created by shushant on 3/18/14.
 */


import android.util.Log;

import com.rainbow.smartpos.common.shell.SH;
import com.rainbow.smartpos.install.Constants;

import java.util.Locale;

public class WebSocketExecutor implements ComponentProviderInterface {

    @Override
    public void connect() {

        String command = String.format(

                Locale.ENGLISH,
                "%s  %s",
                Constants.PHP_SBIN_LOCATION,
                Constants.LIGHTTPD_WWW_LOCATION + "/websocket.php &"
        );

        SH.run(command);
        Log.d("CMD", command);

    }

    @Override
    public void destroy() {

        SH.run(
                Constants.BUSYBOX_SBIN_LOCATION + " killall php");

    }
}
