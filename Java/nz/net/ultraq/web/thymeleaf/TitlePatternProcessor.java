/*
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.ProcessorResult;

/**
 * Allows for greater control of the resulting &lt;title&gt; element by
 * specifying a pattern with some special tokens.  This can be used to extend
 * the decorator's title with the content's one, instead of simply overriding
 * it.
 * 
 * @author Emanuel Rabina
 */
public class TitlePatternProcessor extends AbstractProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TitlePatternProcessor.class);

	private static final String PARAM_TITLE_DECORATOR = "$DECORATOR_TITLE";
	private static final String PARAM_TITLE_CONTENT   = "$CONTENT_TITLE";

	static final String PROCESSOR_NAME_TITLEPATTERN      = "title-pattern";
	static final String PROCESSOR_NAME_TITLEPATTERN_FULL = LAYOUT_PREFIX + ":" + PROCESSOR_NAME_TITLEPATTERN;

	static final String DECORATOR_TITLE_NAME = "title-pattern::decorator-title";

	/**
	 * Constructor, sets this processor to work on the 'title-pattern' attribute.
	 */
	public TitlePatternProcessor() {

		super(PROCESSOR_NAME_TITLEPATTERN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPrecedence() {

		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Ensure this attribute is only on the <title> element
		if (!element.getNormalizedName().equals(HTML_ELEMENT_TITLE)) {
			logger.error("layout:title-pattern attribute should only appear in a <title> element");
			throw new IllegalArgumentException("layout:title-pattern attribute should only appear in a <title> element");
		}

		// Replace the <title> text with the expanded title pattern
		String titlepattern   = element.getAttributeValue(attributeName);
		String decoratortitle = (String)arguments.getLocalVariable(DECORATOR_TITLE_NAME);
		String contenttitle   = element.hasChildren() ? ((Text)element.getFirstChild()).getContent() : "";
		element.clearChildren();
		element.addChild(new Text(titlepattern
				.replace(PARAM_TITLE_DECORATOR, decoratortitle)
				.replace(PARAM_TITLE_CONTENT, contenttitle)));

		element.removeAttribute(attributeName);
		return ProcessorResult.OK;
	}
}
