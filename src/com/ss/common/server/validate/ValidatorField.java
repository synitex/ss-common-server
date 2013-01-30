package com.ss.common.server.validate;

import java.util.ArrayList;
import java.util.List;

import com.ss.common.gwt.validate.shared.Validator;
import com.ss.common.gwt.validate.shared.ValidatorResult;
import com.ss.common.gwt.validate.shared.ValidatorValueProvider;

public class ValidatorField<T> {

	private List<Validator<T>> validators = new ArrayList<Validator<T>>();
	private T value;
	private String fieldKey;

	public ValidatorField(String fieldKey, T value) {
		this.value = value;
		this.fieldKey = fieldKey;
	}

	public ValidatorField<T> addValidator(Validator<T> validator) {
		validators.add(validator);
		return this;
	}

	public ValidatorField<T> addValidators(List<Validator<T>> validators) {
		this.validators.addAll(validators);
		return this;
	}

	public List<ValidatorResult> validate() {
		List<ValidatorResult> errors = new ArrayList<ValidatorResult>();
		for (Validator<T> v : validators) {
			ValidatorResult error = v.validate(new ValidatorValueProvider<T>() {
				@Override
				public T getValue() {
					return value;
				}
			});
			if (error != null) {
				errors.add(error);
			}
		}
		return errors;
	}

	public String getFieldKey() {
		return fieldKey;
	}

}
