package proj;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SchrwsK on 2016-12-26.
 */
public class Util {
    public static DefaultHttpClient httpclient;
    public static boolean login(String id, String passwd) {
        try {
            if (httpclient == null) httpclient = new DefaultHttpClient();
            HttpResponse response;
            HttpEntity entity;

            HttpPost httpost = new HttpPost("https://bis.sasa.hs.kr/lib/session.php");

            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("id", id));
            nvps.add(new BasicNameValuePair("passwd", passwd));
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.ISO_8859_1));
            response = httpclient.execute(httpost);
            entity = response.getEntity();
            System.out.println(response.getStatusLine());
            BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(entity.getContent()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null)
                result.append(line);
            rd.close();

            return result.toString().contains("info.php");
        } catch (Exception e) {return false;}
    }

    public static String loadFromWeb(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        //  httpget_goto.setEntity(new UrlEncodedFormEntity(nvps, HTTP.ISO_8859_1));
        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();
        BufferedReader rd = null;
        if (entity != null) rd = new BufferedReader(new java.io.InputStreamReader(entity.getContent()));
        if (rd == null) return null;
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = rd.readLine()) != null)
            result.append(line);
        rd.close();
        return result.toString();
    }
}
