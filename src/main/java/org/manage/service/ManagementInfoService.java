package org.manage.service;

import io.quarkus.runtime.configuration.ProfileManager;
import org.manage.config.JHipsterInfo;
import org.manage.service.dto.ManagementInfoDTO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
* Provides information for management/info resource
*/
@ApplicationScoped
public class ManagementInfoService {

    private final JHipsterInfo JHipsterInfo;

    @Inject
    public ManagementInfoService(JHipsterInfo JHipsterInfo) {
        this.JHipsterInfo = JHipsterInfo;
    }

    public ManagementInfoDTO getManagementInfo(){
        var info = new ManagementInfoDTO();
        if(JHipsterInfo.isEnable()){
            info.activeProfiles.add("swagger");
        }
        info.activeProfiles.add(ProfileManager.getActiveProfile());
        info.displayRibbonOnProfiles = ProfileManager.getActiveProfile();
        return info;
    }
}

