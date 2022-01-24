package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.axelor.apps.account.db.InvoiceLine;

public class InvoiceServiceImpl implements InvoiceService {
	@Override
	public List<BigDecimal> setInvoiceNetAmounts(List<InvoiceLine> invoiceLines, boolean inAti) {
		BigDecimal netAmount = new BigDecimal("0");
		BigDecimal netIGST = new BigDecimal("0");
		BigDecimal netCGST = new BigDecimal("0");
		BigDecimal netSGST = new BigDecimal("0");
		BigDecimal grossAmount = new BigDecimal("0");

		for (InvoiceLine a : invoiceLines) {
			netSGST = netSGST.add(a.getSGST());
			netCGST = netCGST.add(a.getCGST());
			netIGST = netIGST.add(a.getIGST());

			if (inAti)
				netAmount = netAmount.add(a.getInTaxTotal());
			else
				netAmount = netAmount.add(a.getExTaxTotal());

			grossAmount = grossAmount.add(a.getGrossAmount());
		}
		List<BigDecimal> netValues = Arrays.asList(netAmount, netIGST, netCGST, netSGST, grossAmount);
		return netValues;

	}
}
