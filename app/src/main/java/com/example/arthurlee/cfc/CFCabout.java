package com.example.arthurlee.cfc;


import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by arthurlee on 7/3/15.
 */
public class CFCabout extends Fragment
{
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private Button mWebsiteButton;

    public static CFCabout newInstance(int page) {
        //Bundle args = new Bundle();
        //args.putInt(ARG_PAGE, page);
        CFCabout fragment = new CFCabout();
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mPage = getArguments().getInt(ARG_PAGE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        mWebsiteButton = (Button)view.findViewById( R.id.about_website_button );
        mWebsiteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.cfchome.org"));
                startActivity(browserIntent);
            }
        });

        return view;
    }
}
