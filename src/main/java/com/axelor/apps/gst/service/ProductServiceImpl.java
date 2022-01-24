package com.axelor.apps.gst.service;

import java.util.ArrayList;
import java.util.List;

import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.inject.Beans;

public class ProductServiceImpl implements ProductService {
	@Override
	public List<InvoiceLine> setGstInvoiceproduct(List<Long> productids) {
		List<InvoiceLine> lines = new ArrayList<InvoiceLine>();
		if(!productids.equals(null)) {
			for(Long i : productids) {
				Long l = new Long(i);	
				Product product = Beans.get(ProductRepository.class).find(l);
				InvoiceLine line = new InvoiceLine();
				line.setProduct(product);
				line.setGstRate(product.getGstRate());
				lines.add(line);
			}
		}
		return lines;
	}
}
