package proj;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class Util {
    public static HttpClient httpclient;
    public static boolean login(String id, String passwd) {
        try {
            if (httpclient == null) httpclient = getHttpClient();
            HttpResponse response;

            HttpPost httpost = new HttpPost("https://bis.sasa.hs.kr/lib/session.php");

            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("id", id));
            nvps.add(new BasicNameValuePair("passwd", passwd));
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.ISO_8859_1));
            response = httpclient.execute(httpost);
            System.out.println(response.getStatusLine());

            return getResult(response.getEntity()).contains("info.php");
        } catch (Exception e) {e.printStackTrace();return false;}
    }

    public static String loadFromWeb(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpclient.execute(httpget);
        return getResult(response.getEntity());
    }

    private static String getResult(HttpEntity entity) throws Exception {
        BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(entity.getContent()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null)
            result.append(line);
        rd.close();
        return result.toString();
    }

    private static HttpClient getHttpClient() { // http://blog.saltfactory.net/android/using-https-on-android.html
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SFSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}
