package org.slc.sli.ingestion.dal;

import java.util.Date;

import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.ingestion.model.Error;

/**
 * Spring converter registered in the Mongo configuration to convert DBObjects
 * into NewBatchJob.
 *
 */
public class ErrorReadConverter implements Converter<DBObject, Error> {
	private static final Logger LOG = LoggerFactory.getLogger(NeutralRecordWriteConverter.class);
	EntityEncryption encryptor;

	public EntityEncryption getEncryptor() {
		return encryptor;
	}

	public void setEncryptor(EntityEncryption encryptor) {
		this.encryptor = encryptor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Error convert(DBObject dbObj) {

		Error error = new Error();

		Object obj = dbObj.get("batchJobId");

		if (obj instanceof String) {
			error.setBatchJobId((String) obj);
			LOG.info("333333333333333333" + error.getBatchJobId());
		} else {
			LOG.info("batchJobId is invalid!");
		}

		obj = dbObj.get("stageName");

		if (obj instanceof String) {
			error.setStageName((String) obj);
			LOG.info("444444444444444444" + error.getStageName());
		} else {
			LOG.info("statgeName is invalid! ");
		}

		obj = dbObj.get("resourceId");
		if (obj instanceof String) {
			error.setResourceId((String) obj);
			LOG.info("555555555555555555" + error.getResourceId());
		} else {
			LOG.info("resourceId is invalid! ");
		}

		obj = dbObj.get("sourceIp");
		if (obj instanceof String) {
			error.setSourceIp((String) obj);
			LOG.info("66666666666666666" + error.getSourceIp());
		} else {
			LOG.info("sourceIp is invalid! ");
		}

		obj = dbObj.get("hostname");
		if (obj instanceof String) {
			error.setHostname((String) obj);
			LOG.info("77777777777777777" + error.getHostname());
		} else {
			LOG.info("hostname is invalid! ");
		}

		obj = dbObj.get("timestamp");
		if (obj instanceof Date) {
			error.setTimestamp((Date) obj);
			LOG.info("0000000000000000=======" + error.getTimestamp());
		} else {
			LOG.info("timestamp is invalid! ");
		}

		obj = dbObj.get("severity");
		if (obj instanceof String) {
			error.setSeverity((String) obj);
			LOG.info("8888888888888888" + error.getSeverity());
		} else {
			LOG.info("severity is invalid! ");
		}

		obj = dbObj.get("errorDetail");
		if (obj instanceof String) {
			error.setErrorDetail((String) encryptor.decryptSingleValue(obj));
			LOG.info("9999999999999999" + error.getErrorDetail());
		} else {
			LOG.info("errorDetail is invalid! ");
		}

		// error.setStageName((String) dbObj.get("stageName"));
		// LOG.info("444444444444444444" + error.getStageName());
		// error.setResourceId((String) dbObj.get("resourceId"));
		// LOG.info("555555555555555555" + error.getResourceId());
		// error.setSourceIp((String) dbObj.get("sourceIp"));
		// LOG.info("66666666666666666" + error.getSourceIp());
		// error.setHostname((String) dbObj.get("hostname"));
		// LOG.info("77777777777777777" + error.getHostname());
		// error.setTimestamp((Date) dbObj.get("timestamp"));
		// LOG.info("0000000000000000=======" + error.getTimestamp());
		// error.setSeverity((String) dbObj.get("severity"));
		// LOG.info("8888888888888888" + error.getSeverity());
		// error.setErrorDetail((String) encryptor.decryptSingleValue(dbObj.get("errorDetail")));
		// LOG.info("9999999999999999" + error.getErrorDetail());
		return error;

	}

}
