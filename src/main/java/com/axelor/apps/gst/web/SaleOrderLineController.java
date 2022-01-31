package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.axelor.apps.base.db.Address;
import com.axelor.apps.base.db.Company;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.base.db.PartnerAddress;
import com.axelor.apps.gst.service.InvoiceLineService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class SaleOrderLineController {
	public void setAmounts(ActionRequest req, ActionResponse res) {
		SaleOrder saleOrder = req.getContext().getParent().asType(SaleOrder.class);
		SaleOrderLine saleOrderLine = req.getContext().asType(SaleOrderLine.class);
		try {
			Company company = saleOrder.getCompany();
			Partner partner = saleOrder.getClientPartner();
			
			List<PartnerAddress> partnerAddresses = partner.getPartnerAddressList().stream().filter(i->i.getIsDefaultAddr().equals(true)).collect(Collectors.toList());
			Address partnerAddress = null;
			if(!partnerAddresses.isEmpty()) {
				partnerAddress = partnerAddresses.get(0).getAddress();
			}
			if(!company.getAddress().equals(null) && !partnerAddress.equals(null)) {
				String partnerAddressCity = partnerAddress.getCity().getName();
				String companyAddressCity = company.getAddress().getCity().getName();
				BigDecimal netAmount;
				
				if (saleOrder.getInAti()) {
					netAmount = saleOrderLine.getInTaxPrice();
				}
				else {
					netAmount = saleOrderLine.getExTaxTotal();
				}
				
				BigDecimal gstRate = saleOrderLine.getGstRate();
				
				List<BigDecimal> amounts = Beans.get(InvoiceLineService.class).setAmount(netAmount, gstRate, partnerAddressCity, companyAddressCity);
				
				if (!partnerAddressCity.equals(companyAddressCity)) {
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
			res.setError("City of Client partner or company address should not be empty");
		}
		
	}		
}
