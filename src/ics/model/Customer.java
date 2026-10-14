package ics.model;

import ics.exception.InvalidInputException;
import ics.util.FileCodec;
import ics.util.Validate;

/** A person who buys policies. Registered by Staff; has no login. */
public class Customer extends Entity {

    private String name;
    private String contact;
    private String address;

    public Customer(String id, String name, String contact, String address) throws InvalidInputException {
        super(id);
        setName(name);
        setContact(contact);
        setAddress(address);
    }

    public String getName()    { return name; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }

    public final void setName(String name) throws InvalidInputException {
        this.name = Validate.notBlank("Name", name);
    }

    public final void setContact(String contact) throws InvalidInputException {
        this.contact = Validate.notBlank("Contact number", contact);
    }

    public final void setAddress(String address) {
        this.address = address == null ? "" : address.trim();   // optional field
    }

    @Override
    public String toFileLine() {
        return FileCodec.join(getId(), name, contact, address);
    }
}
