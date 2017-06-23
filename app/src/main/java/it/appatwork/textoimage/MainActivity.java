package it.appatwork.textoimage;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = "TEXT_TO_IMAGE"; //Debug Tag
	
	//Costanti per distinguere i dialog
	
	private static final int SAVE_TOSD_ID = 1;
	private static final int SAVE_TOGALLERY_ID = 2;
	
	//Codici di gestione delle activity risults
	
	private static final int PICTURE_FROM_GALLERY_ID = 1;
	private static final int LOAD_FROM_SD_ID = 2;
	
	
	private File storageDir;
	
	//Variabili per i riferimenti agli elementi grafici
	
	private ImageView imageView;
	private EditText editText;
	private EncDec encdec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (ImageView) findViewById(R.id.imageView);
		editText = (EditText) findViewById(R.id.editText1);
		
		/*Caricamento asincrono del logo*/
		
		loadBitmap( R.drawable.logo4, imageView);
		

		Button selectButton =
	               (Button) findViewById(R.id.selectButton);
		selectButton.setOnClickListener(selectButtonListener);
		
		
		Button encryptButton = 
	            (Button) findViewById(R.id.encryptButton);
		encryptButton.setOnClickListener(encryptButtonListener);
    
		Button decryptButton = 
	            (Button) findViewById(R.id.decryptButton);
		decryptButton.setOnClickListener(decryptButtonListener);
		
		if(isExternalStorageWritable())
			storageDir = getStorageDir("TextToimage");
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	// handle choice from options menu
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) 
	   {
	      // switch based on the MenuItem id
	      switch (item.getItemId()) 
	      {
	      
	      case  R.id.loadItem:
	    	  
	    	//load Image from SD card;
	      
	    	  Intent loadActivityIntent= 
	        	 	new Intent(MainActivity.this, ListActivity.class);
	        	 
	        	 startActivityForResult(loadActivityIntent, LOAD_FROM_SD_ID);
	        	 return true; //
	        	 
	      case  R.id.SaveSdItem:
	    	  showSaveDialog(SAVE_TOSD_ID);
	        
	      } // end switch
	      
	      return super.onOptionsItemSelected(item); // call super's method
	   } // end method onOptionsItemSelected

	   void showSaveDialog(final int requestCode)
		 {
			// get a reference to the LayoutInflater service
		      LayoutInflater inflater = (LayoutInflater) getSystemService(
		         Context.LAYOUT_INFLATER_SERVICE);

			// inflate slideshow_name_edittext.xml to create an EditText
		      View view = inflater.inflate(R.layout.save_name_edittext, null);
		      final EditText nameEditText = 
		         (EditText) view.findViewById(R.id.nameEditText);
		      
		      // create the dialog and inflate its content
		      AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
		      saveDialog.setView(view);
		      saveDialog.setTitle(R.string.title_save_dialog);
		      
		      saveDialog.setPositiveButton(R.string.save_to_dir, 
		    	         new DialogInterface.OnClickListener()
		    	         { 
		    	            public void onClick(DialogInterface dialog, int whichButton) 
		    	            {
		    	               // create a SlideshowInfo for a new slideshow
		    	               String name = nameEditText.getText().toString();
		    	               
		    	               if (name.length() != 0)
		    	               {
		    	            	   switch (requestCode) 
		    	         	      {
		    	         	      case SAVE_TOSD_ID:
		    	         	    	
		    	         	    	 saveImageToSdCard(name);	 
		    	         	        
		    	            	   break;
		    	            	   
		    	         	     case SAVE_TOGALLERY_ID:
			    	         	    	
		    	         	    	 //saveImage(name);	 
		    	         	        
		    	            	   break;
		    	            	   
		    	         	      }   
		    	               } // end if
		    	               else
		    	               {
		    	                  // display message that slideshow must have a name
		    	                  Toast message = Toast.makeText(getBaseContext(), 
		    	                     R.string.message_error_saving, Toast.LENGTH_SHORT);
		    	                  message.setGravity(Gravity.CENTER, 
		    	                     message.getXOffset() / 2, message.getYOffset() / 2);
		    	                  message.show(); // display the Toast
		    	               } // end else
		    	            } // end method onClick 
		    	         } // end anonymous inner class
		    	      ); // end call to setPositiveButton
		    	      
		    	      saveDialog.setNegativeButton(R.string.cancel_Button, null);
		    	      saveDialog.show();

		     
		     	   
		   }

	
	
	
	public void loadBitmap(int resId, ImageView imageView) {
	    BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	    task.execute(resId);
	}
	
	
	
	
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return decodeSampledBitmapFromResource(getResources(), data, 512, 512);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	            	
	            	encdec = new EncDec(bitmap);
	            	if (encdec.pic !=null) Log.i(TAG, "value: " + "OK");
	                imageView.setImageBitmap(bitmap);
	               
	            }
	        }
	    }
	}
	
	public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //ritorna le dimensioni dell'immagine senza caricarla in memoria
	    BitmapFactory.decodeResource(res, resId, options);
	    
	     

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    Log.i(TAG, "Sample size value: " + options.inSampleSize );
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    
	    
	    return BitmapFactory.decodeResource(res, resId, options);
	    
	}
	
	public  Bitmap decodeSampledBitmapFromFile(Uri selectedUri,
	        int reqWidth, int reqHeight) {
		
		Bitmap bitmap;

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //ritorna le dimensioni dell'immagine senza caricarla in memoria
	    encdec.getBitmap(selectedUri, getContentResolver(), options);
	   

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    
	    Log.i(TAG, "Sample size value: " + options.inSampleSize ); 
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    
	    bitmap = encdec.getBitmap(selectedUri, getContentResolver(), options);
	    
	    Log.i(TAG, "Sample height value: " + options.outHeight );
	    Log.i(TAG, "Sample width value: " + options.outWidth);

	    return bitmap;
	    
	}

	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    
    Log.i(TAG, "height value: " + height); 
    
    final int width = options.outWidth;
    
    Log.i(TAG, "width value: " + width); 
    
    int inSampleSize = 1;
    

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
	
	class EncryptWorkerTask extends AsyncTask<String, Void, Boolean> {
	    

	    public EncryptWorkerTask() {
	        
	    	
	    }

	    // Decode image in background.
	    @Override
	    protected Boolean doInBackground(String... params) {
	       
	        return encdec.encryptImage(params[0]);
	    }

	    // Once complete, a message will be showed.
	    @Override
	    protected void onPostExecute(Boolean error_message) {
	    	
	    	editText.setText("");
	    	//dataTextView.setText("Message hidden!");
	    	//display a message indicating that the image was saved
	    	Toast message_encrypted = Toast.makeText(getBaseContext(), 
	    	  "Message hidden!", Toast.LENGTH_SHORT);
	    	message_encrypted.setGravity(Gravity.CENTER, message_encrypted.getXOffset() / 2, 
	    	  message_encrypted.getYOffset() / 2);
	    	message_encrypted.show(); // display the Toast

	        
	            	
	    }
	}

 class DecryptWorkerTask extends AsyncTask<Void, Void, String> {
	    

	    public DecryptWorkerTask() {
	        
	    	
	    }

	    // Decode image in background.
	    @Override
	    protected String doInBackground(Void...params) {
	       
	        return encdec.decryptImage();
	    }

	    // Once complete, hidden message will be showed.
	    @Override
	    protected void onPostExecute(String message) {
	    
	    	//display hidden message
	    	Toast message_decrypted = Toast.makeText(getBaseContext(), 
	    	  message, Toast.LENGTH_SHORT);
	    	message_decrypted.setGravity(Gravity.CENTER, message_decrypted.getXOffset() / 2, 
	    	  message_decrypted.getYOffset() / 2);
	    	message_decrypted.show(); // display the Toast

	        
	            	
	    }
	}
 
  
 
