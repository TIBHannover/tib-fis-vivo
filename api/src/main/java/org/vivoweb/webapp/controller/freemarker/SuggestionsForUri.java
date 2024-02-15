package org.vivoweb.webapp.controller.freemarker;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.rdfservice.RDFService;
import edu.cornell.mannlib.vitro.webapp.rdfservice.impl.RDFServiceUtils;

public class SuggestionsForUri {

	public static final String VIVO_REJECTED_WORK = "http://vivoweb.org/ontology/core#rejectedWork";
	// Query to find all VCARDs attached as authors to publications
	private static final String SUGGESTED_WORKS_QUERY = ""
			+ "PREFIX vcard: <http://www.w3.org/2006/vcard/ns#>\n"
			+ "PREFIX core: <http://vivoweb.org/ontology/core#>\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
			+ "PREFIX obo: <http://purl.obolibrary.org/obo/>\n"
			+ "SELECT DISTINCT ?entityUri WHERE {\n"
			+ "  VALUES ?profileUriValue { ?profileUri  }\n"
			+ "  ?entityUri a <http://purl.obolibrary.org/obo/IAO_0000030> .\n"
			+ "  ?entityUri <http://vivoweb.org/ontology/core#relatedBy> ?relationshipUri .\n"
			+ "  ?relationshipUri <http://vivoweb.org/ontology/core#relates> ?vcardUri .\n"
			+ "  NOT EXISTS {\n"
			+ "    GRAPH <http://vitro.mannlib.cornell.edu/default/user-profile-suggestions> {\n"
			+ "      ?profileUriValue core:rejectedWork ?entityUri . \n"
			+ "    }\n"
			+ "}\n"
			+ "  NOT EXISTS {\n"
			+ "  ?entityUri a <http://purl.obolibrary.org/obo/ARG_2000379> .\n"
			+ "}\n"
			+ "  NOT EXISTS {\n"
			+ "  ?relationshipUri <http://vivoweb.org/ontology/core#relates> ?profileUriValue .\n"
			+ "}\n"
			+ "	 NOT EXISTS {\n"
			+ "    GRAPH <http://vitro.mannlib.cornell.edu/default/user-profile-suggestions> \n" + "  {\n"
			+ "      ?profileUriValue core:rejectedWork ?entityUri .\n" 
			+ "    }\n"
			+ "  }\n" 
			+ "  ?vcardUri a <http://www.w3.org/2006/vcard/ns#Individual> .\n"
			+ "  ?vcardUri <http://www.w3.org/2006/vcard/ns#hasName> ?nameUri .\n"
			+ "  {\n"
			+ "  ?profileUriValue foaf:lastName ?lastName .\n"
			+ "  BIND ( ?lastName AS ?profileLabel )\n"
			+ "}\n"
			+ "  UNION\n"
			+ "  {\n"
			+ "  ?profileUriValue obo:ARG_2000028 ?profileVCard .\n"
			+ "  ?profileVCard vcard:hasName ?vCardName .\n"
			+ "  ?vCardName vcard:familyName ?lastName .\n"
			+ "  BIND (?lastName AS ?profileLabel )\n"
			+ "} \n"
			+ "  UNION\n"
			+ "  {\n"
			+ "  ?profileUriValue rdfs:label ?profileLabel_lit .\n"
			+ "  BIND ( ?profileLabel_lit as ?profileLabel)\n"
			+ "}\n"
			+ "  ?nameUri <http://www.w3.org/2006/vcard/ns#familyName> ?familyName .\n"
			+ "  FILTER (STRSTARTS(LCASE(?profileLabel), LCASE(?familyName)))\n"
			+ "}";

	public static String[] getSuggestedWorks(VitroRequest vreq, String profileUri) {
		if (StringUtils.isEmpty(profileUri)) {
			return new String[0];
		}
		Set<String> suggestedWorks = querySuggestedWorks(vreq, profileUri);
		return suggestedWorks.stream().toArray(String[]::new);
	}

	private static Set<String> querySuggestedWorks(VitroRequest vreq, String profileUri) {
		RDFService contentRdfService = vreq.getUnfilteredRDFService();
		return queryWorks(contentRdfService, SUGGESTED_WORKS_QUERY, profileUri);
	}

	private static Set<String> queryWorks(RDFService rdfService, String query, String profileUri) {
		Set<String> works = new HashSet<String>();
		ParameterizedSparqlString pss = new ParameterizedSparqlString(query);
		pss.setIri("profileUri", profileUri);
		ResultSet results = RDFServiceUtils.sparqlSelectQuery(pss.toString(), rdfService);
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			RDFNode uri = qs.get("entityUri");
			if (uri != null && uri.isURIResource()) {
				works.add(uri.asResource().getURI());
			}
		}
		return works;
	}
}