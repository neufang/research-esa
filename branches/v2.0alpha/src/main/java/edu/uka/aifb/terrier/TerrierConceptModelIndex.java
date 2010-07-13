package edu.uka.aifb.terrier;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import edu.kit.aifb.concept.IConceptExtractor;
import edu.kit.aifb.terrier.concept.TerrierESAIndex;
import edu.uka.aifb.nlp.Language;

public class TerrierConceptModelIndex extends TerrierESAIndex {

	static {
		logger = Logger.getLogger( TerrierConceptModelIndex.class );
	}
		
	public TerrierConceptModelIndex (Configuration config, String indexId,
			Language language ) {
		super(config, indexId, language);
	}

	public IConceptExtractor getConceptExtractor() {
		try {
			IConceptExtractor extractor = new TerrierConceptModelExtractor( m_config, index, language );
			return extractor;
		}
		catch( Exception e ) {
			logger.error( e );
			return null;
		}
	}

}
