package admin.grails
import grails.converters.*


class RolesController {
	private static final String RAW_JSON = "[{\"name\":\"Educator\",\"rights\":[\"AGGREGATE_READ\",\"READ_GENERAL\"]},{\"name\":\"Leader\",\"rights\":[\"AGGREGATE_READ\",\"READ_GENERAL\",\"READ_RESTRICTED\"]},{\"name\":\"Aggregate Viewer\",\"rights\":[\"AGGREGATE_READ\"]},{\"name\":\"IT Administrator\",\"rights\":[\"AGGREGATE_READ\",\"READ_GENERAL\",\"READ_RESTRICTED\",\"WRITE_GENERAL\",\"WRITE_RESTRICTED\"]}]";
  
	def index = {
		def jsonArray = JSON.parse(RAW_JSON)
		def roles = [];
		jsonArray.each() {
			def general = null;
			if (it.rights.contains("READ_GENERAL") && it.rights.contains("WRITE_GENERAL")) {
				general = "R/W";
			} else if (it.rights.contains("READ_GENERAL")) {
				general = "R";
			} else if (it.rights.contains("WRITE_GENERAL")) {
				general = "W";
			}
			def restricted = null;
			if (it.rights.contains("READ_RESTRICTED") && it.rights.contains("WRITE_RESTRICTED")) {
				restricted = "R/W";
			} else if (it.rights.contains("READ_RESTRICTED")) {
				restricted = "R";
			} else if (it.rights.contains("WRITE_RESTRICTED")) {
				restricted = "W";
			}
			roles.add([
				name: "${it.name}",
				aggregate: "${it.rights.contains('AGGREGATE_READ')}".toBoolean(),
				individual: "",
				general: general,
				restricted: restricted
			])
		}
		[roles: roles]
	}

}
