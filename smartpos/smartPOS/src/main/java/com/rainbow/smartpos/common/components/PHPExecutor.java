package com.rainbow.smartpos.common.components;


import com.rainbow.smartpos.common.shell.SH;
import com.rainbow.smartpos.install.Constants;

/**
 * Created by shushant on 3/19/14.
 */
public class PHPExecutor implements ComponentProviderInterface {

    @Override
    public void connect() {

       /* String[] envVariable = new String[]{
                "PHP_FCGI_CHILDREN=4",
                "PHP_FCGI_MAX_REQUESTS=10000",
                "PHPRC="+Constants.PROJECT_LOCATION+"/conf/php.ini",
                "TMPDIR="+Constants.INTERNAL_LOCATION+"/tmp"
        };

        String command = String.format(

                Locale.ENGLISH,
                "%s -f %s -D",
                Constants.LIGHTTPD_SBIN_LOCATION,
                Constants.PROJECT_LOCATION + "/conf/lighttpd.conf"
        );

        SH.run(command, envVariable);*/

        //do we really need this ??

    }

    @Override
    public void destroy() {

        SH.run(
                Constants.BUSYBOX_SBIN_LOCATION + " killall php");

    }
}
