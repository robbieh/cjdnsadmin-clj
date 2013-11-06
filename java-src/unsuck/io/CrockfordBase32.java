package unsuck.io;


/**
 * Java implementation of Crockford's Base32 algorithm for encoding simple numeric values.
 * @author Jeff Schnitzer
 */
public class CrockfordBase32 {
       
        /** The 32 symbols which we should use for encoding */
        private static final char[] ENCODE_SYMBOLS = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
                'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
        };
       
        /** Decode one character symbol */
        private static int decodeSymbol(char c) {
                switch (c) {
                        case '0':
                        case 'O':
                        case 'o': return 0;
                               
                        case '1':
                        case 'I':
                        case 'i':
                        case 'L':
                        case 'l': return 1;
                       
                        case '2': return 2;
                        case '3': return 3;
                        case '4': return 4;
                        case '5': return 5;
                        case '6': return 6;
                        case '7': return 7;
                        case '8': return 8;
                        case '9': return 9;
                       
                        case 'A':
                        case 'a': return 10;
                       
                        case 'B':
                        case 'b': return 11;
                       
                        case 'C':
                        case 'c': return 12;
                       
                        case 'D':
                        case 'd': return 13;
                       
                        case 'E':
                        case 'e': return 14;
                       
                        case 'F':
                        case 'f': return 15;
                       
                        case 'G':
                        case 'g': return 16;
                       
                        case 'H':
                        case 'h': return 17;
                       
                        case 'J':
                        case 'j': return 18;
                       
                        case 'K':
                        case 'k': return 19;
                       
                        case 'M':
                        case 'm': return 20;
                       
                        case 'N':
                        case 'n': return 21;
                       
                        case 'P':
                        case 'p': return 22;
                       
                        case 'Q':
                        case 'q': return 23;
                       
                        case 'R':
                        case 'r': return 24;
                       
                        case 'S':
                        case 's': return 25;
                       
                        case 'T':
                        case 't': return 26;
                       
                        case 'U':
                        case 'u':
                        case 'V':
                        case 'v': return 27;
                       
                        case 'W':
                        case 'w': return 28;
                       
                        case 'X':
                        case 'x': return 29;
                       
                        case 'Y':
                        case 'y': return 30;
                       
                        case 'Z':
                        case 'z': return 31;
                       
                        default: throw new IllegalArgumentException("Illegal character: " + c);
                }
        }
       
        /**
         * Encode a long to a compact Crockford-Base32 string.  No check symbol is added.
         * @param value must be a positive value
         * @return a properly crockford-encoded base32 string
         */
        public static String encode(long value) {
                assert value >= 0;
               
                // Special case this because otherwise nothing would be considered significant and we'd produce an empty string.
                if (value == 0)
                        return "0";

                StringBuilder bld = new StringBuilder();
                boolean significant = false;    // goes true when we are past leading 0s
               
                // Now deal with each set of 5 bits from top to bottom. The top is 4 bits but whatever.
                for (int i=60; i>=0; i-=5) {
                        int fiveBits = (int)(31 & (value >>> i));
                        if (fiveBits == 0) {
                                if (significant)
                                        bld.append(ENCODE_SYMBOLS[fiveBits]);   // always '0'
                        } else {
                                bld.append(ENCODE_SYMBOLS[fiveBits]);
                                significant = true;
                        }
                }
               
                return bld.toString();
        }

        /**
         * Decode a Crockford-Base32-encoded value into a long.
         * @param encoded must not exceed the value for a long
         * @return the positive long value from decoding the string.
         */
        public static long decode(String encoded) {
                long value = 0;
               
                for (int i=0; i<encoded.length(); i++) {
                        value = value << 5;
                        value = value | decodeSymbol(encoded.charAt(i));
                }
               
                return value;
        }
       
        /** */
        public static void main(String[] args) {
                tryout(1);
                tryout(31);
                tryout(32);
                tryout(100);
                tryout(12345);
                tryout(47001);
               
                System.out.println("2RVV is " + decode("2RVV"));
        }
       
        private static void tryout(long num) {
                String encoded = encode(num);
                long back = decode(encoded);
               
                System.out.println(num + " -> " + encoded + " -> " + back);
        }
}


