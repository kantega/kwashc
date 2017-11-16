import javax.net.ssl.SSLServerSocketFactory;
import java.util.Map;
import java.util.TreeMap;

public class Ciphers
{
    public static void main(String[] args)
            throws Exception
    {
        SSLServerSocketFactory ssf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

        String[] defaultCiphers = ssf.getDefaultCipherSuites();
        String[] availableCiphers = ssf.getSupportedCipherSuites();

        TreeMap<String, Boolean> ciphers = new TreeMap<>();

        for (String availableCipher : availableCiphers) ciphers.put(availableCipher, Boolean.FALSE);

        for (String defaultCipher : defaultCiphers) ciphers.put(defaultCipher, Boolean.TRUE);

        System.out.println("Default\tCipher");
        for (Map.Entry<String, Boolean> cipher : ciphers.entrySet()) {
            if (Boolean.TRUE.equals(cipher.getValue()))
                System.out.print('*');
            else
                System.out.print(' ');

            System.out.print('\t');
            System.out.println(cipher.getKey());
        }
    }
}