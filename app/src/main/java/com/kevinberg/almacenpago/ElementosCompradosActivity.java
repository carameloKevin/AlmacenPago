package com.kevinberg.almacenpago;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ElementosCompradosActivity extends ListActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementos_comprados);

        TextView textView = new TextView(this);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setText("Items Comprados");

        ListView listView = (ListView) findViewById(android.R.id.list);
        CompradosListAdapater adapter = new CompradosListAdapater(this,);
        listView.setAdapter(adapter);

        sharedPreferences = getApplicationContext().getSharedPreferences("userdetails", 0);
        String userEmail = sharedPreferences.getString("email", "wrongEmail");

        AlmacenPagoDatabaseHelper almacenPagoDatabaseHelper = new AlmacenPagoDatabaseHelper(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                Toast.makeText(ElementosCompradosActivity.this, "Esta es la pos "+ position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}