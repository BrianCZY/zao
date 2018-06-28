package com.hzu.zao.jsonorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * json��������
 * 
 * @author Nearby Yang
 * 
 *         Create at 2015-5-14 ����11:05:13
 */
public class sortJsonArrayByDate {

//	JSONArray mJSONArray;

	public JSONArray sortJson(JSONArray mJSONArray,String sortBy) {
		
//		for(int ii=0;ii<mJSONArray.length();ii++){
//			
//			try {
//				Log.i("����ǰ", mJSONArray.getJSONObject(ii).toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}

		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject jsonObj = null;
		for (int i = 0; i < mJSONArray.length(); i++) {

			jsonObj = mJSONArray.optJSONObject(i);
			list.add(jsonObj);

		}
		
		// �������
		JsonComparator pComparator = new JsonComparator(sortBy);
		Collections.sort(list, pComparator);

		// �����ݷŻ�ȥ
		mJSONArray = new JSONArray();
		
		for (int i = 0; i < list.size(); i++) {
			jsonObj = list.get(i);
//			Log.i("����֮��", jsonObj.toString()+"");
			mJSONArray.put(jsonObj);
		}
		
		return mJSONArray;
	}
}
