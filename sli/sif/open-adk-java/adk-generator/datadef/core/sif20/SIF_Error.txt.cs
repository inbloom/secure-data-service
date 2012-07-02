    public SIF_Error(SifErrorCategoryCode category, int sifCode, string sifDesc, string sifExtDesc)
            : this((int)category, sifCode, sifDesc)
        {
            this.SIF_ExtendedDesc = sifExtDesc;
        }


        public override string ToString()
        {
            System.Text.StringBuilder buf = new System.Text.StringBuilder();

            try
            {
                buf.Append("[Category=");
                buf.Append(SIF_Category);
                buf.Append("; Code=");
                buf.Append(SIF_Code);
                buf.Append("] ");

                string desc = SIF_Desc;
                if (desc != null)
                    buf.Append(desc);
                desc = SIF_ExtendedDesc;
                if (desc != null)
                {
                    buf.Append(": ");
                    buf.Append(desc);
                }
            }
            catch (System.Exception thr)
            {
                System.Console.Out.WriteLine(thr);
                System.Console.Out.WriteLine(thr.StackTrace);
            }

            return buf.ToString();
        }
