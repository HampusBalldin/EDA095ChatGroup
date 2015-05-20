//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.05.21 at 12:02:44 AM CEST 
//


package org.violin.database.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MessageType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Request_Receive_Data"/>
 *     &lt;enumeration value="Request_Send_Data"/>
 *     &lt;enumeration value="Login"/>
 *     &lt;enumeration value="Logout"/>
 *     &lt;enumeration value="GetFriends"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MessageType")
@XmlEnum
public enum MessageType {
    @XmlEnumValue("Request_Receive_Data")
    REQUEST_RECEIVE_DATA("Request_Receive_Data"),
    @XmlEnumValue("Request_Send_Data")
    REQUEST_SEND_DATA("Request_Send_Data"),
    @XmlEnumValue("Login")
    LOGIN("Login"),
    @XmlEnumValue("Logout")
    LOGOUT("Logout"),
    @XmlEnumValue("GetFriends")
    GET_FRIENDS("GetFriends");
    private final String value;

    MessageType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MessageType fromValue(String v) {
        for (MessageType c: MessageType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
