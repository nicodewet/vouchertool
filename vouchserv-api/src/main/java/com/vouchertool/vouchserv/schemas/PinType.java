
package com.vouchertool.vouchserv.schemas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PinType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PinType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NUMERIC"/>
 *     &lt;enumeration value="ALPHANUMERIC_UPPER_CASE"/>
 *     &lt;enumeration value="ALPHANUMERIC_MIXED_CASE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PinType")
@XmlEnum
public enum PinType {

    NUMERIC,
    ALPHANUMERIC_UPPER_CASE,
    ALPHANUMERIC_MIXED_CASE;

    public String value() {
        return name();
    }

    public static PinType fromValue(String v) {
        return valueOf(v);
    }

}
