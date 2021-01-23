package com.driverUSB;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.driverUSB.utils.DataBufTransformer;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.qs5501.demo.settings.test.TestCustomDriverActivity;

import java.io.IOException;
import java.util.ArrayList;

public class USBDriverHandler implements SerialInputOutputManager.Listener{

    //const
    private static final int PRODUCT_ID = 8963;
    private static final int VENDOR_ID = 1659;

    private int[] buffer;

    public int[] getBuffer() {
        return buffer;
    }

    private UsbManager usbManager;
    private UsbSerialPort usbSerialPort;
    private UsbSerialDriver usbSerialDriver;
    private UsbDeviceConnection usbDeviceConnection;

    private Handler mainLooper;
    private boolean connected = false;
    private Context context;

    public USBDriverHandler(Context context) {
        this.context = context;
        mainLooper = new Handler(Looper.getMainLooper());
    }

    public String openPort(Context context){
        usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return "Critical ERROR: usbManager = null";
        }
        if (usbManager.getDeviceList().values().isEmpty()){
            return "Critical ERROR: deviceList empty";
        }
        for(UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getProductId() == PRODUCT_ID && device.getVendorId() == VENDOR_ID){
                usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(device);
            }
        }
        if(usbSerialDriver == null) {
            return "Critical ERROR: device has no driver";
        }
        if(usbSerialDriver.getPorts().size() == 1){
            usbSerialPort = usbSerialDriver.getPorts().get(0);
            usbDeviceConnection = usbManager.openDevice(usbSerialDriver.getDevice());
            try {
                usbSerialPort.open(usbDeviceConnection);
                usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                connected = true;
                return "success";
            } catch (Exception e) {
                closePort();
                return "Critical ERROR: " + e.getMessage();
            }
        }else {
            return "Critical ERROR: the driver has more than one port";
        }
    }

    public void closePort(){
        connected = false;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }

    public String read(int lengthBuffer){
        if(!connected) {
            return "read() not connected";
        }
        try {
            byte[] waitingBuf = new byte[lengthBuffer];
            usbSerialPort.read(waitingBuf, 2000);
            buffer = DataBufTransformer.transformBufByteToBufInt(waitingBuf);
            return "success";
        } catch (IOException e) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            closePort();
            return "read() Critical ERROR: " + e.getMessage();
        }
    }

    public String send(final int[] data) {
        if(!connected) {
            return "read() not connected";
        }
        try {
            byte[] byteData = DataBufTransformer.transformBufIntToBufByte(data);
            usbSerialPort.write(byteData, 2000);
            return "success";
        } catch (Exception e) {
            closePort();
            return "send() Critical ERROR: " + e.getMessage();
        }
    }

    public byte readPort(byte[] Buffer, int expectedQuantityByte, int takenQuantityByte){
        return 0;
    }

    public byte writePort(byte[] Buffer, int expectedQuantityByte, int takenQuantityByte){
        return 0;
    }

    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
            Toast.makeText(context, "onNewData()", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRunError(Exception e) {
        mainLooper.post(() -> {
            Toast.makeText(context, "onRunError()", Toast.LENGTH_SHORT).show();
        });
    }
}
