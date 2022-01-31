package com.axelor.apps.gst.service;

import java.math.BigDecimal;

import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineTaxService;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class GstSaleOrderServiceImpl extends SaleOrderComputeServiceSupplychainImpl{
	
	@Inject
	private AppBaseService appBaseService;
	
	@Inject
	public GstSaleOrderServiceImpl(SaleOrderLineService saleOrderLineService,
			SaleOrderLineTaxService saleOrderLineTaxService) {
		super(saleOrderLineService, saleOrderLineTaxService);
	}
	
	@Override
	public void _computeSaleOrder(SaleOrder saleOrder) throws AxelorException {
		super._computeSaleOrder(saleOrder);
		if(appBaseService.isApp("gst")) {
			this.computeGst(saleOrder);
		}
	}
	
	public SaleOrder computeGst(SaleOrder saleOrder) {
		BigDecimal netIGST = BigDecimal.ZERO;
		BigDecimal netSGST= BigDecimal.ZERO;
		BigDecimal netCGST= BigDecimal.ZERO;
		BigDecimal grossAmount= BigDecimal.ZERO;
		
		if(!saleOrder.getSaleOrderLineList().isEmpty()) {
		for(SaleOrderLine line : saleOrder.getSaleOrderLineList()) {
			netIGST = netIGST.add(line.getIGST());
			netSGST = netSGST.add(line.getSGST());
			netCGST = netCGST.add(line.getCGST());
			grossAmount = grossAmount.add(line.getGrossAmount());
		}
		saleOrder.setNetIgst(netIGST);
		saleOrder.setNetSgst(netSGST);
		saleOrder.setNetCsgt(netCGST);
		saleOrder.setGrossAmount(grossAmount);
		return saleOrder;
		}
		return saleOrder;
	}
}
