/** DocumentVectorAnnotator.java
 * @author Weston Feely
 */
package edu.cmu.lti.f13.hw4.hw4_wfeely.annotators;

import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
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
    // construct a vector of tokens and update the tokenList in CAS
    // copy list elements into an arrayList
    FSList list = doc.getTokenList();
    ArrayList<Token> aList = new ArrayList<Token>();
    Token listTok = null;
    int i = 0;
    while (true) {
      try {
        listTok = (Token) list.getNthElement(i);
      } catch (Exception e) {
        // no more elements to process
        break;
      }
      aList.add(listTok);
      i++;
    }
    // loop through token strings in document
    for (String tokenText : docText.split(" ")) {
      // set up new token
      Token docTok = new Token(jcas);
      docTok.setText(tokenText);
      docTok.setFrequency(1);
      // loop through tokens in aList
      boolean match = false;
      for (Token aListTok : aList) {
        // compare document token with current token from arrayList aList
        if (docTok.getText() == aListTok.getText()) {
          // match; increment frequency of this type and set match to true
          aListTok.setFrequency(aListTok.getFrequency() + 1);
          match = true;
        }
      }
      // no match; add this new type to aList
      if (!match)
        aList.add(docTok);
    }
    // reset FSList list, by copying arrayList aList back into it
    FSList outList = new FSList(jcas);
    ((NonEmptyFSList) outList).setHead(aList.get(0));
    ((NonEmptyFSList) outList).setTail(null);
    for (int j=1; j < aList.size(); j++) {
      Token currentToken = aList.get(j);
      outList = ((NonEmptyFSList) outList).getTail();
      ((NonEmptyFSList) outList).setHead(currentToken);
    }
    doc.setTokenList(outList);
  }
}