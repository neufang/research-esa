package edu.uka.aifb.concept.wikipedia;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

import edu.kit.aifb.ConfigurationManager;
import edu.kit.aifb.concept.IConceptIndex;
import edu.kit.aifb.concept.IConceptIterator;
import edu.kit.aifb.concept.IConceptVector;
import edu.kit.aifb.concept.IConceptVectorMapper;
import edu.kit.aifb.concept.TroveConceptVector;
import edu.kit.aifb.nlp.Language;
import edu.kit.aifb.wikipedia.sql.ILanglinksMap;
import edu.kit.aifb.wikipedia.sql.LanglinksApiLanglinksMap;
import edu.kit.aifb.wikipedia.sql.WikipediaCollection;

public class LanglinksConceptVectorMapper implements IConceptVectorMapper {

	static final String[] REQUIRED_PROPERTIES = {
	};
	
	static Logger logger = Logger.getLogger( LanglinksConceptVectorMapper.class );
	
	private int[] m_idMapping;
	
	public LanglinksConceptVectorMapper(
			Configuration config,
			IConceptIndex sourceIndex, Language sourceLanguage,
			IConceptIndex targetIndex, Language targetLanguage ) throws Exception
			{
		ConfigurationManager.checkProperties( config, REQUIRED_PROPERTIES );
		logger.info( "Initializing langlinks concept vector mapper (" + sourceLanguage + " to " + targetLanguage + ")" );
		
		ILanglinksMap langlinksMap = new LanglinksApiLanglinksMap(
				config,
				sourceLanguage, targetLanguage );
		
		m_idMapping = new int[sourceIndex.size()];
		for( int sourceConceptId=0; sourceConceptId<sourceIndex.size(); sourceConceptId++ )
		{
			if( logger.isDebugEnabled() && sourceConceptId % 100 == 0 ) {
				logger.debug( "Loading mapping ... (" + sourceConceptId + " of " + sourceIndex.size() + ")" );
			}

			int sourceArticleId = WikipediaCollection.buildArticleId( sourceIndex.getConceptName( sourceConceptId ) );
			int targetArticleId = langlinksMap.map( sourceArticleId );
 			
			String targetArticleConceptName = WikipediaCollection.getArticleName( targetArticleId );
			int targetConceptId = targetIndex.getConceptId( targetArticleConceptName );
			
			m_idMapping[sourceConceptId] = targetConceptId;
		}
		
	}
	
	public IConceptVector map( IConceptVector cv ) {
		logger.debug( "Mapping concept vector for document " + cv.getData().getDocName() );
		
		IConceptVector mappedCv = new TroveConceptVector( cv.getData().getDocName(), cv.size() );
		
		IConceptIterator it = cv.iterator();
		while( it.next() )
		{
			mappedCv.set( m_idMapping[it.getId()], it.getValue() );
		}

		return mappedCv;
	}
	
}
