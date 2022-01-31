package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.gst.service.GstInvoieReportPrintService;
import com.axelor.apps.gst.service.GstSaleOrderLineServiceImpl;
import com.axelor.apps.gst.service.GstSaleOrderServiceImpl;
import com.axelor.apps.gst.service.GstSaleOrderToInvoiceService;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceLineServiceImpl;
//import com.axelor.apps.gst.service.InvoicePrintService;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.gst.service.InvoiceServiceImpl;
import com.axelor.apps.gst.service.ProductService;
import com.axelor.apps.gst.service.ProductServiceImpl;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;

public class GstModule extends AxelorModule {

	@Override
	protected void configure() {
		bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
		bind(InvoiceService.class).to(InvoiceServiceImpl.class);
		bind(ProductService.class).to(ProductServiceImpl.class);
//		bind(InvoicePrintServiceImpl.class).to(InvoicePrintService.class);
		bind(SaleOrderLineBusinessProductionServiceImpl.class).to(GstSaleOrderLineServiceImpl.class);
		bind(SaleOrderComputeServiceSupplychainImpl.class).to(GstSaleOrderServiceImpl.class);
		bind(SaleOrderInvoiceProjectServiceImpl.class).to(GstSaleOrderToInvoiceService.class);
		bind(InvoicePrintServiceImpl.class).to(GstInvoieReportPrintService.class);
	}

}
