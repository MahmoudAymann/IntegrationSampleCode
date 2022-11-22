/*
 * Copyright (c) 2019 e-finance
 *
 * Unless required by applicable law or agreed to in writing, this software
 * should not be distributed without permission or authorization guaranteed by the owner entity.
 *
 *
 */

package com.efinance.mobilepaymentintegrationsamplecode.Main.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

public class CryptoHelp {


    /**
     * To use your PFX private key for the signing procedure use the following steps, make sure openssl is installed on your device
     *
     *
     * Export PFX to PKCS12 Key
     *
     *
     * openssl pkcs12 -in YourKeyName.pfx -out InternetPayment.pem
     *
     * openssl pkcs12 -export -in InternetPayment.pem -out InternetPaymentKey.p12 -name "InternetPayment"
     *
     *
     * Add the generated key file to the assets folder of your application
     */



    // Signing


    /**
     * The method which handled the data sign procedure
     *
     * @param data of type String - the data to be signed
     * @param context of type Context - context of your application
     */

    public static String signData(String data, Context context) throws Exception {

        Signature signature = Signature.getInstance("SHA1withRSA");
        PrivateKey privateKey = getPrivateKey(context);

        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signature.sign();

        return new String(Base64.encode(signatureBytes, Base64.NO_WRAP));
    }

    private static PrivateKey getPrivateKey(Context context) throws Exception {

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("InternetPaymentKey.p12");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(inputStream, "1234".toCharArray());
        KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection("1234".toCharArray());
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("InternetPayment", passwordProtection);

        PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        return privateKey;
    }
}
