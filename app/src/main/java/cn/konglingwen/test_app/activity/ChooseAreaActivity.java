package cn.konglingwen.test_app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.konglingwen.test_app.R;
import cn.konglingwen.test_app.model.City;
import cn.konglingwen.test_app.model.County;
import cn.konglingwen.test_app.model.Province;
import cn.konglingwen.test_app.model.TestappDB;
import cn.konglingwen.test_app.util.HttpCallbackListener;
import cn.konglingwen.test_app.util.HttpUtil;
import cn.konglingwen.test_app.util.Utility;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private TestappDB testappDB;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        testappDB = TestappDB.getInstance(this);
        provinceList = testappDB.loadProvinces();
        if(provinceList == null){
            init();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long arg3) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(index);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(index);
                    queryCounties();
                }
            }
        });
        queryProvinces();

    }
//    Initialize the database, if there is no data inside.
    private void init(){
        String address;
        address = "https://raw.githubusercontent.com/deank90/test_app/master/china_cities.xml";
//        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                result = Utility.parse_xml_info(testappDB, response);
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseAreaActivity.this, "Initialized Unsuccessfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void queryProvinces(){
        provinceList = testappDB.loadProvinces();
        dataList.clear();
        for (Province province: provinceList){
            dataList.add(province.getProvinceName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText("中国");
        currentLevel = LEVEL_PROVINCE;
    }
    private void queryCities(){
        cityList = testappDB.loadCities(selectedProvince.getId());
        dataList.clear();
        for (City city: cityList){
            dataList.add(city.getCityName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText(selectedProvince.getProvinceName());
        currentLevel = LEVEL_CITY;
    }
    private void queryCounties(){
        countyList = testappDB.loadCounties(selectedCity.getId());
        dataList.clear();
        for (County county: countyList){
            dataList.add(county.getCountyName());
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText(selectedCity.getCityName());
        currentLevel = LEVEL_COUNTY;
    }

    @Override
    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
