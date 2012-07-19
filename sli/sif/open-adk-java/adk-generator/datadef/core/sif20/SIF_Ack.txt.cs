		internal OpenADK.Library.SifMessagePayload message;
		
		/// <summary>  Determines if this SIF_Ack has a specific status code.</summary>
		/// <param name="code">The status code to test for
		/// </param>
		/// <returns> true if the SIF_Ack has one or more SIF_Status children and one
		/// of those contains the status code
		/// </returns>
		public virtual bool HasStatusCode(int code)
		{
			SIF_Status stat = this.SIF_Status;
            if( stat != null && stat.SIF_Code == code )
            {
                return true;
            }
			
			return false;
		}
		
		/// <summary>  Determines if this SIF_Ack communicates an error</summary>
		/// <returns> true if there is at least one SIF_Error child of SIF_Ack
		/// </returns>
		public virtual bool HasError()
		{
			return this.SIF_Error != null;
		}
		
		/// <summary>  Determines if this SIF_Ack contains the specified error code</summary>
		/// <param name="category">The error category
		/// </param>
		/// <param name="code">The error code
		/// </param>
		/// <returns> true if the SIF_Ack contains this error
		/// </returns>
		public virtual bool HasError(int category, int code)
		{
			string ca = category.ToString();
			string co = code.ToString();
			SIF_Error error = this.SIF_Error;
            if( error != null &&  error.SIF_Category == category && error.SIF_Code == code )
            {
                return true;
            }
			
			return false;
		}
		
		
		public override void  LogSend(log4net.ILog log)
		{
			if ((Adk.Debug & AdkDebugFlags.Messaging ) != 0)
			{
				LogCommon("Send ", log);
			}
		}
		
		
		public override void  LogRecv(log4net.ILog log)
		{
			if ((Adk.Debug & AdkDebugFlags.Messaging ) != 0)
			{
				LogCommon("Receive ", log);
			}
		}
		
		private void  LogCommon(string direction, log4net.ILog log)
		{
			System.Text.StringBuilder b = new System.Text.StringBuilder(direction);
			b.Append( ElementDef.Tag(SifVersion));
			b.Append(" (Status = ");

            SIF_Status stat = this.SIF_Status;
            if( stat != null )
            {
                b.Append( stat.SIF_Code );
            }
            else
            {
                b.Append( "none" );
            }
			
			SIF_Error err = this.SIF_Error;
			if (err != null)
			{
				b.Append("; 1 Error");
			}
			
			b.Append(")");
			log.Debug(b.ToString());
			
			if (err != null && (Adk.Debug & AdkDebugFlags.Messaging) != 0)
			{
				log.Debug(err.ToString());
			}
			
			if ((Adk.Debug & AdkDebugFlags.Messaging_Detailed ) != 0)
			{
				string id = MsgId;
				log.Debug("  MsgId: " + ( id == null?"<none>":id));
				id = SIF_OriginalMsgId;
				log.Debug("  OrgId: " + ( id == null?"<none>":id));
			}
		}
