package cn.edu.pku.wushuangxiong.bean;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by wusx on 2016/12/23.
 */
public class TodayWeather {
    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private WeatherDetail[] weatherDetails = new WeatherDetail[5];


    private String yesterdayDate;
    private String yesterdayHigh;
    private String yesterdayLow;
    private String yesterdayType;
    private String yesterdayFengxiang;
    private String yesterdayFengli;



    public TodayWeather(){
        for(int i = 0; i < weatherDetails.length; i++){
            weatherDetails[i] = new WeatherDetail();
        }
    }
    //五天天气的相同特征
    public class WeatherDetail{
        private String date;
        private String high;
        private String low;
        private String type;
        private String fengxiang;
        private String fengli;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFengxiang() {
            return fengxiang;
        }

        public void setFengxiang(String fengxiang) {
            this.fengxiang = fengxiang;
        }

        public String getFengli() {
            String fl = "二级";
            return fl;
        }

        public void setFengli(String fengli) {
            this.fengli = fengli;
        }
    }
    public WeatherDetail getWeatherDetails(int i) {
        return this.weatherDetails[i];
    }
    public void setWeatherDetails(WeatherDetail weatherDetails, int i){
        this.weatherDetails[i] = weatherDetails;
    }



    public String getYesterdayDate() {
        return yesterdayDate;
    }

    public void setYesterdayDate(String yesterdayDate) {
        this.yesterdayDate = yesterdayDate;
    }

    public String getYesterdayHigh() {
        return yesterdayHigh;
    }

    public void setYesterdayHigh(String yesterdayHigh) {
        this.yesterdayHigh = yesterdayHigh;
    }

    public String getYesterdayLow() {
        return yesterdayLow;
    }

    public void setYesterdayLow(String yesterdayLow) {
        this.yesterdayLow = yesterdayLow;
    }

    public String getYesterdayType() {
        return yesterdayType;
    }

    public void setYesterdayType(String yesterdayType) {
        this.yesterdayType = yesterdayType;
    }

    public String getYesterdayFengxiang() {
        return yesterdayFengxiang;
    }

    public void setYesterdayFengxiang(String yesterdayFengxiang) {
        this.yesterdayFengxiang = yesterdayFengxiang;
    }

    public String getYesterdayFengli() {
        return yesterdayFengli;
    }

    public void setYesterdayFengli(String yesterdayFengli) {
        this.yesterdayFengli = yesterdayFengli;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }


//



    @Override
    public String toString() {
        return "TodayWeather:{" +
                "city= " + city + '\'' +
                "updatetime= " + updatetime + '\'' +
                "wendu= " + wendu + '\'' +
                "shidu= " + shidu + '\'' +
                "pm2.5= " + pm25 + '\'' +
                "quality= " + quality + '\''+
                '}';
    }

}