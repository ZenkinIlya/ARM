package com.qs5501.demo.settings.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.driverUSB.utils.HexDump;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.BuildConfig;
import com.qs5501demo.aidl.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TestActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    private TextView textViewDevice, textRequestAnswer;

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    private UsbManager usbManager;
    private BroadcastReceiver broadcastReceiver;
    private Handler mainLooper, h;
    private UsbDevice usbDevice;
    private UsbSerialPort usbSerialPort;
    private UsbSerialDriver usbSerialDriver;
    private UsbDeviceConnection usbDeviceConnection;
    private SerialInputOutputManager usbIoManager;
    private int lengthAnswer;
    private ArrayList<Byte> bufAnswer = new ArrayList<>();
    private SpannableStringBuilder spn = new SpannableStringBuilder();

    private enum UsbPermission { Unknown, Requested, Granted, Denied };
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private boolean connected = false;

    public TestActivity() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(INTENT_ACTION_GRANT_USB)) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    refresh();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        usbManager = (UsbManager)getSystemService(Context.USB_SERVICE);

        textViewDevice = (TextView)findViewById(R.id.test_device);
        textRequestAnswer = (TextView)findViewById(R.id.test_request_answer);

        h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                textRequestAnswer.append(spn);
            }
        };

        findViewById(R.id.btn_test_status_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] data1 = {23, 0, 0, 0, 0, 0, 117, 11, 25, 127 & 0xFF, 0};
                String hexString = "170000000000750b19b000";
                data1 = HexDump.hexStringToByteArray(hexString);

                send(data1);
                bufAnswer.clear();
                lengthAnswer = 39;
                Thread threadRead = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        read();
                    }
                });
                threadRead.start();
            }
        });

        mainLooper = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        if(usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::refresh);
    }

    @Override
    public void onPause() {
        if(connected) {
            status("disconnected");
            disconnect();
        }
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    void refresh() {
        usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            spn.clear();
            spn.append("\nusbManager == null");
            textViewDevice.append(spn);
            return;
        }

        if (usbManager.getDeviceList().values().isEmpty()){
            spn.clear();
            spn.append("\ndeviceList empty");
            textViewDevice.append(spn);
            return;
        }

        for(UsbDevice device : usbManager.getDeviceList().values()) {
            usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(device);

            if(usbSerialDriver == null) {
                textViewDevice.setText(textViewDevice.getText() + "Driver: <no driver>" + "\n" + "Device: " + getDeviceValues(device));
                return;
            }else if(usbSerialDriver.getPorts().size() == 1){
                textViewDevice.setText(textViewDevice.getText() + "Driver: " +
                        usbSerialDriver.getClass().getSimpleName().replace("SerialDriver","") +
                        "\n" + "Device: " + getDeviceValues(device));
            }
        }

        usbSerialPort = usbSerialDriver.getPorts().get(0);
        usbDeviceConnection = usbManager.openDevice(usbSerialDriver.getDevice());
/*        if(usbDeviceConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(usbSerialDriver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(usbSerialDriver.getDevice(), usbPermissionIntent);
            return;
        }
        if(usbDeviceConnection == null) {
            if (!usbManager.hasPermission(usbSerialDriver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }*/

        try {
            usbSerialPort.open(usbDeviceConnection);
            usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
//            usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
//            Executors.newSingleThreadExecutor().submit(usbIoManager);
            status("connected");
            connected = true;
        } catch (Exception e) {
            status("connection failed: " + e.getMessage());
            disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardHider.hideKeyboard(this);
                this.finish();
                return true;
            case R.id.test_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getDeviceValues(UsbDevice usbDevice){
        return "DeviceName: " + usbDevice.getDeviceName() + "\n" +
                "ManufacturerName: " + usbDevice.getManufacturerName() + "\n" +
                "ProductName: " + usbDevice.getProductName() + "\n" +
                "SerialNumber: " + usbDevice.getSerialNumber() + "\n" +
                "Version: " + usbDevice.getVersion() + "\n" +
                "ConfigurationCount: " + usbDevice.getConfigurationCount() + "\n" +
                "DeviceClass: " + usbDevice.getDeviceClass() + "\n" +
                "DeviceId: " + usbDevice.getDeviceId() + "\n" +
                "DeviceProtocol: " + usbDevice.getDeviceProtocol() + "\n" +
                "DeviceSubclass: " + usbDevice.getDeviceSubclass() + "\n" +
                "InterfaceCount: " + usbDevice.getInterfaceCount() + "\n" +
                "ProductId: " + usbDevice.getProductId() + "\n" +
                "VendorId: " + usbDevice.getVendorId() + "\n";
    }

    public void read(){
        if(!connected) {
            return;
        }
        try {
            spn.clear();
            byte[] buffer = new byte[256];
            int len = usbSerialPort.read(buffer, 2000);
            spn.append("receive ").append(String.valueOf(len)).append(" bytes\n");
            if (len != 0){
                spn.append(HexDump.dumpHexString(Arrays.copyOfRange(buffer, 0, len + 1))).append("\n");
            }
            h.sendEmptyMessage(0);

//            textRequestAnswer.append(spn);
//            receive(Arrays.copyOf(buffer, len));  //сделать флажок
        } catch (IOException e) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            status("\nconnection lost: " + e.getMessage());
            disconnect();
        }
    }

    private void send(final byte[] data) {
        spn.clear();
        if(!connected) {
            spn.append("\nnot connected");
            h.sendEmptyMessage(0);
            return;
        }
        try {
            spn.append("send " + data.length + " bytes\n");
            spn.append(HexDump.dumpHexString(data)+"\n");
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Green)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textRequestAnswer.append(spn);
            usbSerialPort.write(data, 2000);
        } catch (Exception e) {
            onRunError(e);
        }
    }

    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
//            receive(data);
        });
    }

    @Override
    public void onRunError(Exception e) {
        mainLooper.post(() -> {
            status("connection lost: " + e.getMessage());
            disconnect();
        });
    }

    private void receive(byte[] data) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        spn.append("receive " + data.length + " bytes\n");
        if(data.length > 0){
            for (int i = 0; i < data.length; i++){
                bufAnswer.add(data[i]);
            }
        }
        if (bufAnswer.isEmpty()) {
            spn.append("Нет ответа\n");
        }
        if (bufAnswer.size() >= lengthAnswer){
            spn.append(HexDump.dumpHexString(bufAnswer)+"\n");
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Red)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textRequestAnswer.append(spn);
        }
    }

    void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str+'\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Black)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textRequestAnswer.append(spn);
    }

    private void disconnect() {
        connected = false;
        if(usbIoManager != null)
            usbIoManager.stop();
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }
}