//Load Image from gallery 
 
 public void loadImage()
 {
	//Load Image from gallery: it Work!!   
	   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
       intent.setType("image/*");
       startActivityForResult(Intent.createChooser(intent, 
       getResources().getText(R.string.chooser_image)), PICTURE_FROM_GALLERY_ID);
    

 
 
 }
 
 @Override
 protected final void onActivityResult(int requestCode, int resultCode, 
    Intent data)
 {
    if (resultCode == RESULT_OK) // if there was no error
    {
  	  if (requestCode == PICTURE_FROM_GALLERY_ID) // // Load file from gallery
  	  {    
  	  Uri selectedUri = data.getData(); 
  	  
  	  
       
       //l'immagine potrebbe essere da scalare
         
  	  encdec.pic = decodeSampledBitmapFromFile(selectedUri, 512 , 512);
      
         	
  	   imageView.setImageBitmap(encdec.pic);  
  	  }  
  	  
  	  if (requestCode == LOAD_FROM_SD_ID) // Load file from SD
  	  {
  		  Log.i(TAG, "value: " + "OK"); 
  		  String fileToLoad = data.getStringExtra("filetostore"); 
  		  Log.i(TAG, "value: " + storageDir + "/"+ fileToLoad);
  		  if (fileToLoad != null)
  		  encdec.pic = BitmapFactory.decodeFile(storageDir + "/"+fileToLoad, new BitmapFactory.Options());
  		  
  		  imageView.setImageBitmap(encdec.pic); 
  		  
  	  }

    }
    }


 private OnClickListener selectButtonListener = new OnClickListener() 
 {
    @Override
    public void onClick(View v) 
    {
  	  loadImage(); //load Image
  	  
    } // end method onClick
 }; // end selectButtonListener

	
	private OnClickListener encryptButtonListener = new OnClickListener() 
	   {
	      @Override
	      public void onClick(View v) 
	      {   
			  String message = editText.getText().toString();
			  EncryptWorkerTask task = new EncryptWorkerTask();
			    task.execute(message);
	    	  
	      } // end method onClick
	   }; // end encryptButtonListener
	   
	   private OnClickListener decryptButtonListener = new OnClickListener() 
	   {
	      @Override
	      public void onClick(View v) 
	      {   
			  String message = editText.getText().toString();
			  DecryptWorkerTask task = new DecryptWorkerTask();
			  task.execute();
	    	  
	      } // end method onClick
	   }; // end encryptButtonListener
	   
	   /* Checks if external storage is available for read and write */
		
		public boolean isExternalStorageWritable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    }
		    return false;
		}
		
		// Get the directory for the app's private pictures directory
		
		public File getStorageDir(String StorageName) {
			storageDir = new File(Environment.getExternalStorageDirectory(), StorageName);
			Log.i(TAG, "value: " + storageDir.getPath());
		    if (!storageDir.exists())
				    	if (!storageDir.mkdirs()) {
				        Log.e(TAG, "Directory not created");
				    }
				    return storageDir;
				}//end getStorageDir
		
		
		
		

		public void saveImageToSdCard(String imageName)
				{
					String fileName = imageName + "_mod"+".png";

			        File encryptedImage = new File(storageDir, fileName);
			        Log.i(TAG, "value: " + encryptedImage.getAbsolutePath());

			    
			    try 
			    {
			       
			       FileOutputStream outStream = new FileOutputStream(encryptedImage);
			       

			       
			       // copy the bitmap to the OutputStream
			       encdec.pic.compress(Bitmap.CompressFormat.PNG, 100, outStream);

			       // flush and close the OutputStream
			       outStream.flush(); // empty the buffer
			       outStream.close(); // close the stream

			       // display a message indicating that the image was saved
			       Toast message = Toast.makeText(getApplicationContext(), 
			          encryptedImage.getAbsolutePath(), Toast.LENGTH_SHORT);
			       message.show(); // display the Toast
			    } // end try
			    catch (IOException ex) 
			    {
			       // display a message indicating that the image was saved
			       Toast message = Toast.makeText(getBaseContext(), 
			          R.string.message_error_saving, Toast.LENGTH_SHORT);
			       message.setGravity(Gravity.CENTER, message.getXOffset() / 2, 
			          message.getYOffset() / 2);
			       message.show(); // display the Toast
			    } // end catch

				
				}
				
	
			    


}