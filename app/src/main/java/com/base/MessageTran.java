package com.base;

public class MessageTran {
	/*
    private byte btPacketType;     //���ݰ�ͷ��Ĭ��Ϊ0x1B
    private byte btDataLen;        //���ݰ����ȣ����ݰ��ӡ����ȡ��ֽں��濪ʼ���ֽ����������������ȡ��ֽڱ���
    private byte btReadId;         //��д����ַ
    private byte btCmd;            //���ݰ��������
    private byte[] btAryData;      //���ݰ�������������������޲���
    private byte btCheck;          //У��ͣ���У��ͱ����������ֽڵ�У���
    private byte[] btAryTranData;  //�������ݰ�
*/
    private byte btPacketType;	//���ݰ�ͷ��Ĭ��Ϊ0x1B
    private byte btCmd;	        //���ݰ��������
    private byte btSequence;    //���ݰ����к�
    private byte btOpcode;	    //���ݰ�������
    private byte btLen_Low;	    //���ݳ��ȵĵ�λ
    private byte btLen_High;    //���ݳ��ȵĸ�λ
    private byte[] btAryTranData;  //�������ݰ�
    private byte btCheck;           //У���
    private byte btReadId;         //��д����ַ
    private byte[] btAryData;      //���ݰ�������������������޲���
    
    public MessageTran(byte btReadId, byte btCmd, byte[] btAryData) 
    {
    	/*
        int nLen = btAryData.length;

        this.btPacketType = HEAD.HEAD;
        this.btDataLen = (byte)(nLen + 3);
        this.btReadId = btReadId;
        this.btCmd = btCmd;

        this.btAryData = new byte[nLen];
        System.arraycopy(btAryData, 0, this.btAryData, 0, btAryData.length);
        //btAryData.CopyTo(this.btAryData, 0);

        this.btAryTranData = new byte[nLen + 5];
        this.btAryTranData[0] = this.btPacketType;
        this.btAryTranData[1] = this.btDataLen;
        this.btAryTranData[2] = this.btReadId;
        this.btAryTranData[3] = this.btCmd;
        System.arraycopy(this.btAryData, 0, this.btAryTranData, 4, this.btAryData.length);
        //this.btAryData.CopyTo(this.btAryTranData, 4);

        this.btCheck = checkSum(this.btAryTranData, 0, nLen + 4);
        this.btAryTranData[nLen + 4] = this.btCheck;
        */
        int nLen = btAryData.length;
        this.btPacketType = 0x1B;
        this.btLen_Low = (byte)(nLen);
        this.btLen_High = (byte)(nLen >> 8);

        this.btReadId = btReadId;
        this.btCmd = btCmd;

        this.btAryData = new byte[nLen];
        System.arraycopy(btAryData, 0, this.btAryData, 0, btAryData.length);
        //btAryData.CopyTo(this.btAryData, 0);

        this.btAryTranData = new byte[nLen + 7];
        this.btAryTranData[0] = this.btPacketType;
        this.btAryTranData[1] = this.btCmd;
        this.btAryTranData[2] = 0x00;
        this.btAryTranData[3] = btReadId;
        this.btAryTranData[4] = this.btLen_Low;//btLength0
        this.btAryTranData[5] = this.btLen_High;//btLength1
        System.arraycopy(this.btAryData, 0, this.btAryTranData, 6, this.btAryData.length);
        //this.btAryData.CopyTo(this.btAryTranData, 6);

        this.btCheck = CheckSum(this.btAryTranData, 0, nLen + (7 - 1));
        this.btAryTranData[nLen + 6] = this.btCheck;
    }

    public MessageTran(byte btReadId, byte btCmd) 
    {
    	/*
        this.btPacketType = HEAD.HEAD;
        this.btDataLen = 0x03;
        this.btReadId = btReadId;
        this.btCmd = btCmd;

        this.btAryTranData = new byte[5];
        this.btAryTranData[0] = this.btPacketType;
        this.btAryTranData[1] = this.btDataLen;
        this.btAryTranData[2] = this.btReadId;
        this.btAryTranData[3] = this.btCmd;

        this.btCheck = checkSum(this.btAryTranData, 0, 4);
        this.btAryTranData[4] = this.btCheck;
        */
        this.btCmd = btCmd;
        this.btPacketType = 0x1B;
        this.btAryTranData = new byte[7];
        this.btAryTranData[0] = this.btPacketType;
        this.btAryTranData[1] = this.btCmd;
        this.btAryTranData[2] = 0x00;//btSequence
        this.btAryTranData[3] = 0x00;//btOpcode
        this.btAryTranData[4] = 0x00;//btLength0
        this.btAryTranData[5] = 0x00;//btLength1
        this.btAryTranData[6] = 0x00;//BCC
    }

