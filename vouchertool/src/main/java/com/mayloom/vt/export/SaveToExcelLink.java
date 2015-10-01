package com.mayloom.vt.export;

import com.mayloom.vouchserv.man.api.dto.Voucher;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Link;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class SaveToExcelLink extends Link {
	
	private List<Voucher> voucherList;
	private String vsvId;
	
	public void setGenerationResults(List<Voucher> voucherList, String vsvId) {
		if (voucherList == null || voucherList.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.voucherList = voucherList;
		this.vsvId = vsvId;
	}

    public SaveToExcelLink() {
        super();
        setCaption("Download Codes");
        setDescription("Download generated unique codes to local computer");
        setTargetName("_blank");
    }

    @Override
    public void attach() {
        super.attach(); // Must call.

        StreamResource.StreamSource source = new StreamResource.StreamSource() {

            public InputStream getStream() {
                byte[] b = null;
                String ppp = toExcelFormat(voucherList);
                b = ppp.getBytes();
                return new ByteArrayInputStream(b);
            }
        };

        String namefile = "VoucherTool_";
        if (vsvId != null && voucherList != null && voucherList.size() > 0) {
        	namefile = namefile + voucherList.size() + "_Unique_Codes_" + vsvId + ".csv";
        } else {
        	namefile = namefile + "Unique_Codes.csv";
        }
        StreamResource resource = new StreamResource(source, namefile, getApplication());
        resource.getStream().setParameter("Content-Disposition", "attachment;filename=\"" + namefile + "\"");
        resource.setMIMEType("text/csv");
        resource.setCacheTime(0);
        setResource(resource);
    }
    
    String toExcelFormat(List<Voucher> voucherList) {
    	if (voucherList != null && voucherList.size() > 0) {
    		StringBuilder sb = new StringBuilder();
        	//sb.append("<table>");
        	for (Voucher voucher: voucherList) {
        		//sb.append("<tr><td><span>");
        		sb.append("\"");
        		sb.append(voucher.getPin());
        		sb.append("\"");
        		//sb.append("</span></td></tr>");
        		sb.append("," + System.lineSeparator());
        	}
        	//sb.append("</table>");
        	return sb.toString();
    	} else {
    		return "";
    	}
    	
    }
}