/** DocumentVectorAnnotator.java
 * @author Weston Feely
 */
package edu.cmu.lti.f13.hw4.hw4_wfeely.annotators;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_wfeely.typesystems.Token;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> iter = jcas.getAnnotationIndex().iterator();
    if (iter.isValid()) {
      iter.moveToNext();
      Document doc = (Document) iter.get();
      createTermFreqVector(jcas, doc);
    }

  }

  /**
   * 
   * @param jcas
   * @param doc
   */
  private void createTermFreqVector(JCas jcas, Document doc) {

    String docText = doc.getText();
    // TODO: construct a vector of tokens and update the tokenList in CAS
    FSList list = doc.getTokenList();
    for (String tokenText : docText.split(" ")) {
      // set up token
      Token tok = null;
      tok.setText(tokenText);
      tok.setFrequency(1);
      // loop through tokens in tokenList
      boolean match = false;
      while (doc.getTokenList() instanceof NonEmptyFSList) {
        Token head = (Token) ((NonEmptyFSList) list).getHead();
        // compare each token in tokenList to tok
        if (head.getText() == tok.getText()) {
          // found a match; increment the frequency of this token, and break loop
          int freq = head.getFrequency() + 1;
          head.setFrequency(freq);
          match = true;
          list = ((NonEmptyFSList) list).getTail();
          break;
        }
        list = ((NonEmptyFSList) list).getTail();
      }
      if (!match) {
        // TODO: add token to FSList
      }
    }
  }
}