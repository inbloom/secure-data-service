begin
  require 'fileutils'
  begin
    File.delete("../ingestion/ingestion-validation/target/OfflineValidationTool/ingestion-validation/coverage.ec")
  rescue Exception => e
  end
  begin
    File.delete("../ingestion/ingestion-validation/target/OfflineValidationTool/ingestion-base/coverage.ec")
  rescue Exception => e
  end
  begin
    FileUtils.rm_rf('../unit_tests/')
  rescue Exception => e
  end
  begin
    FileUtils.rm_rf('../acceptance_tests/')
  rescue Exception => e
  end
  begin
    FileUtils.rm_rf('../combined_tests/')
  rescue Exception => e
  end
  begin
    File.delete("../../coverage.ec")
  rescue Exception => e
  end
  begin
    File.delete("./coverage.ec")
  rescue Exception => e
  end
  begin
    File.delete("../tomcat_coverage.ec")
  rescue Exception => e
  end
end
