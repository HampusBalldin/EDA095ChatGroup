//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.22 at 02:08:47 AM CEST 
//


package org.violin.database.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Friend complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Friend">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uid_1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="uid_2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Friend", propOrder = {
    "uid1",
    "uid2"
})
public class Friend {

    @XmlElement(name = "uid_1", required = true)
    protected String uid1;
    @XmlElement(name = "uid_2", required = true)
    protected String uid2;

    /**
     * Gets the value of the uid1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid1() {
        return uid1;
    }

    /**
     * Sets the value of the uid1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid1(String value) {
        this.uid1 = value;
    }

    /**
     * Gets the value of the uid2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid2() {
        return uid2;
    }

    /**
     * Sets the value of the uid2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid2(String value) {
        this.uid2 = value;
    }

}
