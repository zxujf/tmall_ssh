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
//		����product������type_single��ȡproductSingleImages����
		productSingleImages = productImageService.list("product",product,"type", ProductImageService.type_single);
//		����product������type_detail ��ȡproductDetailImages����
		productDetailImages = productImageService.list("product",product,"type", ProductImageService.type_detail);
		t2p(product);
		return "listProductImage";
	}

	@Action("admin_productImage_add")
	public String add() {
		productImageService.save(productImage);
//		����productImage��type�ֶ�ֵ��ȷ���ļ���ŵ�Ŀ¼����
		String folder = "img/";
		if(ProductImageService.type_single.equals(productImage.getType())){
			folder +="productSingle";
		}
		else{
			folder +="productDetail";
		}
//		����ServletActionContext.getServletContext().getRealPath(folder) ��λӲ���ϵ�����λ��
		File  imageFolder= new File(ServletActionContext.getServletContext().getRealPath(folder));
//		���ݲ��뵽���ݿ�֮��productImage��id��ȷ���ļ�����
		File file = new File(imageFolder,productImage.getId()+".jpg");
		String fileName = file.getName();
		try {
//			ͨ��FileUtils.copyFile�������ʱ�ļ����Ƶ��ļ�����·��
			FileUtils.copyFile(img, file);
	        BufferedImage img = ImageUtil.change2jpg(file);
//	        ͨ��ImageUtilȷ��ͼƬ��������ʽ��jpg
	        ImageIO.write(img, "jpg", file);			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
//		���ͼƬ��������type_single,����ImageUtil.resizeImage��������С��ͼƬ���ı��С֮�󣬷ֱ��Ƶ�productSingle_middle��productSingle_smallĿ¼��
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
