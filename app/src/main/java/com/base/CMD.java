package com.base;

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
            	strCmd = "��λ��д��";
                break;
            case GET_FIRMWARE_VERSION:
                strCmd = "��ȡ�汾";
                break;

            case SET_OUTPUT_POWER:
                strCmd = "�����������";
                break;
            case GET_OUTPUT_POWER:
                strCmd = "��ȡ�������";
                break;
            case SET_BEEPER_MODE:
                strCmd = "���÷�����״̬";
                break;
            case GET_BARCODE:
                strCmd = "��ȡ����";
                break;
            case READ_TAG:
                strCmd = "����ǩ";
                break;
            case WRITE_TAG:
                strCmd = "д��ǩ";
                break;
            case LOCK_TAG:
                strCmd = "������ǩ";
                break;
            case KILL_TAG:
                strCmd = "����ǩ";
                break;
            case REAL_TIME_INVENTORY:
                strCmd = "�̴��ǩ";
                break;
            case SELECT_SPECIFIC_TAG:
                strCmd = "ѡ�б�ǩ";
                break;
            case CANCEL_SPECIFIC_TAG:
                strCmd = "ȡ��ѡ�б�ǩ";
                break;
            default:
            	strCmd = "δ֪����";
                break;
        }
        return strCmd;
    }
}
