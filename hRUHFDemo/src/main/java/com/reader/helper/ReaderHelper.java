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
    
	//盘存操作前，需要先设置工作天线，用于标识当前是否在执行盘存操作
    public static boolean m_bInventory = false;
    //private boolean m_bISO6BContinue = false;
	//实时盘存次数
    private int m_nTotal = 0;
    
    
    public static String hardwareStr = "";
    public static String mybarcodeStr = "";  
    /**
     * 构造函数
     */
    public ReaderHelper() {
		m_curReaderSetting = new ReaderSetting();
		m_curInventoryBuffer = new InventoryBuffer();
		m_curOperateTagBuffer = new OperateTagBuffer();
	}
    
    /**
     * 设置Context。
     * @param context		设置Context
     * @throws Exception	当Context为空时会抛出错误
     */
	public static void setContext(Context context) throws Exception {
		mContext = context;
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		
		mReaderHelper = new ReaderHelper();
	}
	
    /**
     * 返回helper中全局的读写器帮助类。
     * @return				返回helper中全局的读写器帮助类
     * @throws Exception	当helper中全局的读写器帮助类为空时会抛出错误
     */
	public static ReaderHelper getDefaultHelper() throws Exception {
		
		if (mReaderHelper == null || mContext == null) 
			throw new NullPointerException("mReaderHelper Or mContext is Null!");

		return mReaderHelper;
	}
    
	/**
	 * 设置循环标志位。
	 * @param flag	标志
	 */
	public void setInventoryFlag(boolean flag) {
		this.m_bInventory = flag;
	}

	/**
	 * 获取循环标志位。
	 * @return	标志
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
	 * 显示log。
	 * @param strLog	log信息
	 * @param type		log等级(0x00:正确, 0x11:错误)
	 */
	private void writeLog(String strLog, int type) {
		Intent itent = new Intent(BROADCAST_WRITE_LOG);
		itent.putExtra("type", type);
		itent.putExtra("log", strLog);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * 读写器各参数刷新显示。
	 * @param btCmd			命令类型(用于指定类型的刷新)
	 * @param curSetting	当前读写器各参数
	 */
	private void refreshReaderSetting(byte btCmd, ReaderSetting curReaderSetting) {
		Intent itent = new Intent(BROADCAST_REFRESH_READER_SETTING);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * 存盘标签(实时模式)，标签数据刷新。
	 * @param btCmd					命令类型(用于指定类型的刷新)
	 * @param curInventoryBuffer	当前标签数据
	 */
	private void refreshInventoryReal(byte btCmd, InventoryBuffer curInventoryBuffer) {
		Intent itent = new Intent(BROADCAST_REFRESH_INVENTORY_REAL);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};

	
	/**
	 * 存盘标签(快速模式)，标签数据刷新。
	 * @param btCmd					命令类型(用于指定类型的刷新)
	 * @param curOperateTagBuffer	当前标签数据
	 */
	private void refreshOperateTag(byte btCmd, OperateTagBuffer curOperateTagBuffer) {
		Intent itent = new Intent(BROADCAST_REFRESH_OPERATE_TAG);
		itent.putExtra("cmd", btCmd);
		mLocalBroadcastManager.sendBroadcast(itent);
	};
	
	/**
	 * 设置并返回helper中全局的读写器基类。
	 * @param in			输入流
	 * @param out			输出流
	 * @return				helper中全局的读写器基类
	 * @throws Exception	当in或out为空时会抛出错误
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
	 * 返回helper中全局的读写器基类。
	 * @return				helper中全局的读写器基类
	 * @throws Exception	当helper中全局的读写器基类为空时会抛出错误
	 */
	public ReaderBase getReader() throws Exception {
		if (mReader == null) {
			throw new NullPointerException("mReader is Null!");
		}
		
		return mReader;
	}
	/** 
	 * 可重写函数，解析到一包数据后会调用此函数。
	 * @param msgTran	解析到的包
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
	 * 解析所有设置命令的反馈。
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
			strErrorCode = "未知错误";
		}

		String strLog = strCmd + "失败，失败原因： " + strErrorCode;
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
            strErrorCode = "未知错误";
        }

        String strLog = strCmd + "失败，失败原因： " + strErrorCode;
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
            strErrorCode = "未知错误";
        }

        String strLog = strCmd + "失败，失败原因： " + strErrorCode;
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
            strErrorCode = "未知错误";
        }

        String strLog = strCmd + "失败，失败原因： " + strErrorCode;
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
            String strLog = strCmd + "失败，失败原因： " + strErrorCode;
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
                String strLog = strCmd + "->成功";
                writeLog(strLog, ERROR.SUCCESS);
                return;
            }
            else
            {
	        	strErrorCode = CMD.format(btAryData[0]);
	            String strLog = strCmd + "失败，失败原因： " + strErrorCode;
	            writeLog(strLog, ERROR.FAIL);
            }
        } 
        else if (btAryData.length == 0x03) 
        {
           // writeLog(strCmd, ERROR.SUCCESS);
        }
    }
    
    /**
     * processWriteTag 与 processLockTag 返回一致。
     * @param msgTran	消息包
     */
    private void processLockTag(MessageTran msgTran) {
    	processWriteTag(msgTran);
    }
    
    /**
     * processKillTag 与 processLockTag 返回一致。
     * @param msgTran	消息包
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
	            	//strLog = strCmd + ":当前天线盘存结束";
	                //writeLog(strLog, 0);
	
	                //校验是否盘存操作
	                if (m_bInventory)
	                {
	                    RunInventroy_Once();
	                }
	                refreshInventoryReal(CMD.REFRESH_COUNT, m_curInventoryBuffer);
	            }
	            else
	            {
	                strLog = strCmd + ":当前天线盘存数据 " + u16TagCount+ " 笔";
	                writeLog(strLog, 0);
	            }
	            return;
	        }
	        else if (btAryData.length == 1)
	        {
	            if (btAryData[0] == 0x80)
	            {
	                strLog = strCmd + ":当前天线不可用...";
	                writeLog(strLog, 0x11);
	            }
	            if (btAryData[0] == 0x00)
	            {
	                strLog = strCmd + ":当前天线开始工作...";
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
            	m_curInventoryBuffer.Inventory_Count--;//循环次数
            //numericUpDown1.Value = Inventory_nTotal;
        }
        else if (m_curInventoryBuffer.Inventory_Count == 0)//退出读卡
        {
            m_bInventory = false;
            return;
        }
    }
	
	private void processSelectSpecificTag(MessageTran msgTran) 
	{
		//byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = "选中标签";
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
			strErrorCode = "未知错误";
		}

		String strLog = strCmd + "失败，失败原因： " + strErrorCode;
		writeLog(strLog, ERROR.FAIL);
	}
	private void processCancelSpecificTag(MessageTran msgTran) 
	{
		//byte btCmd = msgTran.getCmd();
    	byte []btAryData = msgTran.getAryData();
		String strCmd = "取消选中标签";
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
			strErrorCode = "未知错误";
		}

		String strLog = strCmd + "失败，失败原因： " + strErrorCode;
		writeLog(strLog, ERROR.FAIL);
	}
}
