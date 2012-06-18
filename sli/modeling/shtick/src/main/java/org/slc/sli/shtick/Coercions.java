package org.slc.sli.shtick;

import java.math.BigInteger;

public final class Coercions {

    public static final BigInteger toBigInteger(final Object obj) {
        if (obj != null) {
            return new BigInteger(obj.toString());
        } else {
            return null;
        }
    }
}
