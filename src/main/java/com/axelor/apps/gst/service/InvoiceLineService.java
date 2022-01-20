package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.InvoiceLine;


public interface InvoiceLineService {
	public List<BigDecimal> setAmount(BigDecimal netAmount , BigDecimal gstRate ,String invoiceAddressStateName,String companyAddressStateName);
	public void setAmountOnchagePartner(List<InvoiceLine> lines ,String invoiceAddressStateName,String companyAddressStateName);

}
