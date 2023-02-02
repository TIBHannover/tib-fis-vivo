package org.vivoweb.webapp.controller.freemarker;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Resource;
import org.vivoweb.webapp.createandlink.Citation;

public class SuggestedWorksResultSetConsumer extends NameResultSetConsumer {
	private Set<String> uris = new HashSet<>();
	private String authorStrLwr;

	SuggestedWorksResultSetConsumer(String authorStr) {
		super(null);
		authorStrLwr = authorStr.toLowerCase();
	}

	@Override
	protected void processQuerySolution(QuerySolution qs) {
		Resource entityUri = qs.contains("entityUri") ? qs.getResource("entityUri") : null;

		if (entityUri != null && !uris.contains(entityUri.getURI())) {
			name = new Citation.Name();
			super.processQuerySolution(qs);

			if (isAuthorMatch(name, authorStrLwr)) {
				uris.add(entityUri.getURI());
			}
		}
	}

	protected boolean isAuthorMatch(Citation.Name author, String authorStrLwr) {
		if (author != null && author.name != null) {
			String nameLwr = author.name.toLowerCase();
			if (nameLwr.startsWith(authorStrLwr) || authorStrLwr.startsWith(nameLwr)) {
				return true;
			}
		}

		return false;
	}

	public String[] getUris() {
		return uris.size() == 0 ? new String[0] : uris.toArray(new String[uris.size()]);
	}
}
