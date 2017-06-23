package it.appatwork.textoimage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;





import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends Activity {
	
	private static final String TAG = "TextToImage";
	private String[] values;
	private int filePosition;
	private File storageDir;
	private FileshowAdapter fsa; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		File file = new File(Environment.getExternalStorageDirectory(), "TextToimage"); // log files in Textoimage
		Log.i(TAG, "Listing Files in " + file.getAbsolutePath());
		storageDir =  file;
		values = file.list();
		
		ListView listView = (ListView) findViewById(R.id.mylist);
		
		
		fsa = new FileshowAdapter(this, values);
		
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		  R.layout.file_list_item, R.id.fileTextView, values);


		// Assign adapter to ListView
		listView.setAdapter(fsa); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {Log.i(TAG, "value: " + "click");
				  if( values !=null)
					{Log.i(TAG, "value: " + "toast");
					  Toast message = Toast.makeText(getApplicationContext(), 
							  "File selected: " + values[position], Toast.LENGTH_LONG);
					         
						    
					  message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
					            message.getYOffset() / 2);
					  message.show();
					  
			          filePosition = position;
						   
						
					} 
				  
				  finish();
				  
			    
			  }
			}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}
	
	@Override
	public void finish() {
		
		Intent retIntent = new Intent();
		if( values.length != 0)
		{retIntent.putExtra("filetoload", values[filePosition]);
		Log.i(TAG, "value: " + "test_finish");}
		   setResult(RESULT_OK, retIntent);
		   super.finish();
	}
	
	// Class for implementing the "ViewHolder pattern"
	   // for better ListView performance
		   private static class ViewHolder
		   {
		      TextView nameTextView; // refers to ListView item's TextView
		      ImageView imageView; // refers to ListView item's ImageView
		      
		   } // end class ViewHolder
	
	
	// ArrayAdapter subclass that displays a slideshow's name, first image
	   // and "Play", "Edit" and "Delete" Buttons
	   private class FileshowAdapter extends ArrayAdapter<String>
	   {
	      private String[] items;
	      private LayoutInflater inflater;

	      // public constructor for SlideshowAdapter
	      public FileshowAdapter(Context context, String[] items)
	      {
	         // call super constructor
	         super(context, -1, items);
	         this.items = items;
	         inflater = (LayoutInflater) 
	            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      } // end SlideshowAdapter constructor

	      // returns the View to display at the given position
	      @Override
	      public View getView(int position, View convertView, 
	         ViewGroup parent)
	      {
	         ViewHolder viewHolder; // holds references to current item's GUI

	         // if convertView is null, inflate GUI and create ViewHolder;
	         // otherwise, get existing ViewHolder
	         if (convertView == null) 
	         {
	            convertView = 
	               inflater.inflate(R.layout.file_list_item, null);

	            // set up ViewHolder for this ListView item
	            viewHolder = new ViewHolder();
	            viewHolder.nameTextView = (TextView) 
	               convertView.findViewById(R.id.fileTextView);
	            viewHolder.imageView = (ImageView) 
	               convertView.findViewById(R.id.fileImageView);
	           } // end if
	         
	         else // get the ViewHolder from the convertView's tag
	            viewHolder = (ViewHolder) convertView.getTag();

	         // get the slideshow the display its name in nameTextView
	         
	         viewHolder.nameTextView.setText(values[position]);
	         
	         convertView.setTag(viewHolder);

	         // if there is at least one image 
	         if (values.length > 0)
	         {
	            // create a bitmap 
	            Uri uri = Uri.parse(values[position]);
	        	 //int id = Integer.parseInt(uri.getLastPathSegment());
	            Log.i(TAG, "value: " + uri);
	            

	        	 Bitmap result = BitmapFactory.decodeFile(storageDir + "/"+ uri, new BitmapFactory.Options());
	        	 viewHolder.imageView.setImageBitmap(result);
	        	 
		         
	            
	 	         
	         } // end if

	        	         return convertView; // return the View for this position
	      } // end getView
	   } // end class SlideshowAdapter   

		   
	
	
	
}


	