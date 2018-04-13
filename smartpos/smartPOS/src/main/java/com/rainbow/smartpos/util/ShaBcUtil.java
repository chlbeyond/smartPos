package com.rainbow.smartpos.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by ss on 2016/11/15.
 */

public class ShaBcUtil {

    public static boolean bcPackAgeSHA1(String APKpath, String SHApath) {

        MessageDigest msgDigest = null;

        try {

            msgDigest = MessageDigest.getInstance("SHA-1");

            byte[] bytes = new byte[1024];

            int byteCount;

            FileInputStream fis = new FileInputStream(new File(APKpath));

            while ((byteCount = fis.read(bytes)) > 0)

            {

                msgDigest.update(bytes, 0, byteCount);

            }

//            BigInteger bi = new BigInteger(1, msgDigest.digest());
//
//            String sha = bi.toString(16);
            String sha = new String(Hex.encodeHex(msgDigest.digest()));
            fis.close();

            if (sha.equals(openSHA1(SHApath))) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
        return false;
    }

    public static String openSHA1(String filePath) {
        String res = "";

        try {

            FileInputStream fin = new FileInputStream(filePath);

            int length = fin.available();

            byte[] buffer = new byte[length];

            fin.read(buffer);

            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return res;
    }


}
