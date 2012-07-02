    /// <summary>Attach a SifMessagePayload to this EventObject.<p></summary>
    public virtual void Attach( SifDataObject payload )
    {
        AddChild( payload );
    }

    /// <summary>Detach a SifMessagePayload from this EventObject.<p></summary>
    public virtual void Detach( SifDataObject payload )
    {
        RemoveChild( payload );
    }