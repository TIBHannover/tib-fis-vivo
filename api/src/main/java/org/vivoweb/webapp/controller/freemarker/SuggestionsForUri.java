package org.vivoweb.webapp.controller.freemarker;

import org.apache.commons.lang3.StringUtils;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFServiceException;

public class SuggestionsForUri {

	public static String[] getSuggestedWorks(VitroRequest vreq, String profileUri) {
		try {
			if (StringUtils.isEmpty(profileUri)) {
				return new String[0];
			}

			String authorStr = CreateAndLinkResourceController.getFormattedProfileName(vreq, profileUri);
			if (StringUtils.isEmpty(authorStr)) {
				return new String[0];
			}

			// Query to find all VCARDs attached as authors to publications
			String vcardAuthorQuery = "SELECT ?entityUri ?familyName ?givenName WHERE {\n"
					+ "    ?entityUri a <http://purl.obolibrary.org/obo/IAO_0000030> . \n"
					+ "    ?entityUri <http://vivoweb.org/ontology/core#relatedBy> ?relationshipUri .\n"
					+ "    ?relationshipUri <http://vivoweb.org/ontology/core#relates> ?vcardUri .\n"
					+ "    ?vcardUri a <http://www.w3.org/2006/vcard/ns#Individual> .\n"
					+ "    ?vcardUri <http://www.w3.org/2006/vcard/ns#hasName> ?nameUri .\n"
					+ "    OPTIONAL { ?nameUri <http://www.w3.org/2006/vcard/ns#familyName> ?familyName . }\n"
					+ "    OPTIONAL { ?nameUri <http://www.w3.org/2006/vcard/ns#givenName>  ?givenName . }\n"
					+ "    MINUS { ?entityUri a <http://purl.obolibrary.org/obo/ARG_2000379> . }\n" +
					// Remove relationships that are linked to the profile
					"    MINUS { ?relationshipUri <http://vivoweb.org/ontology/core#relates> <" + profileUri + "> . }\n"
					+ "}\n";

			// Process the query
			SuggestedWorksResultSetConsumer consumer = new SuggestedWorksResultSetConsumer(authorStr);
			// As we are just getting vcards to check against the author name, there is no
			// need for expensive language filtering
			vreq.getUnfilteredRDFService().sparqlSelectQuery(vcardAuthorQuery, consumer);
			return consumer.getUris();
		} catch (RDFServiceException e) {
		}

		return null;
	}
}