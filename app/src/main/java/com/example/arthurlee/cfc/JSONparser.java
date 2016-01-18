package com.example.arthurlee.cfc;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by arthurlee on 1/15/16.
 */
public class JSONparser
{

    final String TAG = "JsonParser.java";

    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jArray = null;
    static String json = "";

    public JSONObject getJSONFromUrl(String url) {

        // make HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;
    }

    public JSONArray getJSONArrayFromUrl(String url)
    {
        // make HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int linenumb = 0;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
                linenumb ++;
            }
            is.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
            Log.d("JSONParser", "lines of string parsed" + linenumb);
        }

        // try parse the string to a JSON Array
        try {
            jArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
            Log.d("JSONParser", "lines of string parsed" + linenumb);

        }

        // return JSON String
        return jArray;
    }

    /**
     * This function is different from the previous JSONObject parser because it cuts parts of the string
     * before parsing because there are some errors in the raw json file
     * @param url
     * @return
     */
    public JSONObject getJSONFromUrl2( String url )
    {
        // make HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int line_number = 0;
        int line_number2 = 0;

        /**
         * the following try catch gets the raw string input then correct for the JSON errors before
         * parsing the raw string into a JSON object, then changes the JSON object
         */
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();

            // add this to correct the JSON object
            sb.append( "{" + "\n" );
            sb.append("\"sermons\": [\n" );

            String line = null;
            while ((line = reader.readLine()) != null) {
                line_number ++;
                // the first 19 lines are not needed
                if( line_number > 20 )
                {
                    // remove "sermonx":{ and add {
                    if( line_number2 % 18 != 0)
                    {
                        sb.append(line + "\n");
                    }
                    else
                    {
                        sb.append("{\n");
                    }

                    line_number2 ++;
                }
            }
            is.close();

            // remove last 4 lines of string
            for( int i = 0; i < 5; i++ )
            {
                int last = sb.lastIndexOf("\n");
                if (last >= 0) { sb.delete(last, sb.length()); }
            }

            // add this at end
            sb.append( "}\n]\n}" );

            json = sb.toString();

        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
            Log.d("JSONParser", "lines of string parsed" + line_number);
        }


        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            //Log.e(TAG, "Error parsing data " + e.toString());
            Log.e(TAG, "Error parsing data");

            int length = json.length();

            for(int i=0; i<length; i+=1024)
            {
                if(i+1024<length)
                    Log.d("JSON OUTPUT", json.substring(i, i+1024));
                else
                    Log.d("JSON OUTPUT", json.substring(i, length));
            }
        }

        // return JSON String
        return jObj;
    }
}

