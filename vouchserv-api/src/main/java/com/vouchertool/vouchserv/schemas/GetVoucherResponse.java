
package com.vouchertool.vouchserv.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="voucher" type="{http://vouchertool.com/vouchserv/schemas}ContentEnricherVoucher"/>
 *           &lt;element name="errorCode" type="{http://vouchertool.com/vouchserv/schemas}VoucherReferenceErrorCode"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "voucher",
    "errorCode"
})
@XmlRootElement(name = "GetVoucherResponse")
public class GetVoucherResponse {

    protected ContentEnricherVoucher voucher;
    protected String errorCode;

    /**
     * Gets the value of the voucher property.
     * 
     * @return
     *     possible object is
     *     {@link ContentEnricherVoucher }
     *     
     */
    public ContentEnricherVoucher getVoucher() {
        return voucher;
    }

    /**
     * Sets the value of the voucher property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentEnricherVoucher }
     *     
     */
    public void setVoucher(ContentEnricherVoucher value) {
        this.voucher = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

}
