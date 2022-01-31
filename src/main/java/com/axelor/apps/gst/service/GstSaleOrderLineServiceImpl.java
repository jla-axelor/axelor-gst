package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.db.City;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductMultipleQtyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderLineRepository;
import com.axelor.apps.sale.service.app.AppSaleService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class GstSaleOrderLineServiceImpl extends SaleOrderLineBusinessProductionServiceImpl {

	@Inject
	public GstSaleOrderLineServiceImpl(CurrencyService currencyService, PriceListService priceListService,
			ProductMultipleQtyService productMultipleQtyService, AppBaseService appBaseService,
			AppSaleService appSaleService, AccountManagementService accountManagementService,
			SaleOrderLineRepository saleOrderLineRepo, AppAccountService appAccountService,
			AnalyticMoveLineService analyticMoveLineService, AppSupplychainService appSupplychainService) {
		super(currencyService, priceListService, productMultipleQtyService, appBaseService, appSaleService,
				accountManagementService, saleOrderLineRepo, appAccountService, analyticMoveLineService,
				appSupplychainService);
	}

	@Override
	public void computeProductInformation(SaleOrderLine saleOrderLine, SaleOrder saleOrder) throws AxelorException {
		super.computeProductInformation(saleOrderLine, saleOrder);
		if (appBaseService.isApp("gst")) {
			this.computeGstInfo(saleOrderLine);
		}
	}

	@Override
	public Map<String, BigDecimal> computeValues(SaleOrder saleOrder, SaleOrderLine saleOrderLine)
			throws AxelorException {
		if (appBaseService.isApp("gst")) {
			super.computeValues(saleOrder, saleOrderLine);
			return this.computeGstAmount(saleOrder, saleOrderLine);
		} else {
			return super.computeValues(saleOrder, saleOrderLine);
		}
	}

	public SaleOrderLine computeGstInfo(SaleOrderLine saleOrderLine) {
		saleOrderLine.setHsbn(saleOrderLine.getProduct().getHsbn());
		saleOrderLine.setGstRate(saleOrderLine.getProduct().getGstRate());
		return saleOrderLine;
	}

	public Map<String, BigDecimal> computeGstAmount(SaleOrder saleOrder, SaleOrderLine saleOrderLine) {
		boolean inAti = saleOrder.getInAti();
		BigDecimal igst;
		BigDecimal sgst;
		BigDecimal cgst;
		BigDecimal gross_amount;
		BigDecimal netAmount;
		HashMap<String, BigDecimal> map = new HashMap<>();
		if (inAti) {
			netAmount = saleOrderLine.getInTaxTotal();
		} else {
			netAmount = saleOrderLine.getExTaxTotal();
		}
		try {
			City invoiceAddressCity = saleOrder.getMainInvoicingAddress().getCity();
			City compamyAddressCity = saleOrder.getCompany().getAddress().getCity();

			if (!invoiceAddressCity.equals(null) && !compamyAddressCity.equals(null)) {
				if (invoiceAddressCity.equals(compamyAddressCity)) {
					sgst = netAmount.multiply(saleOrderLine.getGstRate()).divide(new BigDecimal(2))
							.divide(new BigDecimal(100));
					cgst = sgst;
					gross_amount = netAmount.add(sgst).add(cgst);
					saleOrderLine.setSGST(sgst);
					saleOrderLine.setCGST(cgst);
					saleOrderLine.setGrossAmount(gross_amount);
					map.put("SGST", sgst);
					map.put("CGST", cgst);
					map.put("grossAmount", gross_amount);
				} else {
					igst = netAmount.multiply(saleOrderLine.getGstRate()).divide(new BigDecimal(100));
					gross_amount = netAmount.add(igst);
					saleOrderLine.setIGST(igst);
					saleOrderLine.setGrossAmount(gross_amount);
					map.put("IGST", igst);
					map.put("grossAmount", gross_amount);
				}
			}
		} catch (NullPointerException e) {
			System.err.println("City is empty");
		}
		return map;
	}
}
