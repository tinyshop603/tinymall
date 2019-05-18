package com.attitude.tinymall;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

public class PaySSLSocketFactory extends SSLSocketFactory{
    static{
        mySSLSocketFactory = new PaySSLSocketFactory(createSContext());
    }

    private static PaySSLSocketFactory mySSLSocketFactory = null;



    private static SSLContext createSContext(){
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslcontext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return null;
        }
        return sslcontext;
    }

    private PaySSLSocketFactory(SSLContext sslContext) {
        super(sslContext);
        this.setHostnameVerifier(ALLOW_ALL_HOSTNAME_VERIFIER);
    }

    public static PaySSLSocketFactory getInstance(){
        if(mySSLSocketFactory != null){
            return mySSLSocketFactory;
        }else{
            return mySSLSocketFactory = new PaySSLSocketFactory(createSContext());
        }
    }
}
