package com.reader.helper;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.reader.base.CMD;
import com.reader.base.ERROR;
import com.reader.base.HEAD;
import com.reader.base.MessageTran;
import com.reader.base.ReaderBase;
import com.reader.base.StringTool;
import com.reader.helper.InventoryBuffer.InventoryTagMap;
import com.reader.helper.OperateTagBuffer.OperateTagMap;

public class ReaderHelper {
	public final static String BROADCAST_ON_LOST_CONNECT = "com.reader.helper.onLostConnect";
	public final static String BROADCAST_WRITE_DATA = "com.reader.helper.writeData";
	public final static String BROADCAST_WRITE_LOG = "com.reader.helper.writeLog";
	public final static String BROADCAST_REFRESH_READER_SETTING = "com.reader.helper.refresh.readerSetting";
	//public final static String BROADCAST_REFRESH_GET_BARCODE = "com.reader.helper.refresh.inventorybarcode";
	public final static String BROADCAST_REFRESH_INVENTORY_REAL = "com.reader.helper.refresh.inventoryReal";

	public final static String BROADCAST_REFRESH_OPERATE_TAG = "com.reader.helper.refresh.operateTag";
	
	private static LocalBroadcastManager mLocalBroadcastManager = null;
	
	public final static byte INVENTORY_ERR = 0x00;
	public final static byte INVENTORY_ERR_END = 0x01;
	public final static byte INVENTORY_END = 0x02;
	
	public final static int WRITE_LOG = 0x10;
	public final static int REFRESH_READER_SETTING = 0x11;
	public final static int REFRESH_INVENTORY = 0x12;
	public final static int REFRESH_INVENTORY_REAL = 0x13;
	public final static int REFRESH_FAST_SWITCH = 0x14;
	public final static int REFRESH_OPERATE_TAG = 0x15;
	public final static int REFRESH_ISO18000_6B = 0x15;
	
	public final static int LOST_CONNECT = 0x20;

	private static ReaderBase mReader;
	private static Context mContext;
	
	private static ReaderHelper mReaderHelper;
	
	private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    
	//�̴����ǰ����Ҫ�����ù������ߣ����ڱ�ʶ��ǰ�Ƿ���ִ���̴����
    public static boolean m_bInventory = false;
    //private boolean m_bISO6BContinue = false;
	//ʵʱ�̴����
    private int m_nTotal = 0;
    
    
    public static String hardwareStr = "";
    public static String mybarcodeStr = "";  
    /**
     * ���캯��
     */
    public ReaderHelper() {
		m_curReaderSetting = new ReaderSetting();
		m_curInventoryBuffer = new InventoryBuffer();
		m_curOperateTagBuffer = new OperateTagBuffer();
	}
    
    /**
     * ����Context��
     * @param context		����Context
     * @throws Exception	��ContextΪ��ʱ���׳�����
     */
	public static void setContext(Context context) throws Exception {
		mContext = context;
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		
		mReaderHelper = new ReaderHelper();
	}
	
    /**
     * ����helper��ȫ�ֵĶ�д�������ࡣ
     * @return				����helper��ȫ�ֵĶ�д��������
     * @throws Exception	��helper��ȫ�ֵĶ�д��������Ϊ��ʱ���׳�����
     */
	public static ReaderHelper getDefaultHelper() throws Exception {
		
		if (mReaderHelper == null || mContext == null) 
			throw new NullPointerException("mReaderHelper Or mContext is Null!");

		return mReaderHelper;
	}
    
	/**
	 * ����ѭ����־λ��
	 * @param flag	��־
	 */
	public void setInventoryFlag(boolean flag) {
		this.m_bInventory = flag;
	}

	/**
	 * ��ȡѭ����־λ��
	 * @return	��־
	 */
	public boolean getInventoryFlag() {
		return this.m_bInventory;
	}
		
	public int getInventoryTotal() {
		return this.m_nTotal;
	}
	
	public void setInventoryTotal(int num) {
		this.m_nTotal = num;
	}
	
	public void clearInventoryTotal() {
		this.m_nTotal = 0;
	}
    
