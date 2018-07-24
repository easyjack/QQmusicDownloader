package Qmusic;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * ������Ϣ������
 * @author WILO
 */
public class Media {
	/**js���ظ���*/
	public String title;
	/**js���ظ���*/
	public String singer;
	/**js����vkey*/
	public String vkey;
	/**js����filename*/
	public String filename;
	/**js����song mid*/
	public String songmid;
	/**��ȡvkey��ַ*/
	private static String getVkeyUrl = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?loginUin=0&format=json&inCharset=utf8&outCharset=utf-8&cid=205361747&guid=0";
	
	public Media(JsonElement data) throws ClientProtocolException, IOException{
		//��ʼ��������Ϣ
		JsonParser jsParser = new JsonParser();																	//���������׼����������
		JsonObject jsonOb = (JsonObject) jsParser.parse(data.toString());
		title = jsonOb.get("title").getAsString();																	//���������
		JsonArray singerArr = new JsonArray();																	//����json�������������Ϣ
		singerArr = jsonOb.get("singer").getAsJsonArray();
		singer = "";
		for(JsonElement js_singer : singerArr) {
			singer += (js_singer.getAsJsonObject().get("name") + " / ");
		}
		singer = singer.replaceAll("\"| / $", "");																	//������Ϣ��������
		filename = "C400" + jsonOb.get("file").getAsJsonObject().get("media_mid") + ".m4a";			//���������ļ���(���ڻ�ȡvkey)
		filename = filename.replaceAll("\"", "");
		songmid = jsonOb.get("mid").getAsString();																			//�������songmid(���ڻ�ȡvkey)
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpUriRequest httpUrlRe = new HttpGet(getVkeyUrl + "&filename=" + filename + "&songmid=" + songmid);
		CloseableHttpResponse response = httpClient.execute(httpUrlRe);
		if(response != null){
			HttpEntity entity = response.getEntity();			
			String result = EntityUtils.toString(entity, "UTF-8");
			jsonOb = (JsonObject)jsParser.parse(result);
			//��json��������ȡvkey
			vkey = jsonOb.get("data").getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("vkey").getAsString();
		}
		if (response != null){
            response.close();
        }
        if (httpClient != null){
            httpClient.close();
        }
	}
	
	public void debugShow() {
		System.out.println("title: " + title);
		System.out.println("singer: " + singer);
		System.out.println("filename: " + filename);
		System.out.println("songmid: " + songmid);
		System.out.println("playUrl: " + getMeidaUrl());
	}
	
	/**
	 * ���������ļ�ֱ����ַ
	 */
	public String getMeidaUrl() {
		String url = "http://dl.stream.qqmusic.qq.com/";
		url = url + filename + "?vkey=" + vkey + "&guid=0&uin=0";
        return url;
	}
}
