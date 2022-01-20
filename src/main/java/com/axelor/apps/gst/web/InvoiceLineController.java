package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {
	
	public void setAmounts(ActionRequest req, ActionResponse res) {
		Invoice invoice = req.getContext().getParent().asType(Invoice.class);
		InvoiceLine invoiceLine = req.getContext().asType(InvoiceLine.class);
		try {
			
		Company invoiceCompany = invoice.getCompany();
		
		Address invoiceAddress = invoice.getAddress();
		Address companyAddress = invoiceCompany.getAddress();
		
		if (!invoiceAddress.equals(null) && !companyAddress.equals(null)) {
			String invoiceAddressCity = invoiceAddress.getCity().getName();
			String companyAddressCity = companyAddress.getCity().getName();
			BigDecimal netAmount;
			
			if (invoice.getInAti()) {
				netAmount = invoiceLine.getInTaxPrice();
			}
			else {
				netAmount = invoiceLine.getExTaxTotal();
			}
			
			BigDecimal gstRate = invoiceLine.getGstRate();
			
			List<BigDecimal> amounts = Beans.get(InvoiceLineService.class).setAmount(netAmount, gstRate, invoiceAddressCity, companyAddressCity);
			
			if (!invoiceAddressCity.equals(companyAddressCity)) {
				BigDecimal igst = amounts.get(0);
				BigDecimal grossAmount = amounts.get(1);
				res.setValue("IGST", igst);
				res.setValue("grossAmount", grossAmount);
			}
			else {
				BigDecimal sgstAndCgst = amounts.get(0);
				BigDecimal grossAmount = amounts.get(1);
				res.setValue("SGST", sgstAndCgst);
				res.setValue("CGST", sgstAndCgst);
				res.setValue("grossAmount", grossAmount);
			}
		}
		}catch (NullPointerException e) {
			res.setError("City of invoice or company address should not be empty");
		}
	}

}
