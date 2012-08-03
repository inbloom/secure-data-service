package org.slc.sli.sif.translation.converter;

public class AbstractConverter<A, B> implements Converter
{
    private Class<A> prototypeA;
    private Class<B> prototypeB;

    /**
     * Defines two types, which will take part conversion.
     *
     * @param prototypeA type source
     * @param prototypeB type destination
     */
    public AbstractConverter(Class<A> prototypeA, Class<B> prototypeB) {
      this.prototypeA = prototypeA;
      this.prototypeB = prototypeB;
    }
    
    @Override
    public Object convert(Object source)
    {
        return null;
    }

}
