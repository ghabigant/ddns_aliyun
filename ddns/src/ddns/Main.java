package ddns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse.Record;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class Main {
    static IClientProfile clientProfile;
    static Properties p;
    static SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        println("###开始初始化");
        init();
        println("###初始化完成");
        while (true) {
            try {
                Record r = getRecord();
                if (r == null)
                    throw new Exception("#缺少*解析");
                String ip = getOutIp().getIp();
                if (!ip.equals(r.getValue())) {
                    println("#开始修改解析:" + r.getValue() + " -> " + ip);
                    r.setValue(ip);
                    motifyRecord(r);
                    println("#解析修改完毕");
                } else {
                    println("#解析未改变");
                }
                String sleepTime = p.getProperty("sleepTime");
                TimeUnit.MINUTES.sleep(sleepTime == null ? 30 : Integer.parseInt(sleepTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void println(String string) {
        System.out.println(myFormatter.format(new Date()) + string);
    }

    private static void init() throws Exception {
        File file = new File("ddns.properties");
        p = new Properties();
        p.load(new FileInputStream(file));
        clientProfile = DefaultProfile.getProfile(p.getProperty("regionId"), p.getProperty("accessKeyId"),
                p.getProperty("secret"));
    }

    static Ip getOutIp() throws Exception {
        URL url = new URL("http://ip.chinaz.com/getip.aspx");
        String info;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            info = reader.readLine();
        }
        return JSON.parseObject(info, Ip.class);
    }

    static Record getRecord() throws Exception {
        DefaultAcsClient client = new DefaultAcsClient(clientProfile);
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setDomainName(p.getProperty("domain"));
        DescribeDomainRecordsResponse response = client.getAcsResponse(request);
        List<Record> list = response.getDomainRecords();
        return list.stream().filter(p -> p.getRR().equals("*")).findAny().orElse(null);
    }

    static UpdateDomainRecordResponse motifyRecord(Record r) throws Exception {
        DefaultAcsClient client = new DefaultAcsClient(clientProfile);
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRecordId(r.getRecordId());
        request.setRR(r.getRR());
        request.setType(r.getType());
        request.setValue(r.getValue());
        return client.getAcsResponse(request);
    }
}