    public MessageTran(byte[] btAryTranData) 
    {
    	/*
        int nLen = btAryTranData.length;

        this.btAryTranData = new byte[nLen];
        System.arraycopy(btAryTranData, 0, this.btAryTranData, 0, btAryTranData.length);
        //btAryTranData.CopyTo(this.btAryTranData, 0);


        byte btCK = checkSum(this.btAryTranData, 0, this.btAryTranData.length - 1);
        if (btCK != btAryTranData[nLen - 1]) {
            return;
        }

        this.btPacketType = btAryTranData[0];
        this.btDataLen = btAryTranData[1];
        this.btReadId = btAryTranData[2];
        this.btCmd = btAryTranData[3];
        this.btCheck = btAryTranData[nLen - 1];

        if (nLen > 5) {
            this.btAryData = new byte[nLen - 5];
            for (int nloop = 0; nloop < nLen - 5; nloop++ ) {
                this.btAryData[nloop] = btAryTranData[4 + nloop];
            }
        }
        */
        int nLen = btAryTranData.length;

        this.btAryTranData = new byte[nLen];
        System.arraycopy(btAryTranData, 0, this.btAryTranData, 0, btAryTranData.length);
        //btAryTranData.CopyTo(this.btAryTranData, 0);

        byte btCK = CheckSum(this.btAryTranData, 0, this.btAryTranData.length - 1);
        if (btCK != btAryTranData[nLen - 1])
        {
            return;
        }
        this.btPacketType = btAryTranData[0];
        this.btCmd = btAryTranData[1];
        this.btSequence = btAryTranData[2];
        this.btOpcode = btAryTranData[3];
        this.btLen_Low = btAryTranData[4];
        this.btLen_High = btAryTranData[5];
        this.btCheck = btAryTranData[nLen - 1];
        if (nLen > 7)
        {
            this.btAryData = new byte[nLen - 7];
            for (int nloop = 0; nloop < nLen - 7; nloop++ )
            {
                this.btAryData[nloop] = btAryTranData[6 + nloop];
            }
        }
    }
    
    //��������
    /**
     * ��ȡ�������ݰ���
     * @return	���ݰ�
     */
    public byte[] getAryTranData() {
            return this.btAryTranData;
    }

    /**
     * ��ȡ���ݰ�������������������޲�����
     * @return	�������
     */
    public byte[] getAryData() {
            return this.btAryData;
    }

    /**
     * ��ȡ��д����ַ��
     * @return	��д����ַ
     */
    public byte getReadId() {
            return this.btReadId;
    }

    /**
     * ��ȡ���ݰ������֡�
     * @return	������
     */
    public byte getCmd() {
            return this.btCmd;
    }

    /**
     * ��ȡ���ݰ�ͷ��Ĭ��Ϊ0x1B��
     * @return	���ݰ�ͷ
     */
    public byte getPacketType() {
            return this.btPacketType;
    }
    
    /**
     * У���ͷ��
     * @return	У����
     */
    public boolean checkPacketType() {
        return this.btPacketType == HEAD.HEAD;
    }

    /**
     * ����У���
     * @param btAryBuffer	����
     * @param nStartPos		��ʼλ��
     * @param nLen			У�鳤��
     * @return	У���
     */
    /*
    private byte checkSum(byte[] btAryBuffer, int nStartPos, int nLen) 
    {
        byte btSum = 0x00;

        for (int nloop = nStartPos; nloop < nStartPos + nLen; nloop++ ) {
            btSum += btAryBuffer[nloop];
        }

        return (byte)(((~btSum) + 1) & 0xFF);
    }
    */
    public byte CheckSum(byte[] btAryBuffer, int nStartPos, int nLen)
    {
        byte u8Bcc = 0;
        for (int nloop = 6; nloop < nStartPos + nLen; nloop++)
        {
            u8Bcc ^= btAryBuffer[nloop];
        }
        return u8Bcc;
    }
}
