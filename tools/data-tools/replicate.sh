#The first parameter specifies the directory that contains edfi xml dataset files.
#The second parameter specifies the number of copies/districts to be created from district data.
#The replicated data sets will be saved in ./multiDistrictData/ddMMyyyy_HHmmss
java -Xmx40g -jar Replicate.jar multiDistrictData/ 10
