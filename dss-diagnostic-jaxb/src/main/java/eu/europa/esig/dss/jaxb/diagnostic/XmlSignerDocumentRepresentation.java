//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.04.05 at 04:38:43 PM CEST 
//


package eu.europa.esig.dss.jaxb.diagnostic;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="HashOnly" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="DocHashOnly" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class XmlSignerDocumentRepresentation implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlAttribute(name = "HashOnly", required = true)
    protected boolean hashOnly;
    @XmlAttribute(name = "DocHashOnly", required = true)
    protected boolean docHashOnly;

    /**
     * Gets the value of the hashOnly property.
     * 
     */
    public boolean isHashOnly() {
        return hashOnly;
    }

    /**
     * Sets the value of the hashOnly property.
     * 
     */
    public void setHashOnly(boolean value) {
        this.hashOnly = value;
    }

    /**
     * Gets the value of the docHashOnly property.
     * 
     */
    public boolean isDocHashOnly() {
        return docHashOnly;
    }

    /**
     * Sets the value of the docHashOnly property.
     * 
     */
    public void setDocHashOnly(boolean value) {
        this.docHashOnly = value;
    }

}
