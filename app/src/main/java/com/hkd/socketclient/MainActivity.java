package com.hkd.socketclient;

// Based on https://github.com/hkdsun/Android-Telnet-Client

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.*;

import java.io.IOException;

public class MainActivity extends Activity {

	private PioneerController client = null;
	private Toast fastToast;
	private  TextView et;
    private  TextView st;
	private  NumberPicker numpicker;
	private static int SERVERPORT = 23;
	private static String SERVER_IP = "192.168.0.105";
	private static final int MIN_VOL = 0;
	private static final int MAX_VOL = 80;	

	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		EditText etIp = (EditText) findViewById(R.id.EditTextIp);
		et = (TextView) findViewById(R.id.inputStreamTextView);
        st = (TextView) findViewById(R.id.statusStreamTextView);
		et.setMovementMethod(new ScrollingMovementMethod());

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
		
				
		fastToast = Toast.makeText(this,"", Toast.LENGTH_SHORT);
		
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		if(sharedPref.contains("last_server"))
			etIp.setText(sharedPref.getString("last_server", ""));
	}

	public void btnFwd(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");
			return;
		}
		client.fwd();
	}
	public void btnRew(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");
			return;
		}
		client.rew();
	}
	public void btnLeft(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");
			return;
		}
		client.left();
	}
	public void btnRight(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");
			return;
		}
		client.right();
	}
	public void btnStop(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");
			return;
		}
		client.stop();
	}




	public void onVolumeChange(View view){
		if(!client.isConnected()){
			toastFast("Not connected to a server");			
			return;
		}
		
		client.changeVolume(numpicker.getValue());
		//client.getVolume();
	}
	
	@Override
	protected void onStop() {
		if (client!=null && client.isConnected()) {
			if(disconnect()) toastFast("Disconnected from server");
			else toastFast("Error disconnecting from server");
		}		
		super.onStop();
	}

	
	void toastFast(String str) {
		
		fastToast.setText(str);
		fastToast.show();
	}

	private boolean etIsEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
	
	
	private boolean disconnect(){
		if(client.disconnect()){
			return true;
		}
		return false;
	}
	
	public void onClickConnect(View view){
		EditText etIp = (EditText) findViewById(R.id.EditTextIp);
		//EditText etPort = (EditText) findViewById(R.id.EditTextPort);
		
		if(!etIsEmpty(etIp)){	
			String tmp = etIp.getText().toString();
			
			if(tmp.contains(":")){
				String[] address = tmp.split(":");
				SERVER_IP = address[0];
				SERVERPORT = Integer.parseInt(address[1]);
			}
			else{
				SERVER_IP = etIp.getText().toString();
			}
			
			SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("last_server", tmp);
			editor.apply();
			

		}
		else 
			toastFast("Enter a server IP");
		
		
		
		if(client!=null && client.isConnected()) 
			toastFast("Already connected");
		else {
            new AsyncTask<MainActivity, Void, Void>() {
                @Override
                protected Void doInBackground(MainActivity... act) {
                    try {
                        client = new PioneerController(SERVER_IP, SERVERPORT, act[0]);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    //numpicker.setValue(client.getVolume());
                }
            }.execute(this);
        }
	}
	

	public void onClickDisconnect(View view){
		if (client!=null && client.isConnected()) {
			if(disconnect()){
				toastFast("Disconnected from server");
			}
			else toastFast("Error disconnecting from server");
		}
		else{
			toastFast("Already disconnected");
		}
	}
	
	public void onClickSend(View view) {
		if(client==null || !client.isConnected()){
			toastFast("Not connected to a server");			
			return;
		}
		
		EditText et = (EditText) findViewById(R.id.EditTextCommand);
				
		client.sendCommand(et.getText().toString());
		
	}

	
	public void onClickPlay(View view){
		client.play();
	}
	
	public void onClickStop(View view){
		client.stop();
	}

	public void onClickXbox(View view){
		client.input("Xbox");
	}

	public void onClickProjector(View view){
		client.input("Projector");
	}
	
	public void onClickRP(View view){
		client.input("Radio Paradise");
	}

	public void appendToConsole(String str){
        et.append("\n");
		et.append(str);
	}
	
	public void resetConsole(){
		et.setText("");
	}
	
	public void setConsole(String str){
		et.setText(str);
	}
	
	public String getConsole(){
		return et.getText().toString();
	}


    public void setVolume(int volume) {
        if(!numpicker.isDirty())
            numpicker.setValue(volume);
    }

    public void setStatus(String s) {
        st.setText(s);
    }
}