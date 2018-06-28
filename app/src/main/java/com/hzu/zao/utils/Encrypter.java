package com.hzu.zao.utils;


/**
 * 用于字符串的加密与解密
 *
 * @author Darhao
 *
 */
public class Encrypter {

	//加密
	public static String encrypt(String text){
		String s = "";
		for(int i = 0; i < text.length();i++)
		{
			s+= ((char)(text.charAt(i)-130));
		}
		return s;
	}

	//解密
	public static String decrypt(String text){
		String s = "";
		for(int i = 0; i < text.length();i++)
		{
			s+= ((char)(text.charAt(i)+130));
		}
		return s;
	}
}
