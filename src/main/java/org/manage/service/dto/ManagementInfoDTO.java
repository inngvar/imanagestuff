package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
*  DTO to emulate /management/info response
*/
public class ManagementInfoDTO {

    public List<String> activeProfiles = Lists.newArrayList();

    @JsonbProperty("display-ribbon-on-profiles")
    public String displayRibbonOnProfiles;

}
