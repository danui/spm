package com.apfrank.spm;

import java.security.MessageDigest;

public class HashTool {
    
    public static String getMd5(String str) throws Exception {
        return getHash("MD5", str);
    }

    public static String getHash(String algo, String str)
        throws Exception
    {
        MessageDigest md = MessageDigest.getInstance(algo);
        md.update(str.getBytes());
        byte[] buf = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i=0; i < buf.length; ++i) {
            sb.append(String.format("%02x", buf[i]));
        }
        return sb.toString();
    }
}
