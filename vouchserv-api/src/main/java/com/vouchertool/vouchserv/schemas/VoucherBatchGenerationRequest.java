
package com.vouchertool.vouchserv.schemas;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="voucherNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="batchType" type="{http://vouchertool.com/vouchserv/schemas}VoucherBatchType"/>
 *         &lt;element name="pinLength" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;minInclusive value="6"/>
 *               &lt;maxInclusive value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="pinType" type="{http://vouchertool.com/vouchserv/schemas}PinType" minOccurs="0"/>
 *         &lt;element name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
    "voucherNumber",
    "batchType",
    "pinLength",
    "pinType",
    "expiryDate"
})
@XmlRootElement(name = "VoucherBatchGenerationRequest")
public class VoucherBatchGenerationRequest {

    @XmlElement(required = true)
    protected String vsvID;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger voucherNumber;
    @XmlElement(required = true)
    protected VoucherBatchType batchType;
    protected Integer pinLength;
    protected PinType pinType;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar expiryDate;

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
     * Gets the value of the voucherNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * Sets the value of the voucherNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVoucherNumber(BigInteger value) {
        this.voucherNumber = value;
    }

    /**
     * Gets the value of the batchType property.
     * 
     * @return
     *     possible object is
     *     {@link VoucherBatchType }
     *     
     */
    public VoucherBatchType getBatchType() {
        return batchType;
    }

    /**
     * Sets the value of the batchType property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoucherBatchType }
     *     
     */
    public void setBatchType(VoucherBatchType value) {
        this.batchType = value;
    }

    /**
     * Gets the value of the pinLength property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPinLength() {
        return pinLength;
    }

    /**
     * Sets the value of the pinLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPinLength(Integer value) {
        this.pinLength = value;
    }

    /**
     * Gets the value of the pinType property.
     * 
     * @return
     *     possible object is
     *     {@link PinType }
     *     
     */
    public PinType getPinType() {
        return pinType;
    }

    /**
     * Sets the value of the pinType property.
     * 
     * @param value
     *     allowed object is
     *     {@link PinType }
     *     
     */
    public void setPinType(PinType value) {
        this.pinType = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

}
