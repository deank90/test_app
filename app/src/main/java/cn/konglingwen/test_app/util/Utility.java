package cn.konglingwen.test_app.util;

import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import cn.konglingwen.test_app.model.City;
import cn.konglingwen.test_app.model.County;
import cn.konglingwen.test_app.model.Province;
import cn.konglingwen.test_app.model.TestappDB;

/**
 * Created by Administrator on 2016/4/12.
 */
public class Utility {
    public synchronized static boolean parse_xml_info(TestappDB testappDB, String response){
        if (!TextUtils.isEmpty(response)){

            Province current_province = new Province();
            City current_city = new City();
            try {
                XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int eventType = xmlPullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT){
                    String nodeName = xmlPullParser.getName();
                    if (eventType == XmlPullParser.START_TAG){
                        if ("province".equals(nodeName)){
                            Province province = new Province();
                            province.setProvinceCode(xmlPullParser.getAttributeValue(null, "id"));
                            province.setProvinceName(xmlPullParser.getAttributeValue(null, "name"));
                            testappDB.saveProvince(province);
                            current_province = province;
                        }else if ("city".equals(nodeName)){
                            City city = new City();
                            city.setCityCode(xmlPullParser.getAttributeValue(null, "id"));
                            city.setCityName(xmlPullParser.getAttributeValue(null, "name"));
                            city.setProvinceId(current_province.getId());
                            testappDB.saveCity(city);
                            current_city = city;
                        }else if("county".equals(nodeName)){
                            County county = new County();
                            county.setCountyCode(xmlPullParser.getAttributeValue(null, "id"));
                            county.setCountyName(xmlPullParser.getAttributeValue(null, "name"));
                            county.setCityId(current_city.getId());
                            testappDB.saveCounty(county);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }
}
