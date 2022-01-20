package com.axelor.apps.gst.web;

import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.gst.service.InvoiceLineService;
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

//			setInvoiceNet(req, res);
				}
			} catch (NullPointerException e) {
				res.setError("City of invoice or company address should not be empty");
			}
		}
	}
}
