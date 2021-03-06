/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.zamia.verilog.node;

import java.util.*;
import org.zamia.verilog.analysis.*;

@SuppressWarnings("nls")
public final class AP1PrimaryHierarchicalIdentifier extends PPrimaryHierarchicalIdentifier
{
    private PIdentifier _identifier_;
    private final LinkedList<PBracketRange> _bracketRange_ = new LinkedList<PBracketRange>();
    private TTPeriod _tPeriod_;
    private PPrimaryHierarchicalIdentifier _primaryHierarchicalIdentifier_;

    public AP1PrimaryHierarchicalIdentifier()
    {
        // Constructor
    }

    public AP1PrimaryHierarchicalIdentifier(
        @SuppressWarnings("hiding") PIdentifier _identifier_,
        @SuppressWarnings("hiding") List<PBracketRange> _bracketRange_,
        @SuppressWarnings("hiding") TTPeriod _tPeriod_,
        @SuppressWarnings("hiding") PPrimaryHierarchicalIdentifier _primaryHierarchicalIdentifier_)
    {
        // Constructor
        setIdentifier(_identifier_);

        setBracketRange(_bracketRange_);

        setTPeriod(_tPeriod_);

        setPrimaryHierarchicalIdentifier(_primaryHierarchicalIdentifier_);

    }

    @Override
    public Object clone()
    {
        return new AP1PrimaryHierarchicalIdentifier(
            cloneNode(this._identifier_),
            cloneList(this._bracketRange_),
            cloneNode(this._tPeriod_),
            cloneNode(this._primaryHierarchicalIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAP1PrimaryHierarchicalIdentifier(this);
    }

    public PIdentifier getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(PIdentifier node)
    {
        if(this._identifier_ != null)
        {
            this._identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._identifier_ = node;
    }

    public LinkedList<PBracketRange> getBracketRange()
    {
        return this._bracketRange_;
    }

    public void setBracketRange(List<PBracketRange> list)
    {
        this._bracketRange_.clear();
        this._bracketRange_.addAll(list);
        for(PBracketRange e : list)
        {
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
        }
    }

    public TTPeriod getTPeriod()
    {
        return this._tPeriod_;
    }

    public void setTPeriod(TTPeriod node)
    {
        if(this._tPeriod_ != null)
        {
            this._tPeriod_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._tPeriod_ = node;
    }

    public PPrimaryHierarchicalIdentifier getPrimaryHierarchicalIdentifier()
    {
        return this._primaryHierarchicalIdentifier_;
    }

    public void setPrimaryHierarchicalIdentifier(PPrimaryHierarchicalIdentifier node)
    {
        if(this._primaryHierarchicalIdentifier_ != null)
        {
            this._primaryHierarchicalIdentifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._primaryHierarchicalIdentifier_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._identifier_)
            + toString(this._bracketRange_)
            + toString(this._tPeriod_)
            + toString(this._primaryHierarchicalIdentifier_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._identifier_ == child)
        {
            this._identifier_ = null;
            return;
        }

        if(this._bracketRange_.remove(child))
        {
            return;
        }

        if(this._tPeriod_ == child)
        {
            this._tPeriod_ = null;
            return;
        }

        if(this._primaryHierarchicalIdentifier_ == child)
        {
            this._primaryHierarchicalIdentifier_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._identifier_ == oldChild)
        {
            setIdentifier((PIdentifier) newChild);
            return;
        }

        for(ListIterator<PBracketRange> i = this._bracketRange_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PBracketRange) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._tPeriod_ == oldChild)
        {
            setTPeriod((TTPeriod) newChild);
            return;
        }

        if(this._primaryHierarchicalIdentifier_ == oldChild)
        {
            setPrimaryHierarchicalIdentifier((PPrimaryHierarchicalIdentifier) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
