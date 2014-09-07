package com.doviztakipinternet;


import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String TAG="DovizTakip";
	private List<String> dovizOranList;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainn);
		
		dovizOranList=getDovizOranList();
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dovizOranList);
		ListView dovizOranListView=(ListView) findViewById(R.id.dovizOranListView);
		dovizOranListView.setAdapter(adapter);
		
		Button yenileButton=(Button) findViewById(R.id.yenileButton);
		
		yenileButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dovizOranList=getDovizOranList();
				adapter.notifyDataSetChanged();
				
			}
		});
	}

	private List<String> getDovizOranList() {
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		HttpURLConnection urlConnection=null;
		
		try{
			URL url=new URL(getResources().getString(R.string.doviz_takip_url));
			urlConnection=(HttpURLConnection) url.openConnection();
			
			int sonucKodu=urlConnection.getResponseCode();
			if(sonucKodu==HttpURLConnection.HTTP_OK){
				BufferedInputStream stream=new BufferedInputStream(urlConnection.getInputStream());
				return getDovizOranListFromInputStream(stream);
			}
		}catch(Exception e){
			Log.d(TAG, "HTTP baðlantýsý kurulurken hata oluþtu",e);
		}
		finally{
			if(urlConnection!=null)
				urlConnection.disconnect();
		}
		return new ArrayList<String>();
	}

	private List<String> getDovizOranListFromInputStream(
			BufferedInputStream stream) {
		
List<String> dovizOranList = new ArrayList<String>();
    	
    	if(stream == null)
    		return dovizOranList;
    	
    	try {
			
    		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse (stream);
			Element firstCube = (Element) document.getElementsByTagName("Cube").item(0);
			Element secondCube = (Element) firstCube.getElementsByTagName("Cube").item(0);
			
			NodeList dovizOranNodeList = secondCube.getElementsByTagName("Cube");
			
			for (int i = 0; i < dovizOranNodeList.getLength(); i++) {
				Element dovizOranElement = (Element) dovizOranNodeList.item(i);
				String paraBirimi = dovizOranElement.getAttribute("currency"); 
				String euroyaOrani = dovizOranElement.getAttribute("rate");
				
				dovizOranList.add(paraBirimi + " / € = " + euroyaOrani);
			}
		
		} catch (Exception e) {
			Log.d(TAG, "XML parse edilirken hata oluþtu", e);
		}
    	
    	return dovizOranList;
    }
		
	}


