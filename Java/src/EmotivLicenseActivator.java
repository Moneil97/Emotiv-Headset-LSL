import java.text.SimpleDateFormat;

import com.emotiv.Iedk.Edk;
import com.emotiv.Iedk.EdkErrorCode;
import com.emotiv.Iedk.EmotivCloudClient;

public class EmotivLicenseActivator {
	
	private String userName, password, license_ID = "";

	public EmotivLicenseActivator(String userName, String password) {
		 	this.userName = userName;
		 	this.password = password;
	}
	
	public String login() {
        if (Edk.INSTANCE.IEE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
        	return "Emotiv Engine start up failed.";
        }
        if(EmotivCloudClient.INSTANCE.EC_Connect() != EdkErrorCode.EDK_OK.ToInt()){
            return "Cannot connect to Emotiv Cloud";
        }
        if(EmotivCloudClient.INSTANCE.EC_Login(userName, password) != EdkErrorCode.EDK_OK.ToInt()){            
            return "Your login attempt has failed. The username or password may be incorrect";
        }
        
        return "Logged in as " + userName;
	}
	
	public String getSessionsInfo() {
		int result = 0;
        
       //Debit Information
        Edk.DebitInfos_t.ByReference debitInfos = new Edk.DebitInfos_t.ByReference();
        
        //Get Debit Information
        result = Edk.INSTANCE.IEE_GetDebitInformation(license_ID, debitInfos);
        if (result == EdkErrorCode.EDK_OK.ToInt()){          
            if(debitInfos.total_session_inYear > 0){
            	return  "Remaining Sessions                   : " + (debitInfos.remainingSessions & 0xffffffffL) + //convert from signed int to unsigned long
            			"\nTotal debitable sessions in Year     : " + debitInfos.total_session_inYear;
            }
            else if(debitInfos.total_session_inMonth > 0){
            	return ("Remaining Sessions                   : " + (debitInfos.remainingSessions & 0xffffffffL)) + //convert from signed int to unsigned long
            			"\nTotal debitable sessions in Month    : " + debitInfos.total_session_inMonth;
            }
            else{
            	return "Remaining Sessions                   : unlimitted"
            		 + "\nTotal debitable sessions in Year     : unlimitted";
            }
        }
        else{
        	return "Get Debit Information unsuccessfully";
        }
        
	}
	
	public String addSessions(int sessions) {
        
        //Authorize License with debit number
        int result = Edk.INSTANCE.IEE_AuthorizeLicense(license_ID, sessions);
        
        if(result == EdkErrorCode.EDK_LICENSE_NOT_FOUND.ToInt()){
            return "AuthorizeLicense: EDK_LICENSE_NOT_FOUND";
        }
        else if (result == EdkErrorCode.EDK_LICENSE_ERROR.ToInt()){
        	return "AuthorizeLicense: EDK_LICENSE_ERROR";
        }
        else if (result == EdkErrorCode.EDK_LICENSE_EXPIRED.ToInt()){
        	return "AuthorizeLicense: EDK_LICENSE_EXPIRED";
        }
        else if (result == EdkErrorCode.EDK_LICENSE_REGISTERED.ToInt()){
        	return "AuthorizeLicense: EDK_LICENSE_REGISTERED";
        }
        else if (result == EdkErrorCode.EDK_LICENSE_DEVICE_LIMITED.ToInt()){
        	return "AuthorizeLicense: EDK_LICENSE_DEVICE_LIMITED";
        }
        else if (result == EdkErrorCode.EDK_UNKNOWN_ERROR.ToInt()){
        	return "AuthorizeLicense: EDK_UNKNOWN_ERROR";
        }
        
        if (!(result == EdkErrorCode.EDK_OK.ToInt() || result == EdkErrorCode.EDK_LICENSE_REGISTERED.ToInt()))
            return "Adding sessions failed";
     
        return sessions + " sessions added";
	}
	
	public String getLicenseInfo() {
		
        Edk.LicenseInfos_t.ByReference licenseInfos = new Edk.LicenseInfos_t.ByReference();
        
        //Get License Information
        int result = Edk.INSTANCE.IEE_LicenseInformation(licenseInfos);
        if(result == EdkErrorCode.EDK_OK.ToInt())
        {
        	//Convert to Epoc time to Date time UTC
            String date_from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(licenseInfos.date_from*1000L)); //multiple 1000 because convert to milisecond
            String date_to = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(licenseInfos.date_to*1000L));
            String soft_limit_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(licenseInfos.soft_limit_date*1000L));
            String hard_limit_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(licenseInfos.hard_limit_date*1000L));

            String output = "From date              : " + date_from
            + "\nTo   date              : " + date_to
            + "\nNumber of seats        : " + licenseInfos.seat_count
            + "\nTotal quotas           : " + licenseInfos.quota
            + "\nTotal used quotas      : " + licenseInfos.usedQuota
            + "\nGrace Period      from : " + soft_limit_date  + " to    " + hard_limit_date;

            if(licenseInfos.scopes == 1){
                output += "\nLicense type       : " + "EEG";
            }
            else if (licenseInfos.scopes == 2){
            	output += "\nLicense type      : " + "PM";
            }
            else if (licenseInfos.scopes == 3){
            	output += "\nLicense type       : " + "EEG + PM";
            }
            else{
            	output += "\nLicense type       : " + "No type";
            }
            return output;
        }
        else if (result == EdkErrorCode.EDK_LICENSE_EXPIRED.ToInt()){
           return "LicenseInformation: EDK_LICENSE_EXPIRED";
        }
        else if (result == EdkErrorCode.EDK_OVER_QUOTA.ToInt()){
            return "LicenseInformation: EDK_OVER_QUOTA";
        }
        else if (result == EdkErrorCode.EDK_ACCESS_DENIED.ToInt()){
           return "LicenseInformation: EDK_ACCESS_DENIED";
        }
        else if (result == EdkErrorCode.EDK_LICENSE_ERROR.ToInt()){
            return "LicenseInformation: EDK_LICENSE_ERROR";
        }
        else if (result == EdkErrorCode.EDK_NO_ACTIVE_LICENSE.ToInt()){
            return "LicenseInformation: EDK_NO_ACTIVE_LICENSE";
        }
        else{
            return "Unknown Error";
        }
	}
	
	public void disconnect() {
		Edk.INSTANCE.IEE_EngineDisconnect();
	}

	public static void main(String[] args) {
		EmotivLicenseActivator ela = new EmotivLicenseActivator("", "");
		System.out.println(ela.login());
		System.out.println(ela.getSessionsInfo());
		System.out.println(ela.addSessions(1));
		System.out.println(ela.getLicenseInfo());
	}
}
