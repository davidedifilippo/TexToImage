package it.appatwork.textoimage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


public class EncDec {
	
	 private static final String TAG = "EncDec"; //Debug Tag
	   
	 public Bitmap pic;
	 private boolean error_message = false;
	
	 // EncDec constructor
	 
	 public EncDec(Bitmap picture)
	   {
	      
		pic = picture;
		
	   } // end EncDec constructor
	
	public boolean encryptImage(String message)
	 {   
		  
		 
		  
		 
	   pic = pic.copy(Bitmap.Config.ARGB_8888, true);

	   int L,ii;
       String LSB,controllo,BitLunghezza,BitMessaggioRicostruito;
       int lettera,Nb,Lunghezza,NumeroCaratteri;
       String lettera_stringa_binaria,BitCarattere;
       String stringa_normalizzata;
       int Len_Stringa;
       String Control_Message = "101010101010101010101010";
       String RecoveredMessage;
       

       String messaggio = message;
       messaggio  = messaggio +" ";
       String Binary_Message = "";
       
       L=messaggio.length();
       
       for (ii=0;ii<L;ii++)
       {
         lettera = messaggio.charAt(ii);
         lettera_stringa_binaria = Integer.toBinaryString(lettera);
         Len_Stringa = lettera_stringa_binaria.length();
         
         stringa_normalizzata = "";
         switch (Len_Stringa) {
           case 1: stringa_normalizzata="0000000"+lettera_stringa_binaria;
                    break;
           case 2:  stringa_normalizzata="000000"+lettera_stringa_binaria;
                    break;
           case 3:  stringa_normalizzata="00000"+lettera_stringa_binaria;
                    break;
           case 4:  stringa_normalizzata="0000"+lettera_stringa_binaria;
                    break;
           case 5:  stringa_normalizzata="000"+lettera_stringa_binaria;
                    break;
           case 6:  stringa_normalizzata="00"+lettera_stringa_binaria;
                    break;
           case 7:  stringa_normalizzata="0"+lettera_stringa_binaria;
                    break;
           case 8:  stringa_normalizzata=lettera_stringa_binaria;
                    break;

       }
       
       Binary_Message = Binary_Message+stringa_normalizzata;
       }

       
       Nb = Binary_Message.length();
       

       String stringa;
       int Lunghezza_Numero_Binario;
       int Lunghezza_Fissa = 24;
       String Stringa_Fissa = "";

       stringa = Integer.toBinaryString(Nb);
       
       Lunghezza_Numero_Binario = stringa.length();
       
       Stringa_Fissa = stringa;
       for (ii=Lunghezza_Numero_Binario+1;ii<=Lunghezza_Fissa;ii++)
       {
        Stringa_Fissa = "0"+Stringa_Fissa;
       }
       
       

       String Stringa_Totale =  Control_Message+Stringa_Fissa+Binary_Message;
       

//----------------------------------------------------------
//Modifica della squenza numerica utilizzando i bit 
//----------------------------------------------------------
int[] Vettore,Vettore_Modificato;
int bit_da_aggiungere,scan_bit;
int k;
int Numero;
int errore=0;
int ImgX,ImgY;
int pixel,r,g,b;
int cont;

ImgX = pic.getWidth();
ImgY = pic.getHeight();

Vettore = new int[ImgX*ImgY*3];
cont = 0;
for(int i=0; i<pic.getWidth() ;i++)
{
for(int j=0; j<pic.getHeight(); j++)
{
  pixel = pic.getPixel(i,j);
  
  r = (pixel>>16) & 0xff;
  
  g = (pixel>>8) & 0xff;
 
  b = (pixel) & 0xff;
  
  Vettore[cont] = r;
  cont++;
  Vettore[cont] = g;
  cont++;
  Vettore[cont] = b;
  cont++;
}
}
			 
				




Vettore_Modificato = new int[Vettore.length];
for (k=0;k<Vettore.length;k++)
{
Vettore_Modificato[k] = Vettore[k];
}

String SequezaBit = Stringa_Totale;
bit_da_aggiungere = SequezaBit.length();
scan_bit    = 0;

for (k=0;k<Vettore.length;k++)
{
Numero = Vettore[k];
stringa = Integer.toBinaryString(Numero);
Stringa_Fissa = stringa;
Lunghezza_Numero_Binario = stringa .length();
Lunghezza_Fissa = 8;
for (ii=Lunghezza_Numero_Binario+1;ii<=Lunghezza_Fissa;ii++)
{
 Stringa_Fissa = "0"+Stringa_Fissa;
}

char[] vettore_binario = Stringa_Fissa.toCharArray();

vettore_binario[5] = SequezaBit.charAt(scan_bit);
scan_bit++;
if (scan_bit>=bit_da_aggiungere)
{
 break;
}

vettore_binario[6] = SequezaBit.charAt(scan_bit);
scan_bit++;
if (scan_bit>=bit_da_aggiungere)
{
 break;
}

vettore_binario[7] = SequezaBit.charAt(scan_bit);
scan_bit++;
if (scan_bit>=bit_da_aggiungere)
{
 break;
}
 
String stringa_modificata = new String(vettore_binario);



Vettore_Modificato[k] = Integer.parseInt(stringa_modificata,2);


}
if (scan_bit<bit_da_aggiungere)
{
 error_message=true;
}



cont = 0;
for(int i=0; i<pic.getWidth() ;i++)
{
for(int j=0; j<pic.getHeight(); j++)
{
  
  r = Vettore_Modificato[cont];
  cont++;
  g = Vettore_Modificato[cont];
  cont++;
  b = Vettore_Modificato[cont];
  cont++;

  pixel = (0xff<<24)+(r<<16)+(g<<8)+(b); 
  pic.setPixel(i, j,pixel);  
  
}
}



Log.i(TAG, "value: " + message); 

return error_message;

//Da implementare in modo diverso

/*editText.setText("");
//dataTextView.setText("Message hidden!");
//display a message indicating that the image was saved
Toast message_encrypted = Toast.makeText(getBaseContext(), 
  "Message hidden!", Toast.LENGTH_SHORT);
message_encrypted.setGravity(Gravity.CENTER, message_encrypted.getXOffset() / 2, 
  message_encrypted.getYOffset() / 2);
message_encrypted.show(); // display the Toast
*/

 //imageView.setImageBitmap(pic);    //old version

 
	 }
	 
	 
	 
