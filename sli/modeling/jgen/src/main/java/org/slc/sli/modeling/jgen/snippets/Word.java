package org.slc.sli.modeling.jgen.snippets;

import java.io.IOException;

import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

public enum Word implements JavaSnippet {

    NULL("null");

    private final String word;

    Word(final String word) {
        if (word == null) {
            throw new NullPointerException("word");
        }
        this.word = word;
    }

    @Override
    public void write(final JavaStreamWriter jsw) throws IOException {
        jsw.write(word);
    }
}
