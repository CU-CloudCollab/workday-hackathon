package edu.cornell.hackathon.workday.api;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIResponse implements Serializable {

	private static final long serialVersionUID = 1311468739752345678L;

	private String serviceName;
	private Object data;

}
