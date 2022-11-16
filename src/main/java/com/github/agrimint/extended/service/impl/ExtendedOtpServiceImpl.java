package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.resources.vm.OtpRequestVM;
import com.github.agrimint.extended.resources.vm.OtpResponseVM;
import com.github.agrimint.extended.resources.vm.OtpValidationVM;
import com.github.agrimint.extended.service.ExtendedOtpService;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.service.OtpRequestQueryService;
import com.github.agrimint.service.OtpRequestService;
import com.github.agrimint.service.criteria.OtpRequestCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.OtpRequestDTO;
import java.time.Instant;
import java.util.Locale;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.StringFilter;

/**
 * @author OMONIYI ILESANMI
 */

@Service
public class ExtendedOtpServiceImpl implements ExtendedOtpService {

    @Autowired
    private OtpRequestService otpRequestService;

    @Autowired
    private OtpRequestQueryService otpRequestQueryService;

    @Autowired
    private UserUtil userUtil;

    @Override
    public OtpResponseVM generateOtp(OtpRequestVM otpRequestVM) {
        OtpRequestDTO otpRequest = new OtpRequestDTO();
        BeanUtils.copyProperties(otpRequestVM, otpRequest);
        otpRequest.setPhoneNumber(otpRequestVM.getPhoneNumber());
        otpRequest.setCountryCode(otpRequestVM.getCountryCode());
        otpRequest.setStatus("NEW");
        otpRequest.setOtpType(otpRequestVM.getOtpType().toUpperCase(Locale.ROOT));
        otpRequest.setOtp(RandomStringUtils.randomNumeric(otpRequestVM.getOtpLength()));
        otpRequestService.save(otpRequest);
        OtpResponseVM responseVM = new OtpResponseVM();
        responseVM.setOtp(otpRequest.getOtp());
        return responseVM;
    }

    @Override
    public OtpRequestDTO validateOtp(OtpValidationVM otpValidationVM) {
        OtpRequestCriteria criteria = new OtpRequestCriteria();
        StringFilter statusFilter = new StringFilter();
        statusFilter.setEquals("NEW");
        criteria.setStatus(statusFilter);

        StringFilter phoneNumberFilter = new StringFilter();
        phoneNumberFilter.setEquals(otpValidationVM.getPhoneNumber());
        criteria.setPhoneNumber(phoneNumberFilter);

        StringFilter countryCodeFilter = new StringFilter();
        countryCodeFilter.setEquals(otpValidationVM.getCountryCode());
        criteria.setCountryCode(countryCodeFilter);

        StringFilter otyTypeFilter = new StringFilter();
        otyTypeFilter.setEquals(otpValidationVM.getOtpType().toUpperCase());
        criteria.setOtpType(otyTypeFilter);

        StringFilter otpFilter = new StringFilter();
        otpFilter.setEquals(otpValidationVM.getOtp());
        criteria.setOtp(otpFilter);
        OtpRequestDTO request = otpRequestQueryService.findByCriteria(criteria).stream().findFirst().orElse(null);
        if (request != null) {
            request.setDateValidated(Instant.now());
            request.setStatus("DONE");
            otpRequestService.save(request);
        }
        return request;
    }
}
