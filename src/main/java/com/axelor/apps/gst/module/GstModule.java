package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineServiceImpl;

public class GstModule extends AxelorModule{
	
	@Override
	protected void configure() {
		bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
	}

}
