package cn.mark.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.mark.o2o.dao.ProductCategoryDao;
import cn.mark.o2o.dao.ProductDao;
import cn.mark.o2o.dto.ProductCategoryExecution;
import cn.mark.o2o.entity.ProductCategory;
import cn.mark.o2o.enums.ProductCategoryStateEnum;
import cn.mark.o2o.exceptions.ProductCategoryOperationException;

@Service
public class ProductCategoryServiceImpl implements cn.mark.o2o.service.ProductCategoryService {

	@Autowired private ProductCategoryDao productCategoryDao;
	@Autowired private ProductDao productDao;
	
	@Override
	public List<ProductCategory> getProductCategoryList(long shopId) {
		
		return productCategoryDao.queryProductCategoryList(shopId);
	}

	@Override
	@Transactional
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryOperationException {
		if(productCategoryList != null && productCategoryList.size() > 0) {
			try {
				int effectedNum = productCategoryDao.batchInsertProductCategory(productCategoryList);
				if(effectedNum <= 0) {
					throw new ProductCategoryOperationException("商品类别创建失败！");
				} else {
					return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
				}
			} catch (Exception e) {
				throw new ProductCategoryOperationException("batchAddProductCategory error:" + e.getMessage());
			}
		} else {
			return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		}
		
	}

	@Override
	@Transactional
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId) throws ProductCategoryOperationException{
		//解除tb_product里的商品和该商品类别的关联
		try {
			int effectedNum = productDao.updateProductCategoryToNull(productCategoryId);
			if(effectedNum < 0) {
				throw new RuntimeException("商品类别更新失败！");
			}
		} catch (Exception e) {
			throw new RuntimeException("deleteProductCategory error： " + e.getMessage());
		}
		//删除该类别
		try {
			int effectedNum = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if(effectedNum <= 0) {
				throw new ProductCategoryOperationException("商品类别删除失败！");
			} else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		} catch (ProductCategoryOperationException e) {
			throw new ProductCategoryOperationException("deleteProductCategory error : " + e.getMessage());
		}
	}

}
