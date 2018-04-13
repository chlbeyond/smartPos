/*
 * *
 *  * This file is part of DroidPHP
 *  *
 *  * (c) 2014 Shushant Kumar
 *  *
 *  * For the full copyright and license information, please view the LICENSE
 *  * file that was distributed with this source code.
 *
 */

package com.rainbow.smartpos.install;

import com.minipos.device.SerialPort;

/**
 * Created by shushant on 3/16/14.
 */
public class Constants {

//    public static final int MENU_POSITION_OF_NGINX = 0;
//    public static final int MENU_POSITION_OF_PHP = 1;
//    public static final int MENU_POSITION_OF_SQL_SERVER = 2;
//    public static final int MENU_POSITION_OF_ABOUT = 3;
//    public static final int MENU_POSITION_OF_GITHUB = 4;

    public static final String SERVER_LOCATION = "/mnt/sdcard/htdocs";

    public static final String PROJECT_LOCATION = "/mnt/sdcard/smartpos";

    public static final String INTERNAL_LOCATION = "/data/data/com.go2smartphone.smartpos";

    public static final String LIGHTTPD_SBIN_LOCATION = INTERNAL_LOCATION + "/components/lighttpd/sbin/lighttpd";
    public static final String LIGHTTPD_WWW_LOCATION = INTERNAL_LOCATION + "/components/lighttpd/www";
    public static final String LIGTTTPD_CONF_LOCATION = INTERNAL_LOCATION + "/components/lighttpd/conf/lighttpd.conf";

    public static final String PHP_SBIN_LOCATION = INTERNAL_LOCATION + "/components/php/sbin/php-cgi";
    public static final String PHP_INI_LOCATION = INTERNAL_LOCATION + "/components/php/conf/php.ini";

    public static final String MYSQL_DATA_DATA_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/data";

    public static final String MYSQL_SHARE_DATA_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/share";

    public static final String MYSQL_DAEMON_SBIN_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/mysqld";
    public static final String BUSYBOX_SBIN_LOCATION = INTERNAL_LOCATION + "/components/busybox/sbin/busybox";

    public static final String MYSQL_INI_LOCATION = INTERNAL_LOCATION + "/components/mysql/conf/mysql.ini";

    public static final String MYSQL_MONITOR_SBIN_LOCATION = INTERNAL_LOCATION + "/components/mysql/sbin/mysql-monitor";

    public static final String UPDATE_FROM_EXTERNAL_REPOSITORY = "/smartpos/repositroy/update.zip";




    public static String mPort = "/dev/ttySCOM1";
    public static int mBaudrate = 9600;
    public static int mDataBits = SerialPort.DATABITS_8;
    public static int mStopBits = SerialPort.STOPBITS_1;
    public static int mParity = SerialPort.PARITY_NONE;
    public static int mFlowControl = SerialPort.FLOWCONTROL_NONE;
    public static boolean openBalance=false;

    public static boolean isSystemUpdating=false;

}
