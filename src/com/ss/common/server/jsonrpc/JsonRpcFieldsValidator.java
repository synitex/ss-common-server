package com.ss.common.server.jsonrpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ss.common.gwt.jsonrpc.shared.DataValidationDto;
import com.ss.common.gwt.validate.shared.Validator;
import com.ss.common.gwt.validate.shared.ValidatorResult;
import com.ss.common.server.validate.ValidatorField;
import com.ss.common.server.validate.ValidatorForm;

public class JsonRpcFieldsValidator {

	private ValidatorForm form;
	private String prefix;

	public JsonRpcFieldsValidator(String prefix) {
		this.prefix = prefix;
	}

	@SuppressWarnings("unchecked")
	public <T> JsonRpcFieldsValidator field(String fieldKey, T value, List<Validator<T>> validators) {
		return field(fieldKey, value, validators.toArray(new Validator[validators.size()]));
	}
	
	public <T> JsonRpcFieldsValidator field(String fieldKey, T value, Validator<T>... validators) {
		if(form == null) {
			form = new ValidatorForm();
		}

		ValidatorField<T> fld = new ValidatorField<T>(fieldKey, value);
		if (validators != null) {
			fld.addValidators(Arrays.asList(validators));
		}

		form.addField(fld);
		return this;
	}

	public void validate() throws JsonRpcDataValidationException {
		if(form ==null) {
			return;
		}
		Map<String, List<ValidatorResult>> validationResult = form.validate();
		if (validationResult == null || validationResult.size() == 0) {
			return;
		}
		List<DataValidationDto> allErrors = new ArrayList<DataValidationDto>();
		for (Entry<String, List<ValidatorResult>> en : validationResult.entrySet()) {
			List<ValidatorResult> errors = en.getValue();
			if (errors != null && errors.size() > 0) {
				for (ValidatorResult error : errors) {
					allErrors.add(new DataValidationDto(en.getKey(), error.getErrorKey()));
				}
			}
		}
		if (allErrors.size() > 0) {
			throw new JsonRpcDataValidationException(prefix, allErrors);
		}
	}
	
}