	 //Data estraction
	 
	 public String decryptImage()
	 { 


		 String mess="No text has been detected";


		 try
		 {		  



			 int L,ii;
			 String LSB,controllo,BitLunghezza,BitMessaggioRicostruito;
			 int lettera,Nb,Lunghezza,NumeroCaratteri;
			 int Lunghezza_Numero_Binario;
			 String lettera_stringa_binaria,BitCarattere;
			 String stringa_normalizzata;
			 int Len_Stringa;
			 String Control_Message = "101010101010101010101010";
			 String RecoveredMessage="";
			 String stringa;

			 String Stringa_Fissa;
			 int Lunghezza_Fissa;



			 int[] Vettore,Vettore_Modificato;
			 int bit_da_aggiungere,scan_bit;
			 int k;
			 int Numero;
			 int errore=0;







			 //----------------------------------------------------------
			 // Ricostruzione del messaggio dal vettore modificato 
			 //----------------------------------------------------------
			 int ImgX,ImgY;
			 int pixel,r,g,b;
			 int cont;

			 ImgX = pic.getWidth();
			 ImgY = pic.getHeight();

			 Vettore = new int[ImgX*ImgY*3];
			 cont = 0;



			 for(int i=0; i<pic.getWidth() ;i++)
			 {
				 for(int j=0; j<pic.getHeight(); j++)
				 {
					 pixel = pic.getPixel(i,j);

					 r = (pixel>>16) & 0xff;

					 g = (pixel>>8) & 0xff;

					 b = (pixel) & 0xff;

					 Vettore[cont] = r;
					 cont++;
					 Vettore[cont] = g;
					 cont++;
					 Vettore[cont] = b;
					 cont++;
				 }
			 }


			 controllo  = "";
			 for (k=0;k<8;k++)
			 {
				 Numero = Vettore[k];
				 stringa = Integer.toBinaryString(Numero);
				 Stringa_Fissa = stringa;
				 Lunghezza_Numero_Binario = stringa.length();
				 Lunghezza_Fissa = 8;

				 for (ii=Lunghezza_Numero_Binario+1;ii<=Lunghezza_Fissa;ii++)
				 {
					 Stringa_Fissa = "0"+Stringa_Fissa;
				 } 
				 LSB = Stringa_Fissa.substring(5,8);
				 controllo = controllo+LSB;
			 } 


			 BitLunghezza = "";
			 for (k=8;k<16;k++)
			 {
				 Numero = Vettore[k];
				 stringa = Integer.toBinaryString(Numero);
				 Stringa_Fissa = stringa;
				 Lunghezza_Numero_Binario = stringa .length();
				 Lunghezza_Fissa = 8;
				 for (ii=Lunghezza_Numero_Binario+1;ii<=Lunghezza_Fissa;ii++)
				 {
					 Stringa_Fissa = "0"+Stringa_Fissa;
				 } 
				 LSB = Stringa_Fissa.substring(5,8);
				 BitLunghezza = BitLunghezza+LSB;
			 } 




			 if (controllo.equals(Control_Message))
			 {

				 errore = 0;
			 }
			 else
			 {

				 errore = 1;
			 }
			 if (errore==0)
			 {
				 //BitLunghezza = Stringa_Totale.substring(24,48);
				 Lunghezza = Integer.parseInt(BitLunghezza ,2);
				 System.out.println("Lunghezza: "+Lunghezza ); 
				 System.out.println("Elementi da leggere: "+(Lunghezza+Lunghezza%3)/3*8); 



				 BitMessaggioRicostruito = "";
				 k=16;
				 while (BitMessaggioRicostruito.length()<Lunghezza)
				 {
					 Numero = Vettore[k];
					 stringa = Integer.toBinaryString(Numero);
					 Stringa_Fissa = stringa;
					 Lunghezza_Numero_Binario = stringa .length();
					 Lunghezza_Fissa = 8;
					 for (ii=Lunghezza_Numero_Binario+1;ii<=Lunghezza_Fissa;ii++)
					 {
						 Stringa_Fissa = "0"+Stringa_Fissa;
					 } 
					 LSB = Stringa_Fissa.substring(5,8);
					 BitMessaggioRicostruito = BitMessaggioRicostruito+LSB;
					 System.out.println("Parziale - Lunghezza BitMessaggioRicostruito : "+BitMessaggioRicostruito.length()); 
					 k=k+1;
				 }  




				 NumeroCaratteri = (BitMessaggioRicostruito.length())/8;
				 RecoveredMessage = "";
				 for (ii=0;ii<BitMessaggioRicostruito.length()-7;ii=ii+8)
				 {
					 BitCarattere = BitMessaggioRicostruito.substring(ii,ii+8);

					 k = Integer.parseInt(BitCarattere,2);

					 RecoveredMessage = RecoveredMessage+(char)k;
					 System.out.println("Carattere : "+(char)k); 
				 }
				 RecoveredMessage = RecoveredMessage.substring(0,RecoveredMessage.length()-1);



				 mess = RecoveredMessage;
				 Log.i(TAG, "value ok: " + mess);


				 if (errore==1)
				 {
					 error_message=true;
					 mess="No text has been detected";
					 Log.i(TAG, "try: " + mess);

				 }


			 }

		 }

		 catch (Exception exc)
		 {
			 error_message=true;
			 mess="No text has been detected";
			 Log.i(TAG, "catch: " + mess);
		 }

		 Log.i(TAG, "return value : " + mess);

		 return mess;
		 } 
	 
	 
	 
	 public Bitmap getBitmap(Uri uri, ContentResolver cr, 
	         BitmapFactory.Options options)
	      {
	         
	         
	         // get the image
	         try
	         {
	            InputStream input = cr.openInputStream(uri);
	            pic = BitmapFactory.decodeStream(input, null, options);  
	            
	            /*Toast message_encrypted = Toast.makeText(getBaseContext(), 
	            		uri.toString(), Toast.LENGTH_SHORT);
	            		message_encrypted.setGravity(Gravity.CENTER, message_encrypted.getXOffset() / 2, 
	            		   message_encrypted.getYOffset() / 2);
	            		message_encrypted.show(); // display the Toast
	    		*/
	         } // end try
	         catch (FileNotFoundException e) 
	         {
	            Log.v(TAG, e.toString());
	         } // end catch
	         
	         return pic;
	      } // end method getBitmap
	 



}
