//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for esr5Type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="esr5Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="bank" type="{http://www.xmlData.ch/xmlInvoice/XSD}bankAddressType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="amount_due" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="coding_line" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="(&lt;[0-9]{2}0001[0-9]{9}> [0-9]{15}\+ [0-9]{5}>|[0-9]{15}\+ [0-9]{5}>)"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="participant_number" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[0-9]{5}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="reference_number" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[0-9]{5} [0-9]{5} [0-9]{5}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="type" default="15">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="15plus"/>
 *             &lt;enumeration value="15"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "esr5Type", propOrder = {
	"bank"
})
public class Esr5Type {
	
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD")
	protected BankAddressType bank;
	@XmlAttribute(name = "amount_due", required = true)
	protected double amountDue;
	@XmlAttribute(name = "coding_line", required = true)
	protected String codingLine;
	@XmlAttribute(name = "participant_number", required = true)
	protected String participantNumber;
	@XmlAttribute(name = "reference_number", required = true)
	protected String referenceNumber;
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String type;
	
	/**
	 * Gets the value of the bank property.
	 * 
	 * @return possible object is {@link BankAddressType }
	 * 
	 */
	public BankAddressType getBank(){
		return bank;
	}
	
	/**
	 * Sets the value of the bank property.
	 * 
	 * @param value
	 *            allowed object is {@link BankAddressType }
	 * 
	 */
	public void setBank(BankAddressType value){
		this.bank = value;
	}
	
	/**
	 * Gets the value of the amountDue property.
	 * 
	 */
	public double getAmountDue(){
		return amountDue;
	}
	
	/**
	 * Sets the value of the amountDue property.
	 * 
	 */
	public void setAmountDue(double value){
		this.amountDue = value;
	}
	
	/**
	 * Gets the value of the codingLine property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodingLine(){
		return codingLine;
	}
	
	/**
	 * Sets the value of the codingLine property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodingLine(String value){
		this.codingLine = value;
	}
	
	/**
	 * Gets the value of the participantNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getParticipantNumber(){
		return participantNumber;
	}
	
	/**
	 * Sets the value of the participantNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setParticipantNumber(String value){
		this.participantNumber = value;
	}
	
	/**
	 * Gets the value of the referenceNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getReferenceNumber(){
		return referenceNumber;
	}
	
	/**
	 * Sets the value of the referenceNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setReferenceNumber(String value){
		this.referenceNumber = value;
	}
	
	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType(){
		if (type == null) {
			return "15";
		} else {
			return type;
		}
	}
	
	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value){
		this.type = value;
	}
	
}