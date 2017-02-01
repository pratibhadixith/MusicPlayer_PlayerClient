package com.example.prati.playerclient;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by prati on 11/28/2016.
 */

public class ViewTransactions extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

           //set listAdapter and display the list
        ArrayList<String> ActivityList = (ArrayList<String>) getIntent().getSerializableExtra("list");
        setListAdapter(new ArrayAdapter<String>(this, R.layout.listelement, ActivityList));
    }

}