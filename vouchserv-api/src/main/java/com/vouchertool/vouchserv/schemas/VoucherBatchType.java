
package com.vouchertool.vouchserv.schemas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VoucherBatchType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VoucherBatchType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REGULAR"/>
 *     &lt;enumeration value="JUST_IN_TIME"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VoucherBatchType")
@XmlEnum
public enum VoucherBatchType {

    REGULAR,
    JUST_IN_TIME;

    public String value() {
        return name();
    }

    public static VoucherBatchType fromValue(String v) {
        return valueOf(v);
    }

}
