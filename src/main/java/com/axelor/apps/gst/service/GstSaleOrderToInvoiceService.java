package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.db.City;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class GstSaleOrderToInvoiceService extends SaleOrderInvoiceProjectServiceImpl {
	
	@Inject
	public GstSaleOrderToInvoiceService(AppBaseService appBaseService, AppSupplychainService appSupplychainService,
			SaleOrderRepository saleOrderRepo, InvoiceRepository invoiceRepo, InvoiceService invoiceService,
			AppBusinessProjectService appBusinessProjectService, StockMoveRepository stockMoveRepository,
			SaleOrderLineService saleOrderLineService, SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
		super(appBaseService, appSupplychainService, saleOrderRepo, invoiceRepo, invoiceService, appBusinessProjectService,
				stockMoveRepository, saleOrderLineService, saleOrderWorkflowServiceImpl);
		}
	
	
	@Override
	public Invoice createInvoice(SaleOrder saleOrder) throws AxelorException {
		super.createInvoice(saleOrder);
		return this.gstCreateInvoice(saleOrder);
	}
	@Transactional
	public Invoice gstCreateInvoice(SaleOrder saleOrder) throws AxelorException{
		Invoice invoice = super.createInvoice(saleOrder);
		City companyCity = invoice.getCompany().getAddress().getCity();
		City invoiceCity = invoice.getAddress().getCity();
		boolean isCitySame = companyCity.equals(invoiceCity);
		List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();
		
		BigDecimal netIGST = BigDecimal.ZERO;
		BigDecimal netSGST = BigDecimal.ZERO;
		BigDecimal netCGST = BigDecimal.ZERO;
		BigDecimal grossAmount = BigDecimal.ZERO;
		
		
		for(InvoiceLine line : invoiceLines) {
			InvoiceLineRepository linerepo = Beans.get(InvoiceLineRepository.class);
			line.setGstRate(line.getProduct().getGstRate());
			line.setHsbn(line.getProduct().getHsbn());
			if(invoice.getInAti()) {
				if(isCitySame) {
					line.setSGST((line.getInTaxTotal().multiply(line.getGstRate())).divide(new BigDecimal(100)).divide(new BigDecimal(2)));
					line.setCGST(line.getSGST());
					line.setGrossAmount(line.getCGST().add(line.getSGST()).add(line.getInTaxTotal()));
				}
				else {
					line.setIGST((line.getInTaxTotal().multiply(line.getGstRate())).divide(new BigDecimal(100)));
					line.setGrossAmount(line.getIGST().add(line.getInTaxTotal()));
				}
			}
			else {
				if(isCitySame) {
					line.setSGST((line.getExTaxTotal().multiply(line.getGstRate())).divide(new BigDecimal(100)).divide(new BigDecimal(2)));
					line.setCGST(line.getSGST());
					line.setGrossAmount(line.getCGST().add(line.getSGST()).add(line.getExTaxTotal()));
				}
				else {
					line.setIGST((line.getExTaxTotal().multiply(line.getGstRate())).divide(new BigDecimal(100)));
					line.setGrossAmount(line.getIGST().add(line.getExTaxTotal()));
				}
			}
			netIGST = netIGST.add(line.getIGST());
			netSGST = netSGST.add(line.getSGST());
			netCGST = netCGST.add(line.getCGST());
			grossAmount = grossAmount.add(line.getGrossAmount());
			linerepo.save(line);
			}
		invoice.setNetIgst(netIGST);
		invoice.setNetCsgt(netCGST);
		invoice.setNetSgst(netSGST);
		invoice.setGrossAmount(grossAmount);
		return invoice;
	}
}
