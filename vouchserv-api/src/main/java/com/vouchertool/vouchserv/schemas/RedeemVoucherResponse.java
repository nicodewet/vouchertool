
package com.vouchertool.vouchserv.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
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
 *         &lt;element name="resultStatusCode" type="{http://vouchertool.com/vouchserv/schemas}ResultStatusCode"/>
 *         &lt;element name="redeemVoucherErrorCode" type="{http://vouchertool.com/vouchserv/schemas}RedeemVoucherErrorCode" minOccurs="0"/>
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
    "resultStatusCode",
    "redeemVoucherErrorCode"
})
@XmlRootElement(name = "RedeemVoucherResponse")
public class RedeemVoucherResponse {

    @XmlElement(required = true)
    protected String resultStatusCode;
    @XmlList
    protected List<String> redeemVoucherErrorCode;

    /**
     * Gets the value of the resultStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultStatusCode() {
        return resultStatusCode;
    }

    /**
     * Sets the value of the resultStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultStatusCode(String value) {
        this.resultStatusCode = value;
    }

    /**
     * Gets the value of the redeemVoucherErrorCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the redeemVoucherErrorCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRedeemVoucherErrorCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRedeemVoucherErrorCode() {
        if (redeemVoucherErrorCode == null) {
            redeemVoucherErrorCode = new ArrayList<String>();
        }
        return this.redeemVoucherErrorCode;
    }

}
