package it.workstocks.service;

import it.workstocks.exception.WorkstocksBusinessException;

public interface JobAlertService {
	void runJobAlert() throws WorkstocksBusinessException;;
	boolean isJobAlertApplicaredByApplicant(Long companyId, Long applicantId) throws WorkstocksBusinessException;
	Long addOrRemoveJobAlert(Long companyId, Long applicantId, boolean isAdding) throws WorkstocksBusinessException;
}
