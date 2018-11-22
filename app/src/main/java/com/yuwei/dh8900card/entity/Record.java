package com.yuwei.dh8900card.entity;

import com.yuwei.dh8900card.SettingActivity;
import com.yuwei.dh8900card.utils.DateUtils;

/**
 * Created by xiaoQ on 2018/4/6.
 */

public class Record {
    /** 数据库自增ID */
    private int Id;
    /** 1 = RFID  2 = 条码   3 = 二维码 */
    private int type = 1;
    /** 卡号 */
    private String cardNo = "";
    /** 时间戳 */
    private String timeStamp = "";
    /** 时间类型 chiptime,qrtime,manualtime */
    private String timeType = "";
    /** 是否上传 0 = 未上传   1 = 已上传 */
    private int upload = 0;
    /** GPS坐标 */
    private String gps;
    /** 是否已经写到文件 0 = 未写到文件， 1 = 已经写到文件 */
    private int toTxt = 0;
    /** 打卡点名称 */
    private String localName = "";
    /** 打卡日期 yyyyMMddd */
    private String yyyyMMdd = "";
    /** 是否显示 0 = 显示 1 = 不显示*/
    private int show;
    /** 设备号 */
    private String deviceNum = "";

    public void setShow(int show) {
        this.show = show;
    }

    public int getShow() {
        return show;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }


    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setYyyyMMdd(String yyyyMMdd) {
        this.yyyyMMdd = yyyyMMdd;
    }



    public String getYyyyMMdd() {
        return yyyyMMdd;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getUpload() {
        return upload;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getGps() {
        return gps;
    }

    public void setToTxt(int toTxt) {
        this.toTxt = toTxt;
    }

    public int getToTxt() {
        return toTxt;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    @Override
    public String toString() {
      /*  return "类型 = " + (type == 1 ? "RFID" : (type == 2 ? "条码" : "二维码")) + "   卡号 = " + cardNo +
                "   时间 = " + DateUtils.times(timeStamp) + "  gps = " + gps + "   是否上传 = " + (upload == 0 ? "未上传":"已上传");*/
        return  cardNo + "," + DateUtils.times(timeStamp);
    }

    public String toLog () {
        return cardNo + "," + DateUtils.times(timeStamp);
    }

    public String toBak() {
        try {
            return cardNo + "," + DateUtils.times(timeStamp) + "," + timeType + "," + gps + "," + "," + ",";
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
