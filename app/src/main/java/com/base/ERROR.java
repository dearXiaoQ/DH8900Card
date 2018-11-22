package com.base;

public class ERROR {
	public final static byte SUCCESS = 0x00;
	public final static byte FAIL = 0x11;
	public final static byte MCU_RESET_ERROR = 0x20;
	public final static byte CW_ON_ERROR = 0x21;
	public final static byte ANTENNA_MISSING_ERROR = 0x22;
	public final static byte WRITE_FLASH_ERROR = 0x23;
	public final static byte READ_FLASH_ERROR = 0x24;
	public final static byte SET_OUTPUT_POWER_ERROR = 0x25;
	public final static byte TAG_INVENTORY_ERROR = 0x31;
	public final static byte TAG_READ_ERROR = 0x32;
	public final static byte TAG_WRITE_ERROR = 0x33;
	public final static byte TAG_LOCK_ERROR = 0x34;
	public final static byte TAG_KILL_ERROR = 0x35;
	public final static byte NO_TAG_ERROR = 0x36;
	public final static byte INVENTORY_OK_BUT_ACCESS_FAIL = 0x37;
	public final static byte BUFFER_IS_EMPTY_ERROR = 0x38;
	public final static byte ACCESS_OR_PASSWORD_ERROR = 0x40;
	public final static byte PARAMETER_INVALID = 0x41;
	public final static byte PARAMETER_INVALID_WORDCNT_TOO_LONG = 0x42;
	public final static byte PARAMETER_INVALID_MEMBANK_OUT_OF_RANGE = 0x43;
	public final static byte PARAMETER_INVALID_LOCK_REGION_OUT_OF_RANGE = 0x44;
	public final static byte PARAMETER_INVALID_LOCK_ACTION_OUT_OF_RANGE = 0x45;
	public final static byte PARAMETER_READER_ADDRESS_INVALID = 0x46;
	public final static byte PARAMETER_INVALID_ANTENNA_ID_OUT_OF_RANGE = 0x47;
	public final static byte PARAMETER_INVALID_OUTPUT_POWER_OUT_OF_RANGE = 0x48;
	public final static byte PARAMETER_INVALID_FREQUENCY_REGION_OUT_OF_RANGE = 0x49;
	public final static byte PARAMETER_INVALID_BAUDRATE_OUT_OF_RANGE = 0x4A;
	public final static byte PARAMETER_BEEPER_MODE_OUT_OF_RANGE = 0x4B;
	public final static byte PARAMETER_EPC_MATCH_LEN_TOO_LONG = 0x4C;
	public final static byte PARAMETER_EPC_MATCH_LEN_ERROR = 0x4D;
	public final static byte PARAMETER_INVALID_EPC_MATCH_MODE = 0x4E;
	public final static byte PARAMETER_INVALID_FREQUENCY_RANGE = 0x4F;
	public final static byte FAIL_TO_GET_RN16_FROM_TAG = 0x50;
	public final static byte PARAMETER_INVALID_DRM_MODE = 0x51;
	public final static byte PLL_LOCK_FAIL = 0x52;
	public final static byte RF_CHIP_FAIL_TO_RESPONSE = 0x53;
	public final static byte FAIL_TO_ACHIEVE_DESIRED_OUTPUT_POWER = 0x54;
	public final static byte COPYRIGHT_AUTHENTICATION_FAIL = 0x55;
	public final static byte SPECTRUM_REGULATION_ERROR = 0x56;
	public final static byte OUTPUT_POWER_TOO_LOW = 0x57;

