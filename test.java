package src;

import java.util.ArrayList;

public class test {

	
	public static void main(String[] args){
		
		String a = "hello";
		String b = "hi";
		String c = "aloha";
		
		ArrayList<String> strList = new ArrayList<String>(4);
		
		
		System.out.println("size: "+strList.size());
		
		
		strList.add(a);
		strList.add(b);
		strList.add(c);
		
		for(int i=0;i<strList.size();i++){
			String tmp =(String) strList.get(i);
			System.out.println(tmp);
		}
		
		String d = strList.get(0);
		
		strList.remove(0);
		int size = strList.size();
		System.out.println("size after removing: "+size);
		for(String e: strList){
			System.out.println(e);
		}
		
		strList.add(a);
		System.out.println("Before switch");

		for(String e: strList){
			System.out.println(e);
		}
		
		 size = strList.size();
		 
			
		for(int i=0;i<size-1;i++){
			strList.set(size-i-1, strList.get(size-i-2));
			
		}
		
		System.out.println("After switch");
		for(String e: strList){
			System.out.println(e);
		}
		
		
	}
}
