package com.how2java.tmall.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;

import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.util.ImageUtil;



public class ProductImageAction extends Action4Result {

	@Action("admin_productImage_list")
	public String list() {
//		根据product和类型type_single获取productSingleImages集合
		productSingleImages = productImageService.list("product",product,"type", ProductImageService.type_single);
//		根据product和类型type_detail 获取productDetailImages集合
		productDetailImages = productImageService.list("product",product,"type", ProductImageService.type_detail);
		t2p(product);
		return "listProductImage";
	}

	@Action("admin_productImage_add")
	public String add() {
		productImageService.save(productImage);
//		根据productImage的type字段值，确定文件存放的目录名称
		String folder = "img/";
		if(ProductImageService.type_single.equals(productImage.getType())){
			folder +="productSingle";
		}
		else{
			folder +="productDetail";
		}
//		根据ServletActionContext.getServletContext().getRealPath(folder) 定位硬盘上的真正位置
		File  imageFolder= new File(ServletActionContext.getServletContext().getRealPath(folder));
//		根据插入到数据库之后，productImage的id，确定文件名称
		File file = new File(imageFolder,productImage.getId()+".jpg");
		String fileName = file.getName();
		try {
//			通过FileUtils.copyFile把这个临时文件复制到文件绝对路径
			FileUtils.copyFile(img, file);
	        BufferedImage img = ImageUtil.change2jpg(file);
//	        通过ImageUtil确保图片的真正格式是jpg
	        ImageIO.write(img, "jpg", file);			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
//		如果图片的类型是type_single,借助ImageUtil.resizeImage把正常大小的图片，改变大小之后，分别复制到productSingle_middle和productSingle_small目录下
        if(ProductImageService.type_single.equals(productImage.getType())){
        	String imageFolder_small= ServletActionContext.getServletContext().getRealPath("img/productSingle_small");
        	String imageFolder_middle= ServletActionContext.getServletContext().getRealPath("img/productSingle_middle");		
        	File f_small = new File(imageFolder_small, fileName);
        	File f_middle = new File(imageFolder_middle, fileName);
        	f_small.getParentFile().mkdirs();
        	f_middle.getParentFile().mkdirs();
        	ImageUtil.resizeImage(file, 56, 56, f_small);
        	ImageUtil.resizeImage(file, 217, 190, f_middle);
        }		
				
		return "listProductImagePage";
	}
	
	@Action("admin_productImage_delete")
	public String delete() {
		t2p(productImage);
		productService.delete(productImage);
		return "listProductImagePage";
	}
}
