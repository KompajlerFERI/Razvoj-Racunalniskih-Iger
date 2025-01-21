package si.um.feri.kompajler.utils;

public class Keys {
    public static String MAPBOX = "";
    public static String GEOAPIFY = System.getenv("GEOAPI_KEY");

    static {
        if (GEOAPIFY == null || GEOAPIFY.isEmpty()) {
            System.out.println("Error: GEOAPI_KEY is not set!");
        } else {
            System.out.println("GEOAPI_KEY is set.");
        }
    }
}
