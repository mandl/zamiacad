/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import org.zamia.verilog.analysis.*;
import org.zamia.SourceFile;

@SuppressWarnings("nls")
public final class TKAlwaysLatch extends Token
{
    public TKAlwaysLatch(int line, int pos, SourceFile sf)
    {
        super ("always_latch", line, pos, sf);
    }

    @Override
    public Object clone()
    {
      return new TKAlwaysLatch(getLine(), getPos(), getSource());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKAlwaysLatch(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKAlwaysLatch text.");
    }
}