	public static String format(byte btErrorCode)
    {
		String strErrorCode = "";
        switch (btErrorCode)
        {
            case SUCCESS:
                strErrorCode = "����ɹ����";
                break;
            case FAIL:
                strErrorCode = "����ִ��ʧ��";
                break;
            case MCU_RESET_ERROR:
                strErrorCode = "CPU ��λ����";
                break;
            case CW_ON_ERROR:
                strErrorCode = "��CW ����";
                break;
            case ANTENNA_MISSING_ERROR:
                strErrorCode = "����δ����";
                break;
            case WRITE_FLASH_ERROR:
                strErrorCode = "дFlash ����";
                break;
            case READ_FLASH_ERROR:
                strErrorCode = "��Flash ����";
                break;
            case SET_OUTPUT_POWER_ERROR:
                strErrorCode = "���÷��书�ʴ���";
                break;
            case TAG_INVENTORY_ERROR:
                strErrorCode = "�̴��ǩ����";
                break;
            case TAG_READ_ERROR:
                strErrorCode = "����ǩ����";
                break;
            case TAG_WRITE_ERROR:
                strErrorCode = "д��ǩ����";
                break;
            case TAG_LOCK_ERROR:
                strErrorCode = "������ǩ����";
                break;
            case TAG_KILL_ERROR:
                strErrorCode = "����ǩ����";
                break;
            case NO_TAG_ERROR:
                strErrorCode = "�޿ɲ�����ǩ����";
                break;
            case INVENTORY_OK_BUT_ACCESS_FAIL:
                strErrorCode = "�ɹ��̴浫����ʧ��";
                break;
            case BUFFER_IS_EMPTY_ERROR:
                strErrorCode = "����Ϊ��";
                break;
            case ACCESS_OR_PASSWORD_ERROR:
                strErrorCode = "���ʱ�ǩ���������������";
                break;
            case PARAMETER_INVALID:
                strErrorCode = "��Ч�Ĳ���";
                break;
            case PARAMETER_INVALID_WORDCNT_TOO_LONG:
                strErrorCode = "wordCnt ���������涨����";
                break;
            case PARAMETER_INVALID_MEMBANK_OUT_OF_RANGE:
                strErrorCode = "MemBank ����������Χ";
                break;
            case PARAMETER_INVALID_LOCK_REGION_OUT_OF_RANGE:
                strErrorCode = "Lock ����������������Χ";
                break;
            case PARAMETER_INVALID_LOCK_ACTION_OUT_OF_RANGE:
                strErrorCode = "LockType ����������Χ";
                break;
            case PARAMETER_READER_ADDRESS_INVALID:
                strErrorCode = "��д����ַ��Ч";
                break;
            case PARAMETER_INVALID_ANTENNA_ID_OUT_OF_RANGE:
                strErrorCode = "Antenna_id ������Χ";
                break;
            case PARAMETER_INVALID_OUTPUT_POWER_OUT_OF_RANGE:
                strErrorCode = "������ʲ���������Χ";
                break;
            case PARAMETER_INVALID_FREQUENCY_REGION_OUT_OF_RANGE:
                strErrorCode = "��Ƶ�淶�������������Χ";
                break;
            case PARAMETER_INVALID_BAUDRATE_OUT_OF_RANGE:
                strErrorCode = "�����ʲ���������Χ";
                break;
            case PARAMETER_BEEPER_MODE_OUT_OF_RANGE:
                strErrorCode = "���������ò���������Χ";
                break;
            case PARAMETER_EPC_MATCH_LEN_TOO_LONG:
                strErrorCode = "EPC ƥ�䳤��Խ��";
                break;
            case PARAMETER_EPC_MATCH_LEN_ERROR:
                strErrorCode = "EPC ƥ�䳤�ȴ���";
                break;
            case PARAMETER_INVALID_EPC_MATCH_MODE:
                strErrorCode = "EPC ƥ�����������Χ";
                break;
            case PARAMETER_INVALID_FREQUENCY_RANGE:
                strErrorCode = "Ƶ�ʷ�Χ���ò�������";
                break;
            case FAIL_TO_GET_RN16_FROM_TAG:
                strErrorCode = "�޷����ձ�ǩ��RN16";
                break;
            case PARAMETER_INVALID_DRM_MODE:
                strErrorCode = "DRM ���ò�������";
                break;
            case PLL_LOCK_FAIL:
                strErrorCode = "PLL ��������";
                break;
            case RF_CHIP_FAIL_TO_RESPONSE:
                strErrorCode = "��ƵоƬ����Ӧ";
                break;
            case FAIL_TO_ACHIEVE_DESIRED_OUTPUT_POWER:
                strErrorCode = "����ﲻ��ָ�����������";
                break;
            case COPYRIGHT_AUTHENTICATION_FAIL:
                strErrorCode = "��Ȩ��֤δͨ��";
                break;
            case SPECTRUM_REGULATION_ERROR:
                strErrorCode = "Ƶ�׹淶���ô���";
                break;
            case OUTPUT_POWER_TOO_LOW:
                strErrorCode = "������ʹ���";
                break;
            default:
            	strErrorCode = "δ֪����";
                break;
        }
        return strErrorCode;
    }
}
