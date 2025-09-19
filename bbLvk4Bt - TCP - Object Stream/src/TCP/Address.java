package TCP;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = 20180801L;
    private int id;
    private String code;
    private String addressLine;
    private String city;
    private String postalCode;

    public Address() {}

    public Address(int id, String code, String addressLine, String city, String postalCode) {
        this.id = id;
        this.code = code;
        this.addressLine = addressLine;
        this.city = city;
        this.postalCode = postalCode;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getPostalCode() { return postalCode; }

    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    @Override
    public String toString() {
        return "TCP.Address{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", addressLine='" + addressLine + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}

