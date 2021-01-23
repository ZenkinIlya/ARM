package com.driverUSB.utils;

public class DataBufTransformer {

    public static byte[] transformBufIntToBufByte(int[] intBuf){
        byte[] byteData = new byte[intBuf.length];
        for (int i = 0; i < intBuf.length; i++){
            byteData[i] = (byte) intBuf[i];
        }
        return byteData;
    }

    public static int[] transformBufByteToBufInt(byte[] byteBuf){
        int[] intData = new int[byteBuf.length];
        for (int i = 0; i < byteBuf.length; i++){
            if (byteBuf[i] < 0){
                intData[i] = 256 + byteBuf[i];
            }else {
                intData[i] = byteBuf[i];
            }
        }
        return intData;
    }

    public static int[] transformNumberToBuf(long number, int lengthOutputBuf) {
        int[] data = new int[lengthOutputBuf];
        int offset = 0;
        for (int i = 0; i < lengthOutputBuf; i++){
            data[i] = (int) ((number >> offset) & 0xFF);
            offset += 8;
        }
        return data;
    }

    public static String transformBufNumberToString(int[] data, int startPos, int length) {
        int[] buf = new int[length];
        System.arraycopy(data, startPos, buf, 0, length);
        long result = 0;
        for (int i = length - 1; i >= 0; i--){
            result = (result + buf[i]) << 8;
        }
        return String.valueOf(result);
    }

    public static String transformDateToString(int[] data, int startPos, int length) {
        int[] buf = new int[length];
        System.arraycopy(data, startPos, buf, 0, length);
        long result = 0;
        for (int i = length - 1; i > 0; i--){
            result = (result + (int)buf[i]) << 8;
        }
        result = result + buf[0];

        return result / 1000000 + "/" + ((result / 10000) % 100) + "/" + result % 10000;
    }

    public static String transformTimeToString(int[] data, int startPos, int length) {
        int[] buf = new int[length];
        System.arraycopy(data, startPos, buf, 0, length);
        long result = 0;
        result = (result + buf[1]) << 8;
        result = result + buf[0];
        return result / 100 + ":" + result % 100;
    }

    public static int[] calculateCRC(int[] data, byte length) {
        short crc = 0;
        int[] dataOutput;
        for (int i = 0; i < length - 2; i++){
            crc = (short) (crc + data[i]);
        }
        dataOutput = transformNumberToBuf(crc, 2);
        return dataOutput;
    }
}
