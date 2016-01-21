package com.example.arthurlee.cfchome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arthurlee on 1/16/16.
 */
public class RSSDateParser
{
    DateFormat mDateFormat;
    Date mdate;

    public Date parse(String rssDate )
    {
        mDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

        try
        {
            mdate = mDateFormat.parse(rssDate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  mdate;
    }
}
