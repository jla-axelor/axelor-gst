package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.InvoiceLine;

public interface InvoiceService {
	public List<BigDecimal> setInvoiceNetAmounts(List<InvoiceLine> invoiceLines,boolean inAti);
}
