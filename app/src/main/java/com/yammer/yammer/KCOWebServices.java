package com.yammer.yammer;

/**
 * Created by Cockponcher.
 */
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class KCOWebServices {


    public static JSONArray SendHttpGetArray(String URL, String token)
    {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            // Pone un tiempo de espera hasta que se establece conexion.
            int timeoutConnection = 7000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Establece el tiempo de espera en el socket
            //tiempo de espera los datos en  milisegundos .
            int timeoutSocket = 7000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpGet httpPostRequest = new HttpGet(URL);

            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("Content-type", "application/json");
            if(!token.isEmpty())
            {
                httpPostRequest.setHeader("Authorization", "Bearer " + token);
            }

            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);



            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Lee la secuencia de contenido
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                // convierte el contenido a una cadena
                String resultString= convertStreamToString(instream);
                instream.close();

                // Transforma la cadena a un objeto JSON
                JSONArray jsonObjRecv = new JSONArray(resultString);

                return jsonObjRecv;
            }
        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            System.gc();
        }
        return null;
    }

    public static JSONArray SendHttpPostArray(String URL, JSONObject jsonObjSend, String token)
    {
        try {
            HttpParams httpParameters = new BasicHttpParams();

            int timeoutConnection = 7000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            int timeoutSocket = 7000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString(), "UTF-8");

            // Pone los parametros HTTP.
            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("Content-type", "application/json");
            if(!token.isEmpty())
            {
                httpPostRequest.setHeader("Authorization", "Bearer " + token);
            }

            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);

            // Mantiene un control de respuesta en base a los datos.
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Lee la secuencia de contenido
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                // convierte el contenido a una cadena
                String resultString= convertStreamToString(instream);
                instream.close();

                // Transforma la cadena a un objeto JSON
                JSONArray jsonObjRecv = new JSONArray(resultString);

                return jsonObjRecv;
            }
        }
        catch (Exception e)
        {

        }
        catch (OutOfMemoryError e)
        {
            System.gc();
        }
        return null;
    }

    public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend, String token) {
        try {
            HttpParams httpParameters = new BasicHttpParams();

            int timeoutConnection = 7000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

            int timeoutSocket = 7000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;


            se = new StringEntity(jsonObjSend.toString(), "UTF-8");
            // Parametros HTTP
            httpPostRequest.setEntity(se);
            if(!token.isEmpty())
            {
                httpPostRequest.setHeader("Authorization", "Bearer " + token);
            }
            httpPostRequest.setHeader("Content-type", "application/json");
            httpPostRequest.setHeader("Accept", "application/json");

            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);


            //controla la respuesta en base a los datos.
            HttpEntity entity = response.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }


                String resultString= convertStreamToString(instream);
                instream.close();


                if(resultString.substring(0, 1).contains("["))
                    resultString = resultString.substring(1,resultString.length()-1);

                JSONObject jsonObjRecv = new JSONObject(resultString);


                return jsonObjRecv;
            }

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            System.gc();
        }
        return null;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}