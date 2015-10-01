package com.mayloom.vt;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * @author Nico
 */
public abstract class AbstractVoucherToolView extends CustomComponent {
	
	private VerticalLayout mainVerticalLayout;
	
	private Embedded embeddedLogo;
	
	private static final String VOUCHER_TOOL_ELEVATOR_PITCH = "<h3>Elevator Pitch</h3><p>VoucherTool is a value agnostic, enterprise voucher generation and management <a href=\"https://www.vouchertool.com/vouchserv/vouchserv.wsdl\">web service</a>." +
			" Great, but what does that mean? Why should you care?</p>" +
			"<p>Here are some outcomes that matter to VoucherTool web service users and how they are achieved:</p>" + 
			"<ul>" +
				"<li><b>Rapid development</b> - no need to burn man hours solving an engineering challenge that has already been solved, with the challenges coming in at large scale (millions of vouchers).</li>" +
				"<li><b>Performance</b> - a web service that can generate from one voucher up to batches of hundreds of millions of vouchers.</li>" +
				"<li><b>Domain specific, simple feature set</b> - applicable to a broad range of use cases.</li>" +
				"<li><b>Reliability</b> - being dependant on a well tested, bullet proof, telecommunications-grade component.</li>" +
				"<li><b>Cost</b> - no need to buy a milion dollar piece of software from a big name vendor.</li>" +
			"</ul>" +
			"<p>To illustrate use cases by example:" +
			"<ul>" +
				"<li>Mobile network operator generating and ditributing batches of hundreds of <b>millions of prepaid airtime vouchers</b></li>" +
				"<li>Prepaid electricity vendor vending <b>prepaid electicity vouchers</b></li>" +
				"<li>Fast moving consumer goods (e.g. chips) manufacturer creating a <b>product linked pin based competition</b></li>" +
				"<li>Bank generating <b>one-time pins</b></li>" +
			"</ul>" +
			"</p>" +
			"<h3>FAQ</h3>" +
			"<p>Please email <a href=\"mailto:nico@vouchertool.com\">Nico de Wet</a> if you have any questions.</p>" + 
			"<h3>Background</h3>" +
			"<p>VoucherTool is a <a href=\"http://www.mayloom.com\">MAYLOOM</a> product developed by <a href=\"http://www.nicodewet.com\">Nico de Wet</a> since April 2011. Nico has worked on voucher management software from 2006 onwards, ranging from small " +
			"scale operations in the Internet Service Provider space, to large scale voucher management in the telecommunications space, or enterprise space, with millions of dollars worth" +
			" of vouchers being generated and managed.</p>";
	
	private static final String VOUCHER_TOOL_CHANGE_LOG="<h3>Release Notes</h3>" +
			"<p>" +
			"<h4>1.16</h4>"+
			"<ul>" +
				"<li>Web interface: bug fix, Explore button associated with API Keys functional with multiple generated batches</li>" +
			"</ul>" +
			"<h4>1.15</h4>"+
			"<ul>" +
				"<li>Web interface: API keys, each representing unique voucher space, can be explored to view voucher batches</li>" +
				"<li>Web interface: user browser time zone displayed, web service dates however are UTC dates</li>" +
			"</ul>" +
			"<h4>1.14</h4>"+
			"<ul>" +
				"<li>Web interface: release notes pop-up added</li>" +
				"<li>Core generator: robustness enhancement, asynchronous generation jobs continue after restart</li>" +
				"<li>SOAP web service: bug fix in SOAP web service, asynchronous generation now functional</li>" +
			"</ul>" +
			
			"</p>";
	
	Panel buildMainLayoutPanel() {
		
		Panel mainLayoutPanel = new Panel(); 
		
		// common part: create layout
		mainVerticalLayout = new VerticalLayout();
		mainLayoutPanel.setContent(mainVerticalLayout);
		mainLayoutPanel.setSizeFull();
		mainLayoutPanel.setScrollable(true);
		
		mainVerticalLayout.addStyleName(Reindeer.LAYOUT_WHITE);
		mainVerticalLayout.setImmediate(false);
		mainVerticalLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		
		HorizontalLayout topHorizontalLayout = new HorizontalLayout();
		topHorizontalLayout.setWidth("100.0%");
		topHorizontalLayout.setMargin(true);
		embeddedLogo = new Embedded();
		embeddedLogo.setImmediate(false);
		embeddedLogo.setWidth("-1px");
		embeddedLogo.setHeight("-1px");
		embeddedLogo.setSource(new ThemeResource("img/mayloom.png"));
		embeddedLogo.setType(1);
		embeddedLogo.setMimeType("image/png");
		embeddedLogo.setDescription("http://www.mayloom.com");
		topHorizontalLayout.addComponent(embeddedLogo);
		topHorizontalLayout.setComponentAlignment(embeddedLogo, Alignment.TOP_LEFT);
		
		Component topRightComponent = createTopRightComponent();
		topHorizontalLayout.addComponent(topRightComponent);
		topHorizontalLayout.setComponentAlignment(topRightComponent, Alignment.TOP_RIGHT);
		mainVerticalLayout.addComponent(topHorizontalLayout);	// TODO want to do this
		mainVerticalLayout.setComponentAlignment(topHorizontalLayout, Alignment.TOP_CENTER);
		
		Component middleCenterComponent = createMiddleCenterComponent();		
		mainVerticalLayout.addComponent(middleCenterComponent);
		mainVerticalLayout.setComponentAlignment(middleCenterComponent, Alignment.MIDDLE_CENTER);
		
		HorizontalLayout bottomHorizontalLayout = new HorizontalLayout();
		bottomHorizontalLayout.setWidth("100.0%");
		bottomHorizontalLayout.setMargin(true);
		
		Label versionPopUpContent = new Label(VOUCHER_TOOL_CHANGE_LOG);
		versionPopUpContent.setContentMode(Label.CONTENT_XHTML);
		versionPopUpContent.setWidth("500px");
		PopupView versionPopupView = new PopupView("VoucherTool " + VoucherTool.VOUCHER_TOOL_VERSION, versionPopUpContent);
        versionPopupView.setHideOnMouseOut(true);
		bottomHorizontalLayout.addComponent(versionPopupView);
		
		mainVerticalLayout.addComponent(bottomHorizontalLayout);
		
		Label popUpContent = new Label(VOUCHER_TOOL_ELEVATOR_PITCH);
		popUpContent.setContentMode(Label.CONTENT_XHTML);
	    popUpContent.setWidth("500px");
	    PopupView popupView = new PopupView("About VoucherTool", popUpContent);
        popupView.setHideOnMouseOut(true);
        popupView.setHeight("10%");
	           
        bottomHorizontalLayout.addComponent(popupView);
		
		return mainLayoutPanel;
		
	}
	
	/**
	 * Create a component, that will generally be a navigation component (e.g. button) that 
	 * will be placed in the top right. 
	 * 
	 * @return A Component that you wish to have in the top right of the screen. Must not be null.
	 */
	abstract Component createTopRightComponent();
	
	/**
	 * Create a component, that will generally be the primary component (e.g. a form) that will be 
	 * placed in the middle center of the screen.
	 * 
	 * @return A Component that you wish to have in the middle center of the screen. Must not be null.
	 */
	abstract Component createMiddleCenterComponent();
}
