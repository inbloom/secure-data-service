package org.slc.sli.modeling.sdkgen.grammars;

import javax.xml.namespace.QName;

public interface SdkGenElement {

    QName getName();

    SdkGenType getType();
}
