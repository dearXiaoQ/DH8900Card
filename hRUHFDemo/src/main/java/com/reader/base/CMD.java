package com.reader.base;

public class CMD {
	public final static byte RESET = 0x0D;
	public final static byte GET_FIRMWARE_VERSION = 0x02;
	public final static byte SET_OUTPUT_POWER = 0x31;
	public final static byte GET_OUTPUT_POWER = 0x32;
	public final static byte SET_BEEPER_MODE = 0x21;
	public final static byte GET_BARCODE = 0x23;
	public final static byte READ_TAG = (byte) 0x35;
	public final static byte WRITE_TAG = (byte) 0x36;
	public final static byte LOCK_TAG = (byte) 0x37;
	public final static byte KILL_TAG = (byte) 0x38;
	public final static byte REAL_TIME_INVENTORY = (byte) 0x39;
	public final static byte SELECT_SPECIFIC_TAG = (byte) 0x3E;
	public final static byte CANCEL_SPECIFIC_TAG = (byte) 0x3F;
	
	public final static byte REFRESH_COUNT = (byte) 0xF9;
	
	public static String format(byte btCmd)
    {
		String strCmd = "";
        switch (btCmd)
        {
            case RESET:
            	strCmd = "复位读写器";
                break;
            case GET_FIRMWARE_VERSION:
                strCmd = "获取版本";
                break;

            case SET_OUTPUT_POWER:
                strCmd = "设置输出功率";
                break;
            case GET_OUTPUT_POWER:
                strCmd = "获取输出功率";
                break;
            case SET_BEEPER_MODE:
                strCmd = "设置蜂鸣器状态";
                break;
            case GET_BARCODE:
                strCmd = "读取条码";
                break;
            case READ_TAG:
                strCmd = "读标签";
                break;
            case WRITE_TAG:
                strCmd = "写标签";
                break;
            case LOCK_TAG:
                strCmd = "锁定标签";
                break;
            case KILL_TAG:
                strCmd = "灭活标签";
                break;
            case REAL_TIME_INVENTORY:
                strCmd = "盘存标签";
                break;
            case SELECT_SPECIFIC_TAG:
                strCmd = "选中标签";
                break;
            case CANCEL_SPECIFIC_TAG:
                strCmd = "取消选中标签";
                break;
            default:
            	strCmd = "未知操作";
                break;
        }
        return strCmd;
    }
}
