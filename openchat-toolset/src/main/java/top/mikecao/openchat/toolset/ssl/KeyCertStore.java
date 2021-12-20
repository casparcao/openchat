package top.mikecao.openchat.toolset.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

/**
 * @author mike
 * 使用Thread Context Class Loader读取KeyStore文件
 */
public class KeyCertStore {
    private final KeyStore store;

    public KeyCertStore(String path, String storepass)
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {

        store = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        store.load(is, storepass.toCharArray());
    }

    public PrivateKey key(String alias, String keypass)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore.ProtectionParameter protection = null;
        if(Objects.nonNull(keypass)){
            protection =
                    new KeyStore.PasswordProtection(keypass.toCharArray());
        }
        KeyStore.PrivateKeyEntry pke
                = (KeyStore.PrivateKeyEntry) store.getEntry(alias, protection);
        return pke.getPrivateKey();
    }

    public X509Certificate certificate(String alias) throws KeyStoreException {
        return (X509Certificate) store.getCertificate(alias);
    }
}
