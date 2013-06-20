package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class APIController {
	private String url = "https://api.bukalapak.com/v1/";
	static String userid;
	static String token;
	static JSONObject categories;
	static HashMap<String,JSONObject> attributes;
	static HashMap<String,Product> cachedProducts;
	private Context context;
	
	public APIController(Context con)
	{
		this.context = con;
	}	
	
	public APIController(Context con, String username, String password) throws Exception
	{
		this.context = con;
		retrieveNewAccess(username, password);
	}	

	private boolean isActive()
	{
		return (userid!=null && token!=null);
	}
	//////////////////////////TOOLS////////////////////////////
	private void traverse(HashMap<String,String> maps, JSONObject object)
	{
		//if(object.length()<=1) maps.put(object., value)
	}
	
	
	///////////////////////////////AUTHENTICATION API//////////////////////
	public void retrieveNewAccess(String user, String pwd) throws Exception
	{
		JSONObject response = requestPost(user, pwd, null, "authenticate.json");
		String status = response.getString("status");
		if(status.equals("OK"))
		{
			userid = response.getString("user_id");
			token = response.getString("token");
		}
	}
	
	
	/////////////////////////////PRODUCT API//////////////////////////////////
	public List<Product> listProducts(String query, int page, int perpage) throws Exception
	{
		String params = "?page="+page+"&per_page="+perpage;
		if(query!=null&&!query.isEmpty()) params+="&query="+query;
		JSONObject response = requestGet(userid, token,null, "products.json"+params);
		Log.e("Jumlah anak response", response.length()+"");
		Log.e("Jumlah name response", response.names().length()+"");
		String message = response.getString("message");
		Log.e("Jumlah anak message", message+"");
		return null;
	}
	
	public List<Product> listLapak(boolean available, boolean sold) throws Exception
	{
		String params = "";
		if(available && sold) params +="?available=true&sold=true";
		else if(available) params +="?available=true";
		else if(sold) params += "?sold=true";
		JSONObject response = requestGet(userid, token,null, "products/mylapak.json"+params);
		return null;
	}
	
	public void createProduct() throws Exception
	{
		JSONObject json = new JSONObject();
		JSONObject product = new JSONObject();
		JSONObject specs = new JSONObject();
		product.put("category_id", "242");
		product.put("name", "Polygon");
		product.put("price", "270000");
		product.put("weight", "5000");
		product.put("stock", "2");
		product.put("description_bb", "Sepeda roadbike polygon series heli");
		product.put("new", "true");
		product.put("negotiable", "true");
		json.put("product", product);
		json.put("images", "2554836");
		specs.put("type", "Roadbike");
		specs.put("brand", "Polygon");
		specs.put("bahan", "Cromoly");
		json.put("product_detail_attribute", specs);
		JSONObject response = requestPost(userid, token,json, "products.json");
	}
	
//	public Product readProduct()
//	{
//		
//	}
	
	public void deleteProduct()
	{
		
	}
	
	/////////////////////////CATEGORY API?//////////////////////////////////////////
	
	////////////////////////IMAGE API/////////////////////////////////////////////
	
	/////////////////////////TRANSACTIONS API/////////////////////////////////////
	
	
	
	public JSONObject requestPost(String userid,String token,JSONObject json,String suburl) throws Exception
	{
		JSONObject result = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url+suburl);
        // Execute HTTP Post Request
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Authorization",getB64Auth(userid,token));
		if(json!=null)
		{
			StringEntity se = new StringEntity(json.toString());
		    httppost.setEntity(se);
		}
        HttpResponse response = httpclient.execute(httppost);
        String temp = parse(response.getEntity().getContent());
        result =  new JSONObject(temp);
        String status = result.getString("status");
        if(status.equals("ERROR"))
		{
			throw new Exception(result.getString("message"));
		}
        return result;
	}
	
	public JSONObject requestGet(String userid,String token,String json,String suburl) throws Exception
	{
		JSONObject result = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httppost = new HttpGet(url+suburl);
        // Execute HTTP Post Request
        httppost.setHeader("Accept", "application/json");
	    httppost.setHeader("User-Agent", "Apache-HttpClient/4.1 (java 1.5)");
	    httppost.setHeader("Authorization",getB64Auth(userid,token));
        HttpResponse response = httpclient.execute(httppost);
        String temp = parse(response.getEntity().getContent());
        result =  new JSONObject(temp);
        String status = result.getString("status");
        if(status.equals("ERROR"))
		{
			throw new Exception(result.getString("message"));
		}
        return result;
	}
	
	private String parse(InputStream is)
    {
    	BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
		
        is.close();
        } 
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String response = sb.toString();
        return response;
    }
	private String getB64Auth (String login, String pass) {
	   String source=login+":"+pass;
	   String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
	   return ret;
	}
}
