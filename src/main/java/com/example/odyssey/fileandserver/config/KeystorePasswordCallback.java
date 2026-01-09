package com.example.odyssey.fileandserver.config;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class KeystorePasswordCallback implements CallbackHandler {

    private static String keystorePassword;

    public static void setKeystorePassword(String password) {
        keystorePassword = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback wsPasswordCallback) {
                wsPasswordCallback.setPassword(keystorePassword);
            }
        }
    }
}
