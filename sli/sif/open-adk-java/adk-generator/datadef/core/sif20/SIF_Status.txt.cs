		public override string ToString()
		{
			System.Text.StringBuilder sb = new System.Text.StringBuilder();
			
			sb.Append(SIF_Code);
			SIF_Data data = SIF_Data;
			if (data != null)
			{
				sb.Append(" (");
				sb.Append(data.TextValue);
				sb.Append(")");
			}
			
			return sb.ToString();
		}