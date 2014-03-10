package org.slc.sli.api.ingestion.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.slc.sli.api.ingestion.service.IngestionBatchJobService;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.security.RightsAllowed;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@Scope("request") 
@Path(ResourceNames.INGESTION_JOBS)
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class IngestionBatchJobResource {

	@Autowired
	IngestionBatchJobService ingestionBatchJobService;

    @GET
    @RightsAllowed({ Right.INGESTION_LOG_VIEW })
	public @ResponseBody List<IngestionBatchJob> getAll() {
		return ingestionBatchJobService.find(0);
	}
}