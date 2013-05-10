#!/usr/bin/ruby

# Clean up the bulk extraction zone and bulk extract database according to arguments.

def main()
  # Check the argumment signature.
  begin
    tenantCleaner = check_args(ARGV)
    if (tenantCleaner == nil)
      print_help()
    end
  rescue Exception => ex
    puts ex.message
    print_usage()
    exit 1
  end

  # Perform the actual bulk extract cleanup.
  tenantCleaner.clean()
  exit 0
end

def print_usage()
  puts "Usage:"
  puts $PROGRAM_NAME + " <tenant>"
  puts $PROGRAM_NAME + " <tenant> -d<date>"
  puts $PROGRAM_NAME + " <tenant> -e<edorg>"
  puts $PROGRAM_NAME + " <tenant> -e<edorg> -d<date>"
  puts $PROGRAM_NAME + " <tenant> -f<file>"
  puts $PROGRAM_NAME + " -h | -help"
end

def print_help()
  print_usage()
  puts
  puts "Use Cases:"
  puts $PROGRAM_NAME + " <tenant>:"
  puts "    Remove all bulk extract files and their database metadata for tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -e<edorg>"
  puts "    Remove all bulk extract files and their database metadata for"
  puts "    educational organization <edOrg> belonging to tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -e<edorg> -d<date>"
  puts "    Remove all bulk extract files and their database metadata dated previous to"
  puts "    extraction date <date> for educational organization <edOrg> belonging to tenant <tenant>."
  puts $PROGRAM_NAME + " <tenant> -f<file_path>"
  puts "    Remove the particular bulk extract file <file> and its database metadata for tenant <tenant>."
  puts $PROGRAM_NAME + " -h | -help"
  puts "    Print this help page and exit."
  puts
  puts "Note:"
  puts "      <tenant> specifies tenant unique ID, e.g. \"Midgar\""
  puts "      <date> is in ISO8601 datetime format, e.g. \"2013-05-10T01:33:27.857Z\""
  puts "      <edOrg> specifies educational organization unique ID, e.g. \"IL-DAYBREAK\""
  puts "      <file> specifies extract file full directory pathname, e.g.\"/bulk/extract/tarfile.tar\""
end

def check_args(argv)
  # Identify non-use cases..
  if ((argv.length < 1) || (argv.length > 3))
    raise(ArgumentError, "Wrong number of arguments")
  elsif ((argv.length == 1) && (argv[0].eql?("-h") || argv[0].eql?("-help")))
    return nil
  end

  # Verify use case parameters.
  tenant = argv[0]
  edorg = nil
  date = nil
  file = nil
  argv[1..argv.length - 1].each do |param|
    case param[0..1]
      when "-e"
        if (edorg != nil)
          raise(ArgumentError, "Argument for edorg repeated")
        end
        edorg = param[2..param.length - 1]
      when "-d"
        if (date != nil)
          raise(ArgumentError, "Argument for date repeated")
        end
        date = param[2..param.length - 1]
      when "-f"
        if (file != nil)
          raise(ArgumentError, "Argument for file repeated")
        end
        file = param[2..param.length - 1]
      else
        raise(ArgumentError, "Invalid argument(s)")
    end
  end
  return TenantCleaner.new(tenant, date, edorg, file)
end

class TenantCleaner
  def initialize(tenantName, dateTime, edOrgName, filePathname)
    @tenant = tenantName
    @date = dateTime
    @edOrg = edOrgName
    @file = filePathname
  end

  def tenant()
    return @tenant
  end

  def date()
    return @date
  end

  def edorg()
    return @edorg
  end

  def file()
    return @file
  end

  def clean()
    if (file != nil)
      cleanFile()
    elsif ((edorg != nil) && (date != nil))
      cleanEdOrgDate()
    elsif (date != nil)
      cleanDate()
    elsif (edorg != nil)
      cleanEdOrg()
    else
      cleanTenant()
    end
  end

  def cleanFile()
    verified = getVerification("You are about to delete bulk extract file " + @file + " and its database metadata " + \
                               "for tenant " + @tenant)
  end

  def cleanEdOrgDate()
    verified = getVerification("You are about to delete all bulk extract files and database metadata for " + \
                               "education organization " + @edOrg + "for tenant " + @tenant + " before date " + @date)
  end

  def cleanEdOrg()
    verified = getVerification("You are about to delete all bulk extract files and database metadata for " + \
                               "education organization " + @edOrg + "for tenant " + @tenant)
  end

  def cleanDate()
    verified = getVerification("You are about to delete all bulk extract files and database metadata " + \
                               "for tenant " + @tenant + " before date " + @date)
  end

  def cleanTenant()
    verified = getVerification("You are about to delete all bulk extract files and database metadata for " + \
                               "tenant " + @tenant)
  end

  def getVerification(prompt)
    puts(prompt)
    print("Do you wish to proceed ('y' or 'yes' to proceed)? ")
    answer = STDIN.gets
    return (answer.eql?("y") || answer.eql?("yes"))
  end
end

# Run the main program here.
main()
