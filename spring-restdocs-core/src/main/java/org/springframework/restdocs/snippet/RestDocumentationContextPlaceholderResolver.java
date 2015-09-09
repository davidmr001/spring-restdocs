/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.snippet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

/**
 * A {@link PlaceholderResolver} that resolves placeholders using a
 * {@link RestDocumentationContext}. The following placeholders are supported:
 * <ul>
 * <li>{@code step} – the {@link RestDocumentationContext#getStepCount() step current
 * count}.
 * <li>{@code methodName} - the name of the
 * {@link RestDocumentationContext#getTestMethodName() current test method} formatted
 * using camelCase
 * <li>{@code method-name} - the name of the
 * {@link RestDocumentationContext#getTestMethodName() current test method} formatted
 * using kebab-case
 * <li>{@code method_name} - the name of the
 * {@link RestDocumentationContext#getTestMethodName() current test method} formatted
 * using snake_case
 * </ul>
 * 
 * @author Andy Wilkinson
 */
public class RestDocumentationContextPlaceholderResolver implements PlaceholderResolver {

	private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("([A-Z])");

	private final RestDocumentationContext context;

	/**
	 * Creates a new placeholder resolver that will resolve placeholders using the given
	 * {@code context}.
	 * 
	 * @param context the context to use
	 */
	public RestDocumentationContextPlaceholderResolver(RestDocumentationContext context) {
		this.context = context;
	}

	@Override
	public String resolvePlaceholder(String placeholderName) {
		if ("step".equals(placeholderName)) {
			return Integer.toString(this.context.getStepCount());
		}
		if ("methodName".equals(placeholderName)) {
			return this.context.getTestMethodName();
		}
		if ("method-name".equals(placeholderName)) {
			return camelCaseToKebabCase(this.context.getTestMethodName());
		}
		if ("method_name".equals(placeholderName)) {
			return camelCaseToSnakeCase(this.context.getTestMethodName());
		}
		return null;
	}

	/**
	 * Converts the given {@code string} from camelCase to kebab-case.
	 * 
	 * @param string the string
	 * @return the converted string
	 */
	protected final String camelCaseToKebabCase(String string) {
		return camelCaseToSeparator(string, "-");
	}

	/**
	 * Converts the given {@code string} from camelCase to snake_case.
	 * 
	 * @param string the string
	 * @return the converted string
	 */
	protected final String camelCaseToSnakeCase(String string) {
		return camelCaseToSeparator(string, "_");
	}

	/**
	 * Returns the {@link RestDocumentationContext} that should be used during placeholder
	 * resolution.
	 * 
	 * @return the context
	 */
	protected final RestDocumentationContext getContext() {
		return this.context;
	}

	private String camelCaseToSeparator(String string, String separator) {
		Matcher matcher = CAMEL_CASE_PATTERN.matcher(string);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(result, separator + matcher.group(1).toLowerCase());
		}
		matcher.appendTail(result);
		return result.toString();
	}

}
