//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2025.01.19 at 12:32:39 AM CET 
//


package xmw.flush;

import xmw.exa.db.repository.BaseOperations;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element ref="{http://www.w3.org/namespace/}date"/&gt;
 *         &lt;element ref="{http://www.w3.org/namespace/}is_online"/&gt;
 *         &lt;element ref="{http://www.w3.org/namespace/}is_written"/&gt;
 *         &lt;element ref="{http://www.w3.org/namespace/}room_or_link"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="course" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dateOrIsOnlineOrIsWritten"
})
@XmlRootElement(name = "Exam")
public class Exam implements BaseOperations {

    @XmlElements({
        @XmlElement(name = "date", type = Date.class),
        @XmlElement(name = "is_online", type = IsOnline.class),
        @XmlElement(name = "is_written", type = IsWritten.class),
        @XmlElement(name = "room_or_link", type = RoomOrLink.class)
    })
    protected List<Object> dateOrIsOnlineOrIsWritten;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "course", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object course;

    /**
     * Gets the value of the dateOrIsOnlineOrIsWritten property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dateOrIsOnlineOrIsWritten property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDateOrIsOnlineOrIsWritten().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Date }
     * {@link IsOnline }
     * {@link IsWritten }
     * {@link RoomOrLink }
     * 
     * 
     */
    public List<Object> getDateOrIsOnlineOrIsWritten() {
        if (dateOrIsOnlineOrIsWritten == null) {
            dateOrIsOnlineOrIsWritten = new ArrayList<Object>();
        }
        return this.dateOrIsOnlineOrIsWritten;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the course property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCourse() {
        return course;
    }

    /**
     * Sets the value of the course property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCourse(Object value) {
        this.course = value;
    }

}
