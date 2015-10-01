
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
 *         &lt;element name="batchNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="resultStatusCode" type="{http://vouchertool.com/vouchserv/schemas}ResultStatusCode"/>
 *         &lt;element name="batchGenerationErrorList">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="batchGenerationErrorCode" type="{http://vouchertool.com/vouchserv/schemas}BatchGenerationErrorCode"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "batchNumber",
    "resultStatusCode",
    "batchGenerationErrorList"
})
@XmlRootElement(name = "VoucherBatchGenerationResponse")
public class VoucherBatchGenerationResponse {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger batchNumber;
    @XmlElement(required = true)
    protected String resultStatusCode;
    @XmlElement(required = true)
    protected VoucherBatchGenerationResponse.BatchGenerationErrorList batchGenerationErrorList;

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
     * Gets the value of the batchGenerationErrorList property.
     * 
     * @return
     *     possible object is
     *     {@link VoucherBatchGenerationResponse.BatchGenerationErrorList }
     *     
     */
    public VoucherBatchGenerationResponse.BatchGenerationErrorList getBatchGenerationErrorList() {
        return batchGenerationErrorList;
    }

    /**
     * Sets the value of the batchGenerationErrorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link VoucherBatchGenerationResponse.BatchGenerationErrorList }
     *     
     */
    public void setBatchGenerationErrorList(VoucherBatchGenerationResponse.BatchGenerationErrorList value) {
        this.batchGenerationErrorList = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="batchGenerationErrorCode" type="{http://vouchertool.com/vouchserv/schemas}BatchGenerationErrorCode"/>
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
        "batchGenerationErrorCode"
    })
    public static class BatchGenerationErrorList {

        protected String batchGenerationErrorCode;

        /**
         * Gets the value of the batchGenerationErrorCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBatchGenerationErrorCode() {
            return batchGenerationErrorCode;
        }

        /**
         * Sets the value of the batchGenerationErrorCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBatchGenerationErrorCode(String value) {
            this.batchGenerationErrorCode = value;
        }

    }

}
