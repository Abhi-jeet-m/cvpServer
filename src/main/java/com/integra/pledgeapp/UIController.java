package com.integra.pledgeapp;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@CrossOrigin(origins = "*")
public class UIController implements ErrorController {

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP")
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/EMPLOYEE")
	public String employeePage() {
		return "employee";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/HRD")
	public String hrdPage() {
		return "hrd";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/SIGN")
	public String signPage() {
		return "sign";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/TERMS")
	public String termsPage() {
		return "terms";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/DOWNLOAD")
	public String downloadPage() {
		return "download";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/HRDDOWNLOAD")
	public String hrDownload() {
		return "hrdownload";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/LIST")
	public String empList() {
		return "list";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/INSTRUCTIONS")
	public String instructions() {
		return "instructions";
	}
	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/RESPONSE")
	public String response() {
		return "response";
	}

	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/PLEDGEINFO")
	public String pledgeinfo() {
		return "pledgeinfo";
	}
	@RequestMapping(value = "/CORE_VALUE_PLEDGE_APP/error")
	public String handleError() {
		return "error";
	}

//	@Override
	public String getErrorPath() {
		return "error";
	}
}