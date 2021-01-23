package com.driverUSB.commands;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.driverUSB.USBDriverHandler;
import com.driverUSB.utils.DataBufTransformer;
import com.driverUSB.utils.HexDump;
import com.qs5501.demo.settings.test.TestCustomDriverActivity;

import java.util.Arrays;
import java.util.HashMap;

public class StatusRequester {

    private static String answer;
    private HashMap<String, String> answerMap = new HashMap<>();
    private Handler handler, handlerToast;
    private String message;
    private Context context;
    private long numKKM;

    public HashMap<String, String> getAnswerMap() {
        return answerMap;
    }

    @SuppressLint("HandlerLeak")
    public StatusRequester(Context context, long numKKM) {
        this.context = context;
        this.numKKM = numKKM;

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                TestCustomDriverActivity.textView.setText(message);
            }
        };

        handlerToast = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(context, answer, Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void requestStatus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    USBDriverHandler usbDriverHandler = new USBDriverHandler(context);
                    answer = usbDriverHandler.openPort(context);
                    if (!answer.equals("success")){
                        handlerToast.sendEmptyMessage(0);
                        return;
                    }

                    byte length = 11;
                    int[] data = new int[length];
                    data[0] = 0x17;
                    data[1] = 0x00;

                    System.arraycopy(DataBufTransformer.transformNumberToBuf(numKKM, 4),
                            0, data, 2, 4);

                    data[6] = 'u';
                    data[7] = length;
                    data[8] = 0x19;

                    System.arraycopy (DataBufTransformer.calculateCRC(data, length),
                            0, data, 9, 2);

                    answer = usbDriverHandler.send(data);
                    if (!answer.equals("success")){
                        handlerToast.sendEmptyMessage(0);
                        return;
                    }

                    Thread threadRead = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            answer = usbDriverHandler.read(39);
                        }
                    });
                    threadRead.start();
                    threadRead.join();
                    if (!answer.equals("success")){
                        handlerToast.sendEmptyMessage(0);
                        return;
                    }

                    usbDriverHandler.closePort();

/*                    String s = HexDump.dumpHexString(DataBufTransformer.transformBufIntToBufByte(
                            Arrays.copyOfRange(usbDriverHandler.getBuffer(), 0,
                            usbDriverHandler.getBuffer().length + 1)));
                    answerMap.put("answer", s);
                    message = TestCustomDriverActivity.textView.getText() + "\n" + answerMap.entrySet().toString();
                    handler.sendEmptyMessage(0);*/

                    int[] dataAnswer = usbDriverHandler.getBuffer();
                    answerMap.put("quantityFiscalization", String.valueOf(dataAnswer[4]));
                    answerMap.put("quantityNotTransactionDoc", DataBufTransformer.transformBufNumberToString(
                            dataAnswer, 5, 2));
                    answerMap.put("quantityCloseShift", DataBufTransformer.transformBufNumberToString(
                            dataAnswer, 7, 2));
                    answerMap.put("stateCurrentShift", String.valueOf(dataAnswer[9]));
                    answerMap.put("stateCurrentDoc", String.valueOf(dataAnswer[10]));
                    answerMap.put("Date", DataBufTransformer.transformDateToString(
                            dataAnswer, 11, 4));
                    answerMap.put("Time", DataBufTransformer.transformTimeToString(
                            dataAnswer, 15, 2));
                    answerMap.put("numKKM",  DataBufTransformer.transformBufNumberToString(
                            dataAnswer, 17, 5));
                    answerMap.put("regNum", DataBufTransformer.transformBufNumberToString(
                            dataAnswer, 22, 5));
                    answerMap.put("inn", DataBufTransformer.transformBufNumberToString(
                            dataAnswer, 27, 5));
                    answerMap.put("chargeAccumulator", String.valueOf(dataAnswer[32]));
                    answerMap.put("errorModule", String.valueOf(dataAnswer[33]));
                    answerMap.put("errorServer", String.valueOf(dataAnswer[34]));
                    answerMap.put("paperPrint", String.valueOf(dataAnswer[35]));

                    message = TestCustomDriverActivity.textView.getText() + "\n" + answerMap.entrySet().toString();
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    answer = "Exception StatusRequester";
                    handlerToast.sendEmptyMessage(0);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
