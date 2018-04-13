package com.sanyipos.android.sdk.androidUtil;

import com.sanyipos.sdk.utils.DecodeWrapper;
import com.sanyipos.sdk.utils.MessageGZIP;


/**
 * Created by ss on 2016/3/25.
 */
public class DecodeBaseUtil extends DecodeWrapper {

    @Override
    public byte[] decodeMessage(String message) {
        byte[] bytes = android.util.Base64.decode(message, android.util.Base64.DEFAULT);
        return MessageGZIP.uncompressToString(bytes, MessageGZIP.encode);
    }
}
