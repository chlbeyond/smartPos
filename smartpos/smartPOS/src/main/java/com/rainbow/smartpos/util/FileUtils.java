package com.rainbow.smartpos.util;

/**
 * Created by shushant on 3/19/14.
 */

import com.rainbow.smartpos.install.Constants;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * Static method for helping user to validates servers configurations
 */

public class FileUtils {

    /**
     * If is used to find if required binaries exist
     *
     * @return boolean
     */

    public static boolean checkIfExecutableExists() {

        //checking all file is cpu expensive task so only check required file

        if (
                new File(Constants.LIGHTTPD_SBIN_LOCATION).exists() &&
                        new File(Constants.PHP_SBIN_LOCATION).exists() &&
                        new File(Constants.MYSQL_DAEMON_SBIN_LOCATION).exists() &&
                        new File(Constants.MYSQL_MONITOR_SBIN_LOCATION).exists()
                ) {
            //so, all the required files exists
            return true;

        }
        //so, some of the required file are missing :(
        return false;

    }

//    public static boolean fileIsImage(File file) {
//        String type = getFileMimeType(file);
//        if(type.equals(Mim))
//    }

    public static String getFileMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getPath());
        return type;
    }

}
