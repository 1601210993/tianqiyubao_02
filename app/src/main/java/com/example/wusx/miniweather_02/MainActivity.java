package com.example.wusx.miniweather_02;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import cn.edu.pku.wushuangxiong.bean.TodayWeather;
import cn.edu.pku.wushuangxiong.util.NetUtil;

import static com.example.wusx.miniweather_02.R.layout.sixdays;
import static org.xmlpull.v1.XmlPullParserFactory.newInstance;

/**
 * Created by wusx on 2016/12/23.
 */

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener, ViewPager.OnPageChangeListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private ImageView mShare;

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.d_1, R.id.d_2};

    private LayoutInflater inflater;


    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    String cityCode, currentCityNumber, nextCityNumber;

    TodayWeather td;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //初始化小圆点对象
    void initDots() {
        dots = new ImageView[views.size()];
        int a;
        for (a = 0; a < views.size(); a++) {
            dots[a] = (ImageView) findViewById(ids[a]);
        }
    }

    //初始化viewAdapter
    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(sixdays, null));
        views.add(inflater.inflate(R.layout.sixdays_2, null));
        vpAdapter = new ViewPagerAdapter(views, this);
        vp = (ViewPager) findViewById(R.id.viewPager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        initViews();
        initDots();

        mShare = (ImageView) findViewById(R.id.title_share);

        mShare.setOnTouchListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);


        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //初始化今日天气
    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        //windTv.setText("N/A");
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }

        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("cityCode", "101010100");
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String cityNumber = data.getStringExtra("cityCode");
            nextCityNumber = cityNumber;
            cityCode = nextCityNumber;
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cityCode", nextCityNumber);
            editor.commit();
            preUpdateWeather();


        }
    }


    /**
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }


    private TodayWeather parseXML(String xmldata) throws XmlPullParserException {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int yesterdayFX = 0;
        int yesterdayFL = 0;
        int yesterdayType = 0;
        int typeCount = 0;
        int i = 0;  //五天天气索引
        boolean isFirstFengli = true; //XML文件中第一次出现风力风向的信息忽略，统一处理五天天气
        boolean isFirstFengxiang = true;

        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang")) {
                                    eventType = xmlPullParser.next();
                                    if (!isFirstFengxiang) {
                                        if (fengxiangCount == 0) {
                                            todayWeather.getWeatherDetails(i).setFengxiang(xmlPullParser.getText());
                                            Log.d("five", i + ":" + xmlPullParser.getText());
                                            fengxiangCount++;
                                        } else {
                                            fengxiangCount = 0;
                                        }
                                        isFirstFengxiang = false;}
                                    } else if (xmlPullParser.getName().equals("fengli")) {
                                            eventType = xmlPullParser.next();
                                        if (!isFirstFengli) {
                                            if (fengliCount == 0) {
                                                todayWeather.getWeatherDetails(i).setFengli(xmlPullParser.getText());
                                                Log.d("five", i + ":" + xmlPullParser.getText());
                                                i++;
                                                fengliCount++;
                                            } else {
                                                fengliCount = 0;
                                            }
                                            isFirstFengli = false;}
                                        } else if (xmlPullParser.getName().equals("date")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.getWeatherDetails(i).setDate(xmlPullParser.getText());
                                        } else if (xmlPullParser.getName().equals("high")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.getWeatherDetails(i).setHigh(xmlPullParser.getText().substring(2).trim());
                                        } else if (xmlPullParser.getName().equals("low")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.getWeatherDetails(i).setLow(xmlPullParser.getText().substring(2).trim());
                                            if (typeCount == 0) {
                                                todayWeather.getWeatherDetails(i).setType(xmlPullParser.getText());
                                                Log.d("five", i + ":" + xmlPullParser.getText());
                                                typeCount++;
                                            } else {
                                                typeCount = 0;
                                            }
                                        } else if (xmlPullParser.getName().equals("date_1")) {
                                            eventType = xmlPullParser.next();
                                            String date1 = xmlPullParser.getText();
                                            todayWeather.setYesterdayDate(date1.substring(date1.length() - 3, date1.length()).trim());
                                        } else if (xmlPullParser.getName().equals("high_1")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.setYesterdayHigh(xmlPullParser.getText().substring(2).trim());
                                        } else if (xmlPullParser.getName().equals("low_1")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.setYesterdayLow(xmlPullParser.getText().substring(2).trim());
                                        } else if (xmlPullParser.getName().equals("type_1")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.setYesterdayType(xmlPullParser.getText());
                                            yesterdayType++;
                                            if (yesterdayType == 0) {
                                                yesterdayType++;
                                            } else {
                                                todayWeather.setYesterdayType(xmlPullParser.getText());
                                            }
                                        } else if (xmlPullParser.getName().equals("fl_1")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.setYesterdayFengli(xmlPullParser.getText());
                                            yesterdayFL++;
                                            if (yesterdayFL == 0) {
                                                yesterdayFL++;
                                            } else {
                                                todayWeather.setYesterdayFengli(xmlPullParser.getText());
                                            }
                                        } else if (xmlPullParser.getName().equals("fx_1")) {
                                            eventType = xmlPullParser.next();
                                            todayWeather.setYesterdayFengxiang(xmlPullParser.getText());
                                            yesterdayFX++;
                                            if (yesterdayFX == 0) {
                                                yesterdayFX++;
                                            } else {
                                                todayWeather.setYesterdayFengxiang(xmlPullParser.getText());
                                            }
                                        }


                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                        break;
                                }

                            eventType = xmlPullParser.next();
                        }

                }catch(XmlPullParserException e1){
                    e1.printStackTrace();
                }catch(IOException e1){
                    e1.printStackTrace();
                }
                return todayWeather;
            }




    void preUpdateWeather() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        cityCode = sharedPreferences.getString("cityCode", "");
        if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络已连接");
            queryWeatherCode(cityCode);
        } else {
            Log.d("myWeather", "网络挂了");
            cityTv.setText("N/A");
            timeTv.setText("N/A");
            humidityTv.setText("N/A");
            pmDataTv.setText("N/A");
            pmQualityTv.setText("N/A");
            weekTv.setText("N/A");
            temperatureTv.setText("N/A");
            climateTv.setText("N/A");
            //windTv.setText("N/A");

            Toast.makeText(MainActivity.this, "网络挂了", Toast.LENGTH_LONG).show();
        }
    }

    private void setWeatherTypeImage(String weatherType, ImageView imageView) {
        switch (weatherType) {
            case "暴雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                imageView.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                imageView.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                imageView.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                imageView.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                imageView.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                imageView.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                imageView.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                imageView.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
        }
    }

    private void update5dayWeather(TodayWeather todayWeather) {
        vp.removeAllViews();
        views.clear();
        LayoutInflater inflater = LayoutInflater.from(this);
        View page_one;
        page_one = inflater.inflate(R.layout.sixdays,null);
        View page_two = inflater.inflate(R.layout.sixdays_2,null);

        //——昨日天气——
        ((TextView) page_one.findViewById(R.id.yesterday_weatherType)).setText(todayWeather.getYesterdayType());
        ((TextView) page_one.findViewById(R.id.yesterday_date)).setText(todayWeather.getYesterdayDate());
        ((TextView) page_one.findViewById(R.id.yesterday_high_low)).setText(
                todayWeather.getYesterdayHigh() + "~" + todayWeather.getYesterdayLow());
        ((TextView) page_one.findViewById(R.id.yesterday_fengli)).setText(todayWeather.getYesterdayFengli());
        setWeatherTypeImage(todayWeather.getYesterdayType(),
                ((ImageView) page_one.findViewById(R.id.yesterday_weatherImg)));
        Log.d("Yesterday",todayWeather.getYesterdayHigh());

        //——五日天气——1
        String day1_Date = todayWeather.getWeatherDetails(0).getDate();
        ((TextView) page_one.findViewById(R.id.day1_week)).setText(day1_Date.substring(day1_Date.length() - 3, day1_Date.length()));
        ((TextView) page_one.findViewById(R.id.day1_weatherTv)).setText(todayWeather.getWeatherDetails(0).getType());
        ((TextView) page_one.findViewById(R.id.day1_temperature)).setText(
                todayWeather.getWeatherDetails(0).getHigh() + "~" + todayWeather.getWeatherDetails(0).getLow());
        ((TextView) page_one.findViewById(R.id.day1_fengli)).setText(todayWeather.getWeatherDetails(0).getFengli());
        setWeatherTypeImage(todayWeather.getWeatherDetails(0).getType(),
                ((ImageView) page_one.findViewById(R.id.day1_weather)));
        Log.d("1", todayWeather.getWeatherDetails(0).getHigh());

        //——五日天气——2
        String day2_Date = todayWeather.getWeatherDetails(1).getDate();
        ((TextView) page_one.findViewById(R.id.day2_week)).setText(day2_Date.substring(day2_Date.length() - 3, day2_Date.length()));
        ((TextView) page_one.findViewById(R.id.day2_weatherTv)).setText(todayWeather.getWeatherDetails(1).getType());
        ((TextView) page_one.findViewById(R.id.day2_temperature)).setText(
                todayWeather.getWeatherDetails(1).getHigh() + "~" + todayWeather.getWeatherDetails(1).getLow());
        ((TextView) page_one.findViewById(R.id.day2_fengli)).setText(todayWeather.getWeatherDetails(1).getFengli());
        setWeatherTypeImage(todayWeather.getWeatherDetails(1).getType(),
                ((ImageView) page_one.findViewById(R.id.day2_weather)));
        Log.d("2", todayWeather.getWeatherDetails(1).getHigh());

        //——五日天气——3
        String day3_Date = todayWeather.getWeatherDetails(2).getDate();
        ((TextView) page_two.findViewById(R.id.day3_week)).setText(day3_Date.substring(day3_Date.length() - 3, day3_Date.length()));
        ((TextView) page_two.findViewById(R.id.day3_weatherTv)).setText(todayWeather.getWeatherDetails(2).getType());
        ((TextView) page_two.findViewById(R.id.day3_temperature)).setText(
                todayWeather.getWeatherDetails(2).getHigh() + "~" + todayWeather.getWeatherDetails(2).getLow());
        ((TextView) page_two.findViewById(R.id.day3_fengli)).setText(todayWeather.getWeatherDetails(2).getFengli());
        setWeatherTypeImage(todayWeather.getWeatherDetails(2).getType(),
                ((ImageView) page_two.findViewById(R.id.day3_weather)));
        Log.d("3", todayWeather.getWeatherDetails(2).getHigh());

        //——五日天气——4
        String day4_Date = todayWeather.getWeatherDetails(3).getDate();
        ((TextView) page_two.findViewById(R.id.day4_week)).setText(day4_Date.substring(day4_Date.length() - 3, day4_Date.length()));
        ((TextView) page_two.findViewById(R.id.day4_weatherTv)).setText(todayWeather.getWeatherDetails(3).getType());
        ((TextView) page_two.findViewById(R.id.day4_temperature)).setText(
                todayWeather.getWeatherDetails(3).getHigh() + "~" + todayWeather.getWeatherDetails(3).getLow());
        ((TextView) page_two.findViewById(R.id.day4_fengli)).setText(todayWeather.getWeatherDetails(3).getFengli());
        setWeatherTypeImage(todayWeather.getWeatherDetails(3).getType(),
                ((ImageView) page_two.findViewById(R.id.day4_weather)));
        Log.d("4", todayWeather.getWeatherDetails(3).getHigh());

        //——五日天气——5
        String day5_Date = todayWeather.getWeatherDetails(4).getDate();
        ((TextView) page_two.findViewById(R.id.day5_week)).setText(day5_Date.substring(day5_Date.length() - 3, day5_Date.length()));
        ((TextView) page_two.findViewById(R.id.day5_weatherTv)).setText(todayWeather.getWeatherDetails(4).getType());
        ((TextView) page_two.findViewById(R.id.day5_temperature)).setText(
                todayWeather.getWeatherDetails(4).getHigh() + "~" + todayWeather.getWeatherDetails(4).getLow());
        ((TextView) page_two.findViewById(R.id.day5_fengli)).setText(todayWeather.getWeatherDetails(4).getFengli());
        setWeatherTypeImage(todayWeather.getWeatherDetails(4).getType(),
                ((ImageView) page_two.findViewById(R.id.day5_weather)));

        views.add(page_one);
        views.add(page_two);
        vpAdapter.notifyDataSetChanged();
    }

    void updateTodayWeather(TodayWeather todayWeather) {

        //update5dayWeather(todayWeather);

        Log.d("myWeather", todayWeather.toString());
        if (todayWeather.getCity() == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cityCode", currentCityNumber);
            editor.commit();
            Toast.makeText(MainActivity.this, "未找到该城市天气信息！", Toast.LENGTH_SHORT).show();
        } else {
            currentCityNumber = nextCityNumber;

            city_name_Tv.setText(todayWeather.getCity() + "天气");
            cityTv.setText(todayWeather.getCity());
            timeTv.setText(todayWeather.getUpdatetime() + "发布");
            humidityTv.setText("湿度：" + todayWeather.getShidu());
            pmDataTv.setText(todayWeather.getPm25());
            pmQualityTv.setText(todayWeather.getQuality());
            weekTv.setText(todayWeather.getWeatherDetails(0).getDate());
            if (null != todayWeather.getPm25()) {
                pmDataTv.setText(todayWeather.getPm25());
            }
            //空气质量
            if (null != todayWeather.getQuality()) {
                pmQualityTv.setText(todayWeather.getQuality());
            }
            temperatureTv.setText(todayWeather.getWeatherDetails(0).getLow() + "~" + todayWeather.getWeatherDetails(0).getHigh());
            climateTv.setText(todayWeather.getWeatherDetails(0).getType());
            windTv.setText("风力：" + todayWeather.getWeatherDetails(0).getFengli());

            setWeatherTypeImage(todayWeather.getWeatherDetails(0).getType(), weatherImg);

            Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.title_city_manager:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.title_city_down);
                    Intent intent = new Intent(MainActivity.this, SelectCity.class);
                    intent.putExtra("cityName", cityTv.getText().toString());
                    startActivityForResult(intent, 1);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.title_city_up);
                }
                break;
            case R.id.title_update_btn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(R.drawable.title_update_down);
                    preUpdateWeather();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(R.drawable.title_update_up);
                }
                break;
            case R.id.title_share:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
                    //intent.putExtra(Intent.EXTRA_TEXT, td.toString() + "." + " (分享自MyWeather)");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent, getTitle()));

                }
                break;
            default:
                break;

        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int a = 0; a < ids.length; a++) {
            if (a == position) {
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            } else {
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction0() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction0());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());
        client.disconnect();
    }
}





