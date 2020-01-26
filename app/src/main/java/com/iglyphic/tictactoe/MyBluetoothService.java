package com.iglyphic.tictactoe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class MyBluetoothService {
	
	private static final UUID MY_UUID =UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	private static final String MY_NAME="Tic_Tac_Toe";
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_FAILED = 6;
    public static final int MESSAGE_CONNECTED = 7;
	public static final int MESSAGE_DISCONNECTED = 8;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	private final BluetoothAdapter mBluetoothAdapter;
    private static Handler fHandler;
    private static AcceptThread mAcceptThread;
    private static ConnectThread mConnectThread;
    private static ConnectedThread mConnectedThread;
    
    public static boolean acceptStatus=false,connectStatus=false,connectedStatus=false;
    public static boolean server=false;
    
   // public static BluetoothSocket connectedSocket;
    
    
    
    
    
    
    public MyBluetoothService()
    {
    	mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    
    public void setFHandler(Handler handler)
    {
    	fHandler=handler;
    }
    
    
  /*  public void setSHandler(Handler handler)
    {
    	sHandler=handler;
    }  */
    
    
    
    public void startAcceptThread()
    {
    	if(acceptStatus==false)
    	{
    		mAcceptThread=new AcceptThread();
    		mAcceptThread.start();
    		acceptStatus=true;
    	}
    }
    
    
    public void startConnectThread(BluetoothDevice device)
    {
    	if(connectStatus==false)
    	{
    		mConnectThread=new ConnectThread(device);
    		mConnectThread.start();
    		//fHandler.obtainMessage(MESSAGE_CONNECTING);
    		connectStatus=true;
    	}
    }
    
    
    public void startConnectedThread(BluetoothSocket socket)
    {
    	if(connectedStatus==false)
    	{
    		mConnectedThread=new ConnectedThread(socket);
    		mConnectedThread.start();
    		Message msg = fHandler.obtainMessage(MESSAGE_CONNECTED);
    		Bundle bundle = new Bundle();
            bundle.putString(TOAST, "Connection Successful");
            msg.setData(bundle);
            fHandler.sendMessage(msg);
            connectedStatus=true;
    		//Toast.makeText(appContext, "Connection successfull", Toast.LENGTH_LONG).show();
    	}
    }
    
    
    public void stopAcceptThread()
    {
    	if(acceptStatus==true)
    	{
			try {
				mAcceptThread.cancel();
				mAcceptThread=null;
			} catch (Exception e) {
				acceptStatus = false;
			}
    	}
    }
    
    
    public void stopConnectThread()
    {
    	if(connectStatus==true)
    	{
			try {
				mConnectThread.cancel();
				mConnectThread=null;
			} catch (Exception e) {
				connectStatus = false;
			}
    	}
    }
    
    
    public void stopConnectedThread()
    {
    	if(connectedStatus==true)
    	{
			try {
				mConnectedThread.cancel();
				mConnectedThread=null;
			} catch (Exception e) {
				connectedStatus = false;
			}
    	}
    }
    
    
    public void sendMessage(String message)
    {
    	byte[] b = message.getBytes();
    	mConnectedThread.write(b);
    }
    
    
    
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = fHandler.obtainMessage(MESSAGE_CONNECTION_FAILED);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        fHandler.sendMessage(msg);  
    //	Toast.makeText(appContext, "Connection failed", Toast.LENGTH_LONG).show();

        // Start the service over to restart listening mode
       // BluetoothChatService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = fHandler.obtainMessage(MESSAGE_DISCONNECTED);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        fHandler.sendMessage(msg);  
    //	Toast.makeText(appContext, "Connection lost", Toast.LENGTH_LONG).show();

        // Start the service over to restart listening mode
       // BluetoothChatService.this.start();
    }
    
    
    
	private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(MY_NAME, MY_UUID);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	                socket = mmServerSocket.accept();
	            } catch (IOException e) {
	            	//Toast.makeText(getApplicationContext(), "Accept Exception", Toast.LENGTH_LONG).show();
	                break;
	            }
	            // If a connection was accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	            //    manageConnectedSocket(socket);
	            	//connectedSocket=socket;
	            	server=true;
	            	startConnectedThread(socket);
	                try {
						mmServerSocket.close();
						acceptStatus=false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//Toast.makeText(getApplicationContext(), "Close Exception", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
	              //  Toast.makeText(getApplicationContext(), "You are now connected", Toast.LENGTH_LONG).show();
	               // startBluetoothGame(socket);
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            mmServerSocket.close();
	            acceptStatus=false;
	        } catch (IOException e) { }
	    }
	}
	
	
	
	
	
	
	private class ConnectThread extends Thread {
	    private BluetoothSocket mmSocket=null;
	    private BluetoothDevice mmDevice=null;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	       if(tmp!=null) mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	      //  mBluetoothAdapter.cancelDiscovery();
	 
	    	if(mmSocket!=null)
	    	{
	    		try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		        //	Toast.makeText(getApplicationContext(), "Connection Exception", Toast.LENGTH_LONG).show();
		        //	Toast.makeText(getApplicationContext(), "Can't Connect", Toast.LENGTH_LONG).show();
		            try {
		                mmSocket.close();
		                connectStatus=false;
		            } catch (IOException closeException) { 
		            //	Toast.makeText(getApplicationContext(), "Close Exception", Toast.LENGTH_LONG).show();
		            }
		            connectionFailed();
		            return;
		        }
		       // acceptThread.cancel();
	           // startBluetoothGame(mmSocket);
	            
		 
		        // Do work to manage the connection (in a separate thread)
		       // manageConnectedSocket(mmSocket);
		        
	    		//connectedSocket=mmSocket;
	    		server=false;
		        startConnectedThread(mmSocket);
	    	}
	    	else
	    	{
	    		connectStatus=false;
	    		connectionFailed();
	    	}
	        
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	            connectStatus=false;
	        } catch (IOException e) { }
	    }
	}
	
	
	
	
	
	
	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                fHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
	            } catch (IOException e) {
					connectionLost();
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	            connectedStatus=false;
	        } catch (IOException e) { }
	    }
	}
	
    
    
    

}
