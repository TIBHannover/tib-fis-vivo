package org.vivoweb.webapp.controller.freemarker;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;
import org.vivoweb.webapp.createandlink.Citation;
import org.vivoweb.webapp.createandlink.CreateAndLinkUtils;

import edu.cornell.mannlib.vitro.webapp.rdfservice.ResultSetConsumer;

public class NameResultSetConsumer extends ResultSetConsumer {
	protected Citation.Name name;

	NameResultSetConsumer(Citation.Name name) {
		this.name = name;
	}

	@Override
	protected void processQuerySolution(QuerySolution qs) {
		// Get the name(s) from the result set
		Literal familyName = qs.contains("familyName") ? qs.getLiteral("familyName") : null;
		Literal givenName = qs.contains("givenName") ? qs.getLiteral("givenName") : null;

		if (StringUtils.isEmpty(name.name)) {
			// If we have a first / last name, create a formatted author string
			if (familyName != null) {
				if (givenName != null) {
					name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), givenName.getString());
				} else {
					name.name = CreateAndLinkUtils.formatAuthorString(familyName.getString(), null);
				}
			}
		}
	}
}
