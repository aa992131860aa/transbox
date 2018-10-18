package com.data.sort;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.life.entity.Blood;

public class Main {
public static int A =3;
public static List<String> list = new ArrayList<String>();
	public static void main(String[] args) {
		Integer a[] = { 4,-3, 5, -2,-1,2,6,-2};
		//System.out.println(maxSumRec(a, 0, a.length - 1));
		/**
		 * 插入排序
		 */
	 
          
	 
		String str = "1=2=3=4";
		System.out.println(str.split("=").length);
		for(int i=0;i<4;i++){
			System.out.println(str.split("=")[i]);
		}
		
	}
	
	public  <E> void parray(E [] arr){
		
	}
	private void bb(List<String> aa){
		aa.add("cc");
	}

	/**
	 * 插入排序
	 * 
	 */
	public static Integer [] insetionSort(Integer a []){
		
		for(int i=1;i<a.length;i++){
			int key = a[i];
			int j = i-1;
			while(j>-1&&a[j]>key){
				a[j+1]=a[j];
				j = j-1;
			}
			a[j+1]=key;
			
			
		}
		return a;
	}
}
