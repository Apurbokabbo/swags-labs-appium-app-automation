package utilities;

/** The 6 products of the Swag Labs sample app with their expected prices. */
public enum Product {
    BACKPACK("Sauce Labs Backpack", 29.99),
    BIKE_LIGHT("Sauce Labs Bike Light", 9.99),
    BOLT_TSHIRT("Sauce Labs Bolt T-Shirt", 15.99),
    FLEECE_JACKET("Sauce Labs Fleece Jacket", 49.99),
    ONESIE("Sauce Labs Onesie", 7.99),
    RED_TSHIRT("Test.allTheThings() T-Shirt (Red)", 15.99);

    private final String displayName;
    private final double price;

    Product(String displayName, double price) {
        this.displayName = displayName;
        this.price = price;
    }

    public String displayName() { return displayName; }
    public double price()       { return price; }

    public static Product fromName(String name) {
        for (Product p : values()) {
            if (p.displayName.equals(name)) return p;
        }
        throw new IllegalArgumentException("Unknown product shown in app: " + name);
    }
}
