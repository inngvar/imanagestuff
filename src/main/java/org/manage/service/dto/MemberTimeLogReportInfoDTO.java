package org.manage.service.dto;

import com.google.common.collect.Lists;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class MemberTimeLogReportInfoDTO {

    public MemberDTO member;

    public List<TimeLogDTO> entries = Lists.newArrayList();

    public double totalHours;

}
