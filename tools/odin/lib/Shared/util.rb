
################### Should be moved into utilities file #########################################
def stripInterchange(interchangeName)
  interchangeName[11..-5]
end

def genCtlFile(dir='generated')
  File.open("generated/ControlFile.ctl", 'w') do |f|
    Dir["#{File.dirname(__FILE__)}/#{dir}/*.xml"].each do |int|
      # I don't like how I get the ingestion file type below
      f.write "edfi-xml,#{stripInterchange(int[int.rindex('/')+1..-1])},#{int[int.rindex('/')+1..-1]},#{Digest::MD5.file(int).hexdigest}\n"
    end
  end
end

###########################################################################################################
