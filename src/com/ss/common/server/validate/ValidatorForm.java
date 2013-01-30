package com.ss.common.server.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ss.common.gwt.validate.shared.ValidatorResult;

public class ValidatorForm {

	private List<ValidatorField<?>> fields = new ArrayList<ValidatorField<?>>();
	private Set<String> keys = new HashSet<String>();

	public ValidatorForm(List<ValidatorField<?>> fields) {
		this.fields = fields;
	}

	public ValidatorForm() {

	}

	public ValidatorForm addField(ValidatorField<?> field) {
		if (field == null) {
			return this;
		}
		if (keys.contains(field.getFieldKey())) {
			throw new IllegalArgumentException("Form already contains field with key " + field.getFieldKey());
		}
		keys.add(field.getFieldKey());
		fields.add(field);
		return this;
	}

	public Map<String, List<ValidatorResult>> validate() {
		Map<String, List<ValidatorResult>> allErrors = new HashMap<String, List<ValidatorResult>>();
		for (ValidatorField<?> f : fields) {
			List<ValidatorResult> errors = f.validate();
			if (errors != null && errors.size() > 0) {
				allErrors.put(f.getFieldKey(), errors);
			}
		}
		return allErrors;
	}

}
