
package com.vouchertool.vouchserv.schemas;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="vsvID" type="{http://vouchertool.com/vouchserv/schemas}VoucherServiceIdType"/>
 *         &lt;element name="batchNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
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
    "vsvID",
    "batchNumber"
})
@XmlRootElement(name = "ActivateVoucherBatchRequest")
public class ActivateVoucherBatchRequest {

    @XmlElement(required = true)
    protected String vsvID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger batchNumber;

    /**
     * Gets the value of the vsvID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVsvID() {
        return vsvID;
    }

    /**
     * Sets the value of the vsvID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVsvID(String value) {
        this.vsvID = value;
    }

    /**
     * Gets the value of the batchNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBatchNumber() {
        return batchNumber;
    }

    /**
     * Sets the value of the batchNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBatchNumber(BigInteger value) {
        this.batchNumber = value;
    }

}