	public ReaderSetting getCurReaderSetting() {
		return m_curReaderSetting;
	}

	public InventoryBuffer getCurInventoryBuffer() {
		return m_curInventoryBuffer;
	}

	public OperateTagBuffer getCurOperateTagBuffer() {
		return m_curOperateTagBuffer;
	}
	
	/**
	 * ��ʾlog��
	 * @param strLog	log��Ϣ
	 * @param type		log�ȼ�(0x00:��ȷ, 0x11:����)
	 */
	private void writeLog(String strLog, int type) {
		Intent itent = new Intent(BROADCAST_WRITE_LOG);
		itent.putExtra("type", type);
		itent.putExtra("log", strLog);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * ��д��������ˢ����ʾ��
	 * @param btCmd			��������(����ָ�����͵�ˢ��)
	 * @param curSetting	��ǰ��д��������
	 */
	private void refreshReaderSetting(byte btCmd, ReaderSetting curReaderSetting) {
		Intent itent = new Intent(BROADCAST_REFRESH_READER_SETTING);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * ���̱�ǩ(ʵʱģʽ)����ǩ����ˢ�¡�
	 * @param btCmd					��������(����ָ�����͵�ˢ��)
	 * @param curInventoryBuffer	��ǰ��ǩ����
	 */
	private void refreshInventoryReal(byte btCmd, InventoryBuffer curInventoryBuffer) {
		Intent itent = new Intent(BROADCAST_REFRESH_INVENTORY_REAL);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};

	
	/**
	 * ���̱�ǩ(����ģʽ)����ǩ����ˢ�¡�
	 * @param btCmd					��������(����ָ�����͵�ˢ��)
	 * @param curOperateTagBuffer	��ǰ��ǩ����
	 */
	private void refreshOperateTag(byte btCmd, OperateTagBuffer curOperateTagBuffer) {
		Intent itent = new Intent(BROADCAST_REFRESH_OPERATE_TAG);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * ���ò�����helper��ȫ�ֵĶ�д�����ࡣ
	 * @param in			������
	 * @param out			�����
	 * @return				helper��ȫ�ֵĶ�д������
	 * @throws Exception	��in��outΪ��ʱ���׳�����
	 */
	public ReaderBase setReader(InputStream in, OutputStream out) throws Exception {
		
		if (in == null || out == null) throw new NullPointerException("in Or out is NULL!");
		
		if (mReader == null) {

			mReader = new ReaderBase(in, out) {
				
				@Override
				public void onLostConnect() {
					mLocalBroadcastManager.sendBroadcast(new Intent(BROADCAST_ON_LOST_CONNECT));
				}
				
				@Override
				public void analyData(MessageTran msgTran) 
				{
					mReaderHelper.analyData(msgTran);
				}
				
				@Override
				public void reciveData(byte[] btAryReceiveData) 
				{
					String strLog = StringTool.byteArrayToString(btAryReceiveData, 0, btAryReceiveData.length);
					Intent itent = new Intent(BROADCAST_WRITE_DATA);
					itent.putExtra("type", ERROR.SUCCESS & 0xFF);
					itent.putExtra("log", strLog);
					mLocalBroadcastManager.sendBroadcast(itent);
				}
				
				@Override
				public void sendData(byte[] btArySendData) {
					String strLog = StringTool.byteArrayToString(btArySendData, 0, btArySendData.length);
					Intent itent = new Intent(BROADCAST_WRITE_DATA);
					itent.putExtra("type", ERROR.FAIL & 0xFF);
					itent.putExtra("log", strLog);
					mLocalBroadcastManager.sendBroadcast(itent);
				}
			};
		}
		
		return mReader;
	}

	/**
	 * ����helper��ȫ�ֵĶ�д�����ࡣ
	 * @return				helper��ȫ�ֵĶ�д������
	 * @throws Exception	��helper��ȫ�ֵĶ�д������Ϊ��ʱ���׳�����
	 */
	public ReaderBase getReader() throws Exception {
		if (mReader == null) {
			throw new NullPointerException("mReader is Null!");
		}
		
		return mReader;
	}
	/** 
	 * ����д������������һ�����ݺ����ô˺�����
	 * @param msgTran	�������İ�
	 */
	private void analyData(MessageTran msgTran) 
	{
		if (msgTran.getPacketType() != HEAD.HEAD) 
		{
            return;
        }
		
        switch(msgTran.getCmd()) 
        {
        case CMD.RESET:
        	processReset(msgTran);//
        	break;
        case CMD.GET_FIRMWARE_VERSION:
        	processGetFirmwareVersion(msgTran);//
        	break;
        case CMD.SET_OUTPUT_POWER:
        	processSetOutputPower(msgTran);//
        	break;
        case CMD.GET_OUTPUT_POWER:
        	processGetOutputPower(msgTran);//
        	break;
        case CMD.SET_BEEPER_MODE:
        	processSetBeeperMode(msgTran);//
        	break;
        case CMD.GET_BARCODE:
        	processGetBarcode(msgTran);//
        	break;      	
        	
        case CMD.READ_TAG:
        	processReadTag(msgTran);
        	break;
        case CMD.WRITE_TAG:
        	processWriteTag(msgTran);
        	break;
        case CMD.LOCK_TAG:
        	processLockTag(msgTran);
        	break;
        case CMD.KILL_TAG:
        	processKillTag(msgTran);
        	break;       	
        case CMD.REAL_TIME_INVENTORY:
        	processRealTimeInventory(msgTran);//===============
        	break;
        case CMD.SELECT_SPECIFIC_TAG:	
        	processSelectSpecificTag(msgTran);
        	break;
        case CMD.CANCEL_SPECIFIC_TAG:
        	processCancelSpecificTag(msgTran);
        	break;
		default:
		    break;
        }
	}
	
	/**
	 * ����������������ķ�����
	 * @param msgTran
	 */
	private void processSet(MessageTran msgTran) 
	{
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

		if (btAryData.length == 0x01) 
		{
			if (btAryData[0] == ERROR.SUCCESS) 
			{
				m_curReaderSetting.btReadId = msgTran.getReadId();

				writeLog(strCmd, ERROR.SUCCESS);
				return;
			} 
			else 
			{
				strErrorCode = ERROR.format(btAryData[0]);
			}
		} 
		else 
		{
			strErrorCode = "δ֪����";
		}

		String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
		writeLog(strLog, ERROR.FAIL);
	}

	private void processReset(MessageTran msgTran) 
	{
		processSet(msgTran);
	}

    private void processGetFirmwareVersion(MessageTran msgTran) 
    {
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

        if (btAryData.length >= 0x06) 
        {
        	m_curReaderSetting.btReadId = msgTran.getReadId();           
            hardwareStr=StringTool.byteArrayToString(btAryData,0,btAryData.length);
            hardwareStr=StringTool.toStringHex1(hardwareStr); 
            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            writeLog(hardwareStr, ERROR.SUCCESS);

            return;
        } 
        else if (btAryData.length == 0x01) 
        {
            strErrorCode = ERROR.format(btAryData[0]);
        } 
        else 
        {
            strErrorCode = "δ֪����";
        }

        String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }
    private void processGetBarcode(MessageTran msgTran) 
    {
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

        if (btAryData.length >0x01) 
        {
        	m_curReaderSetting.btReadId = msgTran.getReadId();           
        	mybarcodeStr=StringTool.byteArrayToString(btAryData,1,btAryData.length-1);
        	mybarcodeStr=StringTool.toStringHex1(mybarcodeStr);
             
            refreshReaderSetting(btCmd, m_curReaderSetting);
            writeLog(strCmd, ERROR.SUCCESS);
            writeLog(mybarcodeStr, ERROR.SUCCESS);

            return;
        } 
        else if (btAryData.length == 0x01) 
        {
            strErrorCode = ERROR.format(btAryData[0]);
        } 
        else 
        {
            strErrorCode = "δ֪����";
        }

        String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }
    
	private void processSetOutputPower(MessageTran msgTran) 
	{
		processSet(msgTran);
	}
	
	private void processGetOutputPower(MessageTran msgTran) {
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

		if (btAryData.length == 0x02) {
        	m_curReaderSetting.btReadId = msgTran.getReadId();
        	m_curReaderSetting.btAryOutputPower = btAryData[1];
        	
        	refreshReaderSetting(btCmd, m_curReaderSetting);
        	writeLog(strCmd, ERROR.SUCCESS);
            return;
        } else {
            strErrorCode = "δ֪����";
        }

        String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
        writeLog(strLog, ERROR.FAIL);
    }
	
	private void processSetBeeperMode(MessageTran msgTran) {
		processSet(msgTran);
	}
    
    private void processReadTag(MessageTran msgTran) 
    {
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

        if (btAryData.length == 0x01) 
        {
        	strErrorCode = CMD.format(btAryData[0]);
            String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
            writeLog(strLog, ERROR.FAIL);
        } 
        else if (btAryData.length > 0x03)
        {
        	int nLen = btAryData.length;
            int nEpcLen = (btAryData[0] & 0xFF)- 4;            
            int nDataLen = nLen-nEpcLen-5;
            
            String strPC = StringTool.byteArrayToString(btAryData, 2, 2);
            String strEPC = StringTool.byteArrayToString(btAryData, 4, nEpcLen);
            //String strCRC = StringTool.byteArrayToString(btAryData, 5 + nEpcLen, 2);
            String strData = StringTool.byteArrayToString(btAryData, nEpcLen + 5 , nDataLen);

            byte btAntId =  (byte) ((btAryData[nEpcLen+4] & 0xFF)+1);
            
            //int nReadCount = btAryData[nLen - 1] & 0xFF;

            OperateTagMap tag = new OperateTagMap();
            tag.strPC = strPC;
            tag.strCRC = "";
            tag.strEPC = strEPC;
            tag.strData = strData;
            tag.nDataLen= nDataLen;
            tag.btAntId = btAntId;
            tag.nReadCount = 1;
			m_curOperateTagBuffer.lsTagList.add(tag);

            refreshOperateTag(btCmd, m_curOperateTagBuffer);
            writeLog(strCmd, ERROR.SUCCESS);
        }
    }
	
    private void processWriteTag(MessageTran msgTran) 
    {
		byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		String strErrorCode = "";

        if (btAryData.length == 0x01) 
        {
            if (btAryData[0] == 0x00) 
            {
                String strLog = strCmd + "->�ɹ�";
                writeLog(strLog, ERROR.SUCCESS);
                return;
            }
            else
            {
	        	strErrorCode = CMD.format(btAryData[0]);
	            String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
	            writeLog(strLog, ERROR.FAIL);
            }
        } 
        else if (btAryData.length == 0x03) 
        {
           // writeLog(strCmd, ERROR.SUCCESS);
        }
    }
    
    /**
     * processWriteTag �� processLockTag ����һ�¡�
     * @param msgTran	��Ϣ��
     */
    private void processLockTag(MessageTran msgTran) {
    	processWriteTag(msgTran);
    }
    
    /**
     * processKillTag �� processLockTag ����һ�¡�
     * @param msgTran	��Ϣ��
     */
    private void processKillTag(MessageTran msgTran) {
    	processWriteTag(msgTran);
    }

    private void processRealTimeInventory(MessageTran msgTran) 
    {
		byte btCmd = msgTran.getCmd();
		byte []btAryData = msgTran.getAryData();
		String strCmd = CMD.format(btCmd);
		//String strErrorCode = "";
		String strLog="";

		if (btAryData.length <= 0x03) 
		{
			if (btAryData.length == 0x03) 
			{
				//=============================================================================
	            int u16TagCount = (btAryData[0] & 0xFF) + (btAryData[1] & 0xFF) * 256;
	            if (u16TagCount==65535)
	            {
	            	//strLog = strCmd + ":��ǰ�����̴����";
	                //writeLog(strLog, 0);
	
	                //У���Ƿ��̴����
	                if (m_bInventory)
	                {
	                    RunInventroy_Once();
	                }
	                refreshInventoryReal(CMD.REFRESH_COUNT, m_curInventoryBuffer);
	            }
	            else
	            {
	                strLog = strCmd + ":��ǰ�����̴����� " + u16TagCount+ " ��";
	                writeLog(strLog, 0);
	            }
	            return;
	        }
	        else if (btAryData.length == 1)
	        {
	            if (btAryData[0] == 0x80)
	            {
	                strLog = strCmd + ":��ǰ���߲�����...";
	                writeLog(strLog, 0x11);
	            }
	            if (btAryData[0] == 0x00)
	            {
	                strLog = strCmd + ":��ǰ���߿�ʼ����...";
	                writeLog(strLog, 1);
	            }
	            return;
	        }
		}

		else 
		{
			m_nTotal++;
			//int nLength = btAryData.length;
			int nEpcLength = btAryData[0];
			
			String strEPC = StringTool.byteArrayToString(btAryData, 4, nEpcLength-4);
			String strPC = StringTool.byteArrayToString(btAryData, 2, 2);
			
			String strRSSI = String.valueOf(btAryData[1] & 0xFF);

			byte btAntId = btAryData[nEpcLength];
			
			m_curInventoryBuffer.nCurrentAnt = btAntId & 0xFF;
			
			//String strFreq = "NULL";//getFreqString(btFreq);
			SimpleDateFormat dt= new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
			String strTime = dt.format(new Date());
			
			InventoryTagMap tag = null;
			Integer findIndex = m_curInventoryBuffer.dtIndexMap.get(strEPC);
			if (findIndex == null) 
			{
				tag = new InventoryTagMap();
				tag.strPC = strPC;
				tag.strEPC = strEPC;
				tag.strRSSI = strRSSI;
				tag.nReadCount = 1;
				tag.strFreq = strTime;
				m_curInventoryBuffer.lsTagList.add(tag);
				m_curInventoryBuffer.dtIndexMap.put(strEPC, m_curInventoryBuffer.lsTagList.size() - 1);
			} 
			else 
			{
				tag = m_curInventoryBuffer.lsTagList.get(findIndex);
				tag.strRSSI = strRSSI;
				tag.nReadCount++;
				tag.strFreq = strTime;
			}
			
			m_curInventoryBuffer.dtEndInventory = new Date();
			refreshInventoryReal(btCmd, m_curInventoryBuffer);
        }
    }
    
	public static void RunInventroy_Once()
    {
        if (m_curInventoryBuffer.Inventory_Count > 0)
        {
        	mReader.InventoryOnce(m_curReaderSetting.btReadId);
            if (m_curInventoryBuffer.Inventory_Count != 65535)
            	m_curInventoryBuffer.Inventory_Count--;//ѭ������
            //numericUpDown1.Value = Inventory_nTotal;
        }
        else if (m_curInventoryBuffer.Inventory_Count == 0)//�˳�����
        {
            m_bInventory = false;
            return;
        }
    }
	
	private void processSelectSpecificTag(MessageTran msgTran) 
	{
		//byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = "ѡ�б�ǩ";
		String strErrorCode = "";

		if (btAryData.length == 0x01) 
		{
			if (btAryData[0] == ERROR.SUCCESS) 
			{
				m_curReaderSetting.btReadId = msgTran.getReadId();

				writeLog(strCmd, ERROR.SUCCESS);
				return;
			} 
			else 
			{
				strErrorCode = ERROR.format(btAryData[0]);
			}
		} 
		else 
		{
			strErrorCode = "δ֪����";
		}

		String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
		writeLog(strLog, ERROR.FAIL);
	}
	private void processCancelSpecificTag(MessageTran msgTran) 
	{
		//byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = "ȡ��ѡ�б�ǩ";
		String strErrorCode = "";

		if (btAryData.length == 0x01) 
		{
			if (btAryData[0] == ERROR.SUCCESS) 
			{
				m_curReaderSetting.btReadId = msgTran.getReadId();

				writeLog(strCmd, ERROR.SUCCESS);
				return;
			} 
			else 
			{
				strErrorCode = ERROR.format(btAryData[0]);
			}
		} 
		else 
		{
			strErrorCode = "δ֪����";
		}

		String strLog = strCmd + "ʧ�ܣ�ʧ��ԭ�� " + strErrorCode;
		writeLog(strLog, ERROR.FAIL);
	}
}
