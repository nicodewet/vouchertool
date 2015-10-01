
package com.vouchertool.vouchserv.schemas;

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
 *         &lt;element name="resultStatusCode" type="{http://vouchertool.com/vouchserv/schemas}ResultStatusCode"/>
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
    "resultStatusCode"
})
@XmlRootElement(name = "RegisterResponse")
public class RegisterResponse {

    @XmlElement(required = true)
    protected String vsvID;
    @XmlElement(required = true)
    protected String resultStatusCode;

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

}
