/**
 * 文件名 ：ReaderMethod.java
 * CopyRright (c) 2008-xxxx:
 * 文件编号：2014-03-12_001
 * 创建人：Jie Zhu
 * 日期：2014/03/12
 * 修改人：Jie Zhu
 * 日期：2014/03/12
 * 描述：初始版本
 * 版本：1.0.0
 */

package com.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ReaderBase {
	private WaitThread mWaitThread = null;
	private AnalyThread mAnalyThread = null;
	private InputStream mInStream = null;
	private OutputStream mOutStream = null;

	private static BlockingQueue<MessageTran> mMessageTranQueue = new LinkedBlockingQueue<MessageTran>(100);

	/**
	 * 连接丢失。
	 */
	public abstract void onLostConnect();

	/**
	 * 可重写函数，解析到一包数据后会调用此函数。
	 * @param msgTran	解析到的包
	 */
	public abstract void analyData(MessageTran msgTran);

	// 记录未处理的接收数据，主要考虑接收数据分段
	private byte[] m_btAryBuffer = new byte[4096];
	// 记录未处理数据的有效长度
	private int m_nLength = 0;

	/**
	 * 带参构造函数，构造一个带输入、输出流的Reader。
	 * @param in	输入流
	 * @param out	输出流
	 */
	public ReaderBase(InputStream in, OutputStream out) {
		this.mInStream = in;
		this.mOutStream = out;
		mWaitThread = new WaitThread();
		mWaitThread.start();

		mAnalyThread = new AnalyThread();
		mAnalyThread.start();
	}

	/**
	 * 循环接收数据线程。
	 * @author Jie
	 */
	private class WaitThread extends Thread
	{
		private boolean mShouldRunning = true;

		public WaitThread()
		{
			mShouldRunning = true;
		}

		@Override
		/*public void run() {
		byte[] btAryBuffer = new byte[256];
		while (mShouldRunning) {
			try {
				int nLenRead = mInStream.read(btAryBuffer);

				if (nLenRead > 0) {
					byte[] btAryReceiveData = new byte[nLenRead];
					System.arraycopy(btAryBuffer, 0, btAryReceiveData, 0,
							nLenRead);
					runReceiveDataCallback(btAryReceiveData);
				}
			} catch (IOException e) {
				onLostConnect();
				return ;
			}
		}
		}*/

		public void run()
		{
			byte[] btAryBuffer = new byte[512];
			while (mShouldRunning)
			{
				try
				{
					int nLenRead = mInStream.read(btAryBuffer);

					if (nLenRead > 0) {
						byte[] btAryReceiveData = new byte[nLenRead];
						System.arraycopy(btAryBuffer, 0, btAryReceiveData, 0,
								nLenRead);
						runReceiveDataCallback(btAryReceiveData);
					}
					/*
					while (mInStream.read() != (HEAD.HEAD & 0xFF));

					int nLen = mInStream.read() & 0xFF;
					byte[] btAryBuffer = new byte[nLen + 1];
					btAryBuffer[0] = HEAD.HEAD;
					//btAryBuffer[1] = (byte)(nLen & 0xFF);

					int offset = 1;
					int remaining = nLen;
					int countRead = 0;
					do {
						countRead = (remaining < 256 ? remaining : 256);
						countRead = mInStream.read(btAryBuffer, offset, countRead);
						offset += countRead;
						remaining -= countRead;
					} while (remaining > 0);

					mMessageTranQueue.add(new MessageTran(btAryBuffer));
					*/
				}
				catch (IOException e)
				{
					onLostConnect();
					return ;
				}
				catch (Exception e)
				{
					;
				}
			}
		}

		public void signOut()
		{
			mShouldRunning = false;
			interrupt();
		}
	};

	/**
	 * MessageTran解析线程。
	 * @author Jie
	 */
	private class AnalyThread extends Thread {
		private boolean mShouldRunning = true;

		public AnalyThread() {
			mShouldRunning = true;
		}

		@Override
		public void run() {
			while (mShouldRunning) {
				try {

					MessageTran msgTran = mMessageTranQueue.take();
					analyData(msgTran);

					reciveData(msgTran.getAryTranData());
				} catch (InterruptedException e) {
					onLostConnect();
					return ;
				}
			}
		}

		public void signOut() {
			mShouldRunning = false;
			interrupt();
		}
	};

	/**
	 * 退出接收线程。
	 */
	public final void signOut() {
		mWaitThread.signOut();
		try {
			mInStream.close();
			mOutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAnalyThread.signOut();
	}

	/**
	 * 从输入流读取数据后，会调用此函数。
	 * @param btAryReceiveData	接收到的数据
	 */
	private void runReceiveDataCallback(byte[] btAryReceiveData) {
		try {
			reciveData(btAryReceiveData);

			int nCount = btAryReceiveData.length;
			byte[] btAryBuffer = new byte[nCount + m_nLength];

			System.arraycopy(m_btAryBuffer, 0, btAryBuffer, 0, m_nLength);
			System.arraycopy(btAryReceiveData, 0, btAryBuffer, m_nLength,
					btAryReceiveData.length);
			// Array.Copy(m_btAryBuffer, btAryBuffer, m_nLenth);
			// Array.Copy(btAryReceiveData, 0, btAryBuffer, m_nLenth,
			// btAryReceiveData.Length);

			// 分析接收数据，以0x1B为数据起点，以协议中数据长度为数据终止点
			int nIndex = 0; //当数据中存在0x1B时，记录数据的终止点
			int nMarkIndex = 0; //当数据中不存在0x1B时，nMarkIndex等于数据组最大索引
			for (int nLoop = 0; nLoop < btAryBuffer.length; nLoop++)
			{
				if (btAryBuffer.length > nLoop + 6)
				{
					if (btAryBuffer[nLoop] == HEAD.HEAD)
					{
						int nLen = (btAryBuffer[nLoop + 4] & 0xFF)+((btAryBuffer[nLoop + 5]*256) & 0xFF)+6;
						if (nLoop + nLen < btAryBuffer.length)
						{
							byte[] btAryAnaly = new byte[nLen + 1];
							System.arraycopy(btAryBuffer, nLoop, btAryAnaly, 0,nLen + 1);
							// Array.Copy(btAryBuffer, nLoop, btAryAnaly, 0,
							// nLen + 2);

							MessageTran msgTran = new MessageTran(btAryAnaly);
							analyData(msgTran);

							nLoop +=nLen;
							nIndex = nLoop + 1;
						}
						else
						{
							nLoop += nLen;
						}
					}
					else
					{
						nMarkIndex = nLoop;
					}
				}
			}

			if (nIndex < nMarkIndex)
			{
				nIndex = nMarkIndex + 1;
			}

			if (nIndex < btAryBuffer.length)
			{
				m_nLength = btAryBuffer.length - nIndex;
				Arrays.fill(m_btAryBuffer, 0, 4096, (byte) 0);
				System.arraycopy(btAryBuffer, nIndex, m_btAryBuffer, 0,
						btAryBuffer.length - nIndex);
				// Array.Clear(m_btAryBuffer, 0, 4096);
				// Array.Copy(btAryBuffer, nIndex, m_btAryBuffer, 0,
				// btAryBuffer.Length - nIndex);
			}
			else
			{
				m_nLength = 0;
			}
		} catch (Exception e)
		{

		}
	}

	/**
	 * 可重写函数，接收到数据时会调用此函数。
	 * @param btAryReceiveData	收到的数据
	 */
	public void reciveData(byte[] btAryReceiveData) {};

	/**
	 * 可重写函数，发送数据后会调用此函数。
	 * @param btArySendData	发送的数据
	 */
	public void sendData(byte[] btArySendData) {};

	/**
	 * 发送数据，使用synchronized()防止并发操作。
	 * @param btArySenderData	要发送的数据
	 * @return	成功 :0, 失败:-1
	 */
	private int sendMessage(byte[] btArySenderData) {

		try {
			synchronized (mOutStream) {		//防止并发
				mOutStream.write(btArySenderData);
			}
		} catch (IOException e) {
			onLostConnect();
			return -1;
		} catch (Exception e) {
			return -1;
		}

		sendData(btArySenderData);

		return 0;
	}

	private int sendMessage(byte btReadId, byte btCmd)
	{
		MessageTran msgTran = new MessageTran(btReadId, btCmd);

		return sendMessage(msgTran.getAryTranData());
	}

	private int sendMessage(byte btReadId, byte btCmd, byte[] btAryData) {
		MessageTran msgTran = new MessageTran(btReadId, btCmd, btAryData);

		return sendMessage(msgTran.getAryTranData());
	}

	/**
	 * 复位指定地址的读写器。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int reset(byte btReadId) {

		byte btCmd = CMD.RESET;

		int nResult = sendMessage(btReadId, btCmd);

		return nResult;
	}


	/**
	 * 读取读写器固件版本。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int getFirmwareVersion(byte btReadId) {
		byte btCmd = CMD.GET_FIRMWARE_VERSION;

		int nResult = sendMessage(btReadId, btCmd);

		return nResult;
	}


	/**
	 * @return	成功 :0, 失败:-1
	 */
	public final int setOutputPower(byte btReadId, byte btOutputPower) {
		byte btCmd = CMD.SET_OUTPUT_POWER;
		byte[] btAryData = new byte[2];
		btAryData[0] = 0x01;
		btAryData[1] = btOutputPower;

		int nResult = sendMessage(btReadId, btCmd, btAryData);

		return nResult;
	}

	/**
	 * 查询读写器当前输出功率。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int getOutputPower(byte btReadId) {
		byte btCmd = CMD.GET_OUTPUT_POWER;
		byte[] btAryData = new byte[1];
		btAryData[0] = 0x01;
		int nResult = sendMessage(btReadId, btCmd,btAryData);

		return nResult;
	}
	/**
	 * 读取条码，读取时间1秒 1000=0x3E8
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int getBarcode(byte btReadId) {
		byte btCmd = CMD.GET_BARCODE;
		byte[] btAryData = new byte[2];
		btAryData[0] = (byte)0xE8;
		btAryData[1] = 0x03;
		int nResult = sendMessage(btReadId, btCmd,btAryData);
		return nResult;
	}


	/**
	 * 设置蜂鸣器状态。
	 * @return	成功 :0, 失败:-1
	 */
	public final int setBeeperMode(byte btReadId, byte btMode) {
		byte btCmd = CMD.SET_BEEPER_MODE;
		byte[] btAryData = new byte[2];

		btAryData[0] = 0x0B;
		btAryData[1] = btMode;

		int nResult =sendMessage(btReadId, btCmd, btAryData);

		return nResult;
	}


	/**
	 * 读标签。
	 * <br>注意：
	 * <br>★相同EPC的标签，若读取的数据不相同，则被视为不同的标签。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @param btMemBank	标签存储区域(0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
	 * @param btWordAdd	读取数据首地址,取值范围请参考标签规格
	 * @param btWordCnt	读取数据长度,字长,WORD(16 bits)长度. 取值范围请参考标签规格书
	 * @param btAryPassWord	标签访问密码,4字节
	 * @return	成功 :0, 失败:-1
	 */
	public final int readTag(byte btReadId, byte btMemBank, byte btWordAdd,byte btWordCnt, byte[] btAryPassWord)
	{
		byte btCmd = CMD.READ_TAG;
		byte[] btAryData = new byte[8];

		btAryData[0] = btMemBank;
		btAryData[1] = btWordAdd;
		btAryData[2] = btWordCnt;
		if (btAryPassWord != null)
		{
			System.arraycopy(btAryPassWord, 0, btAryData, 3, 4);
		}
		btAryData[7] = 0x00;//天线号
		int nResult = sendMessage(btReadId, btCmd, btAryData);
		return nResult;
	}

	/**
	 * 写标签。
	 * @param btReadId		读写器地址(0xFF公用地址)
	 * @param btAryPassWord	标签访问密码,4字节
	 * @param btMemBank		标签存储区域(0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER)
	 * @param btWordAdd		数据首地址,WORD(16 bits)地址. 写入EPC存储区域一般从0x02开始,该区域前四个字节存放PC+CRC
	 * @param btWordCnt		WORD(16 bits)长度,数值请参考标签规格
	 * @param btAryData		写入的数据, btWordCnt*2 字节
	 * @return	成功 :0, 失败:-1
	 */
	public final int writeTag(byte btReadId, byte[] btAryPassWord, byte btMemBank,
							  byte btWordAdd, byte btWordCnt, byte[] btAryData)
	{
		byte btCmd = CMD.WRITE_TAG;
		byte[] btAryBuffer = new byte[btAryData.length + 8];


		// btAryPassWord.CopyTo(btAryBuffer, 0);
		btAryBuffer[0] = btMemBank;
		btAryBuffer[1] = btWordAdd;
		btAryBuffer[2] = btWordCnt;
		System.arraycopy(btAryPassWord, 0, btAryBuffer, 3, btAryPassWord.length);
		btAryData[7] = 0x00;//天线号
		System.arraycopy(btAryData, 0, btAryBuffer, 8, btAryData.length);
		int nResult = sendMessage(btReadId, btCmd, btAryBuffer);

		return nResult;
	}

	/**
	 * 锁定标签。
	 * @param btReadId		读写器地址(0xFF公用地址)
	 * @param btAryPassWord	标签访问密码,4字节
	 * @param btMembank		操作的数据区域(0x01:User Memory, 0x02:TID Memory, 0x03:EPC Memory, 0x04:Access Password, 0x05:Kill Password)
	 * @param btLockType	锁操作类型(0x00:开放, 0x01:锁定, 0x02:永久开放, 0x03:永久锁定)
	 * @return	成功 :0, 失败:-1
	 */
	public final int lockTag(byte btReadId, byte[] btAryPassWord, byte btMemBank,byte btLockType)
	{
		byte btCmd = CMD.LOCK_TAG;
		byte[] btAryData = new byte[7];

		btAryData[0] = btMemBank;
		btAryData[1] = btLockType;
		btAryData[2] = 0x00;//天线号
		System.arraycopy(btAryPassWord, 0, btAryData, 3, btAryPassWord.length);
		int nResult = sendMessage(btReadId, btCmd, btAryData);
		return nResult;
	}

	/**
	 * 灭活标签。
	 * @param btReadId		读写器地址(0xFF公用地址)
	 * @param btAryPassWord	标签销毁密码,4字节
	 * @return	成功 :0, 失败:-1
	 */
	public final int killTag(byte btReadId, byte[] btAryPassWord)
	{
		byte btCmd = CMD.KILL_TAG;
		byte[] btAryData = new byte[4];

		System.arraycopy(btAryPassWord, 0, btAryData, 0, btAryPassWord.length);
		// btAryPassWord.CopyTo(btAryData, 0);

		int nResult = sendMessage(btReadId, btCmd, btAryData);

		return nResult;
	}


	/**
	 * 盘存标签(实时上传标签数据)。
	 * <br>注意：
	 * <br>★由于硬件为双CPU架构，主CPU负责轮询标签，副CPU负责数据管理。轮询标签和发送数据并行，互不占用对方的时间，因此串口的数据传输不影响读写器工作的效率。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int InventoryOnce(byte btReadId)
	{
		byte btCmd = CMD.REAL_TIME_INVENTORY;
		int nResult = sendMessage(btReadId, btCmd);
		return nResult;
	}

	/**
	 * 匹配ACCESS操作的EPC号(匹配一直有效，直到下一次刷新)。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @param btEpcLen	EPC长度(Word)
	 * @param btAryEpc	EPC号, 由EpcLen*2个字节组成
	 * @return	成功 :0, 失败:-1
	 */
	public final int SelectSpecificTag(byte btReadId, byte btEpcLen,byte btEpcOpposite,byte[] btAryEpc)
	{
		byte btCmd =CMD.SELECT_SPECIFIC_TAG;
		int nLen = (btEpcLen & 0xFF) + 2;
		byte[] btAryData = new byte[nLen];

		btAryData[0] = (byte) ((byte)btEpcLen/2);
		btAryData[1] = btEpcOpposite;
		System.arraycopy(btAryEpc, 0, btAryData, 2, btAryEpc.length);
		// btAryEpc.CopyTo(btAryData, 2);
		int nResult = sendMessage(btReadId, btCmd, btAryData);
		return nResult;
	}

	/**
	 * 清除EPC匹配。
	 * @param btReadId	读写器地址(0xFF公用地址)
	 * @return	成功 :0, 失败:-1
	 */
	public final int CancelSpecificTag(byte btReadId)
	{
		byte btCmd =CMD.CANCEL_SPECIFIC_TAG;
		int nResult = sendMessage(btReadId, btCmd);
		return nResult;
	}
}
