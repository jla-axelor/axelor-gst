package com.axelor.apps.gst.service;

import java.util.List;

import com.axelor.apps.account.db.InvoiceLine;

public interface ProductService {
	public List<InvoiceLine> setGstInvoiceproduct(List<Long> productids);
}
