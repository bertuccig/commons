package sos.util;

/** <p>
 * Title: SOSISOFormat.java
 * </p>
 * <p>
 * Description: abfragen der ISO Format
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company: SOS GmbH
 * </p>
 * 
 * @author M�r�vet �ks�z
 * @version 1.0 */
public class SOSISOFormat {

    /** �berpr�ft, ob die �bergebene L�nderkennzeichen ein ISO-2-Format ist. Die
     * �bergebene Parameter ist dabei die L�nderkennzeichen. Gro� und
     * Kleinschreibungen werden hier ignoriert.
     * 
     * @param country
     * @return boolean */
    public static boolean isISOFormatCountry(String country) throws Exception {
        try {
            String[] c = java.util.Locale.getISOCountries();
            for (int i = 0; i < c.length; i++) {
                if (c[i].equalsIgnoreCase(country)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            throw new Exception("\n -> error in " + SOSClassUtil.getMethodName() + " " + e);
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("DE is ISO Format Country? " + SOSISOFormat.isISOFormatCountry("DE"));
            System.out.println("XY is ISO Format Country? " + SOSISOFormat.isISOFormatCountry("XY"));
            System.out.println("GB is ISO Format Country? " + SOSISOFormat.isISOFormatCountry("GB"));
        } catch (Exception e) {
            System.err.print(e);

        }
    }
}
