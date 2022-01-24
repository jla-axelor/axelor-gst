package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineServiceImpl;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.gst.service.InvoiceServiceImpl;
import com.axelor.apps.gst.service.ProductService;
import com.axelor.apps.gst.service.ProductServiceImpl;

public class GstModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
		bind(InvoiceService.class).to(InvoiceServiceImpl.class);
		bind(ProductService.class).to(ProductServiceImpl.class);
	}

}
