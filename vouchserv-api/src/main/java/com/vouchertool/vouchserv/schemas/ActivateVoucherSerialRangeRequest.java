
package com.vouchertool.vouchserv.schemas;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="vsvID" type="{http://vouchertool.com/vouchserv/schemas}VoucherServiceIdType"/>
 *         &lt;element name="startSerialNumber" type="{http://vouchertool.com/vouchserv/schemas}SerialNumberType"/>
 *         &lt;element name="endSerialNumber" type="{http://vouchertool.com/vouchserv/schemas}SerialNumberType"/>
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
    "startSerialNumber",
    "endSerialNumber"
})
@XmlRootElement(name = "ActivateVoucherSerialRangeRequest")
public class ActivateVoucherSerialRangeRequest {

    @XmlElement(required = true)
    protected String vsvID;
    @XmlElement(required = true)
    protected BigInteger startSerialNumber;
    @XmlElement(required = true)
    protected BigInteger endSerialNumber;

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
     * Gets the value of the startSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartSerialNumber() {
        return startSerialNumber;
    }

    /**
     * Sets the value of the startSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStartSerialNumber(BigInteger value) {
        this.startSerialNumber = value;
    }

    /**
     * Gets the value of the endSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEndSerialNumber() {
        return endSerialNumber;
    }

    /**
     * Sets the value of the endSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEndSerialNumber(BigInteger value) {
        this.endSerialNumber = value;
    }

}
