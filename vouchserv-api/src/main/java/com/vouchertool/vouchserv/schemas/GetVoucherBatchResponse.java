
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
 *         &lt;element name="voucherBatch" type="{http://vouchertool.com/vouchserv/schemas}VoucherBatch" minOccurs="0"/>
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
    "voucherBatch"
})
@XmlRootElement(name = "GetVoucherBatchResponse")
public class GetVoucherBatchResponse {

    protected VoucherBatch voucherBatch;

    /**
     * Gets the value of the voucherBatch property.
     * 
     * @return
     *     possible object is
     *     {@link VoucherBatch }
     *     
     */
    public VoucherBatch getVoucherBatch() {
        return voucherBatch;
    }

    /**
     * Sets the value of the voucherBatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoucherBatch }
     *     
     */
    public void setVoucherBatch(VoucherBatch value) {
        this.voucherBatch = value;
    }

}
