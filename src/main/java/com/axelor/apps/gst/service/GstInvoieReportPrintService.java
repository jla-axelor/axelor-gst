package com.axelor.apps.gst.service;

import java.util.Optional;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.repo.AccountConfigRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.google.common.base.Strings;
import com.google.inject.Inject;

public class GstInvoieReportPrintService extends InvoicePrintServiceImpl  {

	@Inject
	public GstInvoieReportPrintService(InvoiceRepository invoiceRepo, AccountConfigRepository accountConfigRepo,
			AppBaseService appBaseService) {
		super(invoiceRepo, accountConfigRepo, appBaseService);
	}

	@Override
	public ReportSettings prepareReportSettings(Invoice invoice, Integer reportType, String format, String locale)
			throws AxelorException {
		super.prepareReportSettings(invoice, reportType, format, locale);
		return gstReportSetting(invoice, reportType, format, locale);
	}
	
	public ReportSettings gstReportSetting(Invoice invoice, Integer reportType, String format, String locale) throws AxelorException {
			if(Beans.get(AppBaseService.class).isApp("gst")) {
		    ReportSettings reportSetting =
		        ReportFactory.createReport(IReport.GST_INVOICE, "Gst Invoice");

		    if (Strings.isNullOrEmpty(locale)) {
		      String userLanguageCode =
		          Optional.ofNullable(AuthUtils.getUser()).map(User::getLanguage).orElse(null);
		      String companyLanguageCode =
		          invoice.getCompany().getLanguage() != null
		              ? invoice.getCompany().getLanguage().getCode()
		              : userLanguageCode;
		      String partnerLanguageCode =
		          invoice.getPartner().getLanguage() != null
		              ? invoice.getPartner().getLanguage().getCode()
		              : userLanguageCode;
		      locale =
		          accountConfigRepo
		                  .findByCompany(invoice.getCompany())
		                  .getIsPrintInvoicesInCompanyLanguage()
		              ? companyLanguageCode
		              : partnerLanguageCode;
		    }
		    String watermark = null;
		    if (accountConfigRepo.findByCompany(invoice.getCompany()).getInvoiceWatermark() != null) {
		      watermark =
		          MetaFiles.getPath(
		                  accountConfigRepo.findByCompany(invoice.getCompany()).getInvoiceWatermark())
		              .toString();
		    }

		    return reportSetting
		        .addParam("InvoiceId", invoice.getId())
		        .addParam("Locale", locale)
		        .addParam(
		            "Timezone", invoice.getCompany() != null ? invoice.getCompany().getTimezone() : null)
		        .addParam("ReportType", reportType == null ? 0 : reportType)
		        .addParam("HeaderHeight", invoice.getPrintingSettings().getPdfHeaderHeight())
		        .addParam("Watermark", watermark)
		        .addParam("FooterHeight", invoice.getPrintingSettings().getPdfFooterHeight())
		        .addFormat(format);
		  }
		else {
			return super.prepareReportSettings(invoice, reportType, format, locale);
		}
	}

	
	
	
}
