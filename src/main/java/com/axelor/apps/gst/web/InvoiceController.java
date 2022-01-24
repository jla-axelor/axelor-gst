package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.gst.service.InvoiceService;
import com.axelor.apps.gst.service.ProductService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceController {

	public void setInvoiceItemsValues(ActionRequest req, ActionResponse res) {

		Invoice invoice = req.getContext().asType(Invoice.class);
		List<InvoiceLine> lines = invoice.getInvoiceLineList();
		if (!lines.isEmpty()) {

			try {

				Company invoiceCompany = invoice.getCompany();

				Address invoiceAddress = invoice.getAddress();
				Address companyAddress = invoiceCompany.getAddress();

				if (!invoiceAddress.equals(null) && !companyAddress.equals(null)) {
					String invoiceAddressCity = invoiceAddress.getCity().getName();
					String companyAddressCity = companyAddress.getCity().getName();

					if (!invoiceAddressCity.equals(null) && !companyAddressCity.equals(null)) {
						Beans.get(InvoiceLineService.class).setAmountOnchagePartner(lines, invoiceAddressCity,
								companyAddressCity);
					}

			setInvoiceNetAmounts(req, res);
				}
			} catch (NullPointerException e) {
				res.setError("City of invoice or company address should not be empty");
			}
		}
	}
	
	public void setInvoiceNetAmounts(ActionRequest req, ActionResponse res) {
		
		Invoice invoice = req.getContext().asType(Invoice.class);
		List<InvoiceLine> invoiceLine = invoice.getInvoiceLineList();
		boolean inAti = invoice.getInAti();
		
		List<BigDecimal> newValues = Beans.get(InvoiceService.class).setInvoiceNetAmounts(invoiceLine,inAti);
		
		res.setValue("netAmmount", newValues.get(0));
		res.setValue("netIgst", newValues.get(1));
		res.setValue("netCsgt", newValues.get(2));
		res.setValue("netSgst", newValues.get(3));
		res.setValue("grossAmount", newValues.get(4));
			
	}
	
	public void setSelectedProduct(ActionRequest req, ActionResponse res) {
		
		if (req.getContext().containsKey("_productIds")) {
			List<Long> productIds = (List<Long>) req.getContext().get("_productIds");
			List<InvoiceLine> lines = Beans.get(ProductService.class).setGstInvoiceproduct(productIds);
			res.setValue("invoiceItem", lines);
		}
	}
}
