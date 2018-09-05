package com.how2java.tmall.comparator;

import java.util.Comparator;

import com.how2java.tmall.pojo.Product;
//�����Ƚ��� ������������ķ�ǰ��
public class ProductReviewComparator implements Comparator<Product>{

	@Override
	public int compare(Product p1, Product p2) {
		return p2.getReviewCount()-p1.getReviewCount();
	}

}